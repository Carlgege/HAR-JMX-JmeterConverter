package ca.thoughtwire.converttool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import de.sstoehr.harreader.HarReader;
import de.sstoehr.harreader.HarReaderException;
import de.sstoehr.harreader.model.Har;
import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HarHeader;
import de.sstoehr.harreader.model.HarPostDataParam;

// TODO: Auto-generated Javadoc
/**
 * The Class HarConverter.
 */
public class HarConverter extends HarEntityParserBase
{
	
	/**
	 * Convert HAR to JMX.  Write the contents sorted into JMX files.
	 *
	 * @param harPath This is the first parameter indicating the path of HAR file
	 * @param jmxPath This is the second parameter indicating the path of JMX file
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws HarReaderException the har reader exception
	 */
	public void convertHar(final String harPath, final String jmxPath) throws IOException, HarReaderException
	{		
		final List<HarEntry> harEntries = getHarEntries(harPath);
		
		final List<String> jmxResult = new ArrayList<String>();
		
		jmxResult.add(readEnvironment(harEntries)); //header and Environment info
		jmxResult.add(prototype.getThreadString()); // thread info
		jmxResult.add(readLogin(harEntries)); //log in event
		jmxResult.addAll(readInfo(harEntries)); //participant registered, share values, idle waiting requests, waiting requests with notifications, unregistered participants, value removed
		jmxResult.add(prototype.getCollabDestroyString()); //destroy collaboration
		jmxResult.add(prototype.getLogoutString()); //log out
		jmxResult.add(prototype.getViewResultsTreeString()); //view result tree
		jmxResult.add(prototype.getFooterString()); //footer	
		 
		JmxWriter writer = new JmxWriter(jmxPath);
		
		writer.writeList(jmxResult);
		writer.closeBufferedWriter(); //close the buffered writer
		
		final String jmxName = jmxPath.substring(jmxPath.lastIndexOf('/')+1);
		logger.info(jmxName+" has been produced in the directory of "+ jmxPath);
	}


	/**
	 * Read info of TW environment.  Only read once for obtaining agent server.
	 *
	 * @param harEntries the har entries
	 * @return the string, the script of TW environment
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String readEnvironment(final List<HarEntry> harEntries) throws IOException
	{
		final String environmentString = prototype.getEnvironmentString();
		
		//get entry->request->header(which name equals "Host")
		final HarHeader harHeaderResult = harEntries.stream()
										  .flatMap(harEntry->harEntry.getRequest().getHeaders().stream())
										  .filter(harHeader->"Host".equals(harHeader.getName()))
										  .findFirst()
									  	  .orElse(null);	  		

		final String agentHost = harHeaderResult.getValue();
		final String hostResult = environmentString.replace(AGENTHOST, agentHost);

		return hostResult;
	}

	/**
	 * Read info of TW login.  Only read once for obtaining user name and password to login.
	 *
	 * @param harEntries the har entries
	 * @return the string, the script of TW Login
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String readLogin(final List<HarEntry> harEntries) throws IOException
	{
		final List<HarPostDataParam> postDataPara = harEntries.stream()
													.flatMap(harEntry->harEntry.getRequest().getPostData().getParams().stream())
													.collect(Collectors.toList());
		
		String loginResult = prototype.getLoginString();

		//get entry->request->post data->params(which name equals "username")
		HarPostDataParam postUsername = getLoginPara(postDataPara,HarParameter.USERNAMEPARA.val());

		//get entry->request->post data->params(which name equals "password")
		HarPostDataParam postPassword = getLoginPara(postDataPara,HarParameter.PASSWORDPARA.val());

		//usually, the user name and password are not recorded in HAR, which requires users to change them manually in JMeter.
		if(postUsername == null || postPassword == null)
		{
			logger.info("The username or password is not obtained from the har file." + "\n You should remember to add them in JMeter.");
		}
		else
		{
			final String userName = postUsername.getValue();
			final String passWord = postPassword.getValue();

			loginResult = loginResult.replace(USERNAME, userName)
									 .replace(PASSWORD, passWord);

		}
		
		return loginResult;
		
	}
	
	/**
	 * Read all of the specific actions recorded in HAR file.
	 *
	 * @param harEntries the har entries
	 * @return the list, including all of the scripts of specific actions
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private List<String> readInfo(final List<HarEntry> harEntries) throws IOException
	{
		Map<String,String> regTextNameMap = new HashMap<String,String>(); //used to store key - the token(pt) of participant registered, value - name of participant registered
		List<String> infoResult = new ArrayList<String>();
		
		//read parsed scripts of collaboration created
		CollaborationEntityParser collabEntity = new CollaborationEntityParser();
		
		//read parsed scripts of registers classified by different types of CRs
		RegisterEntityParser registerEntity = new RegisterEntityParser(regTextNameMap);

		//read parsed scripts of shared values
		ShareValueEntityParser shareEntity = new ShareValueEntityParser(regTextNameMap);

		//read parsed scripts of idle waiting requests and waiting requests with notifications
		WaitingRequestsEntityParser waitingRequestsEntity = new WaitingRequestsEntityParser(regTextNameMap);
		
		//read parsed scripts of removed values
		RemoveValueEntityParser removeValueEntity = new RemoveValueEntityParser(regTextNameMap);
		
		//read parsed scripts of unregistered participants 
		UnregisterEntityParser unregisterEntity = new UnregisterEntityParser(regTextNameMap);
		
		infoResult.addAll(collabEntity.parse(harEntries));
		infoResult.addAll(registerEntity.parse(harEntries));
		infoResult.addAll(shareEntity.parse(harEntries));
		infoResult.addAll(waitingRequestsEntity.parse(harEntries));
		infoResult.addAll(removeValueEntity.parse(harEntries));
		infoResult.addAll(unregisterEntity.parse(harEntries));
		
		return infoResult;
	}
	
	/**
	 * Gets the login parameter, which is username or password.
	 *
	 * @param postDataPara the post data para
	 * @param somename "username" or "password"
	 * @return the login parameter, the user name or password
	 */
	private HarPostDataParam getLoginPara(final List<HarPostDataParam> postDataPara, final String somename)
	{
		final HarPostDataParam postInfo = postDataPara.stream()
										  .filter(harParas->somename.equals(harParas.getName()))
										  .findFirst()
										  .orElse(null);
		
		return postInfo;
		
	}
	
	/**
	 * Gets the har entries.
	 *
	 * @param harPath the path of the HAR file
	 * @return the har entries
	 * @throws HarReaderException the har reader exception
	 */
	private List<HarEntry> getHarEntries(String harPath) throws HarReaderException
	{
		final HarReader harReader = new HarReader();
		Har har = null;
		//read .har file
		try 
		{
			har = harReader.readFromFile(new File(harPath));
		}
		catch (HarReaderException e) 
		{
			throw new HarReaderException(e);
		}
		
		return har.getLog().getEntries();
	}

	/** The Constant USERNAME. */
	private static final String USERNAME = "**UserName**";
	
	/** The Constant PASSWORD. */
	private static final String PASSWORD = "**PassWord**";
	
	/** The Constant AGENTHOST. */
	private static final String AGENTHOST = "**AgentHost**";

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(HarConverter.class.getName());
}
