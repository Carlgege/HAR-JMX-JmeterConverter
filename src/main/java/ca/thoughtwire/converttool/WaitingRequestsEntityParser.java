package ca.thoughtwire.converttool;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HarPostDataParam;

/**
 * The Class WaitingRequestsEntityParser, which parses the info waiting requests.
 */
public class WaitingRequestsEntityParser extends HarEntityParserBase implements HarEntityParser
{
	/**
	 * Instantiates a new waiting requests entity parser.
	 *
	 * @param regTextNameMap, the map created and passed by RegisterEntityParser
	 */
	public WaitingRequestsEntityParser(final Map<String,String> regTextNameMap)
	{
		this.regTextNameMap = regTextNameMap;
	}
	
	/* (non-Javadoc)
	 * Create the map for storing -key the crid of request notification -value the actual text of notification 
	 * @see ca.thoughtwire.converttool.HarEntityParser#parse(java.util.List)
	 */
	@Override
	public List<String> parse(List<HarEntry> harEntries) throws IOException
	{
		final List<HarEntry> waitingRequestEntries = filterEntries(harEntries,UriPostfix.WAITINGREQUEST.val());
		
		final Map<String,String> regTextCridMap = getRequestNotificationMap(harEntries);
		final int timeBoundary = 20000;
		
		final List<String> idleWaitingResult = getIdleWaitingRequestsList(waitingRequestEntries,timeBoundary);
		final List<String> notificationResult = getNotificationsList(waitingRequestEntries,regTextNameMap,regTextCridMap,timeBoundary);
		

		
		List<String> waitingRequestResult = new ArrayList<String>();
		waitingRequestResult.addAll(idleWaitingResult);
		waitingRequestResult.addAll(notificationResult);
		
		return waitingRequestResult;
		
	}
	
	/**
	 * Gets the idle waiting requests list.
	 *
	 * @param waitingRequestEntries the waiting request entries
	 * @param timeBoundary the time boundary
	 * @return the idle waiting requests list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private List<String> getIdleWaitingRequestsList(final List<HarEntry> waitingRequestEntries,final int timeBoundary) throws IOException
	{
		final List<String> idleWaitingResult = new ArrayList<String>();
		final String idleWaitingRequestsString = prototype.getIdleWaitingRequestString();

		//the duration of the waiting request must exceed 20 sec, which is 20000ms
		final List<HarEntry> idleWaitingRequestEntries = waitingRequestEntries.stream()
														.filter(harentry->harentry.getTime()>timeBoundary)
														.collect(Collectors.toList());

		idleWaitingRequestEntries.stream()
		.forEach
		(
				harentry->
				{
					final HarPostDataParam idlewaitingRequestPara = filterPara(harentry,HarParameter.POLLINGPARA.val());
					final String pollStyle = idlewaitingRequestPara.getName();
					final String singleIdleWaitingResult = idleWaitingRequestsString.replace(POLLSTYLE,pollStyle);
					idleWaitingResult.add(singleIdleWaitingResult);

				}
		);

		return idleWaitingResult;
		
	}
	
	/**
	 * Gets the waiting requests lists with notifications list.
	 *
	 * @param waitingRequestEntries the waiting request entries
	 * @param regTextNameMap, the map created and passed by RegisterEntityParser
	 * @param regTextCridMap the reg text crid map
	 * @param timeBoundary the time boundary
	 * @return the list of the script including waiting requests and notifications 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private List<String> getNotificationsList(final List<HarEntry> waitingRequestEntries,final Map<String,String> regTextNameMap,final Map<String,String> regTextCridMap,final int timeBoundary) throws IOException
	{
		final List<String> notificationResult = new ArrayList<String>();
		final String notificationString = prototype.getNotificationString();
		
		
		//the duration of the waiting request must not exceed 20 sec, which is 20000ms
		final List<HarEntry> notifyWaitingRequestEntries = waitingRequestEntries.stream()
														   .filter(harentry->harentry.getTime()<=timeBoundary)
														   .collect(Collectors.toList());
		notifyWaitingRequestEntries.stream()
		.forEach
		(
			harentry->
			{
				final HarPostDataParam rfPara = filterPara(harentry,HarParameter.RFPARA.val());
				final String rfValue = rfPara.getValue();
				final Long size = harentry.getResponse().getContent().getSize();
				String singleNotifResult = "";
		
				//the value of rf must be 'false' and size must be larger than 2, then start to create scripts
				if("false".equals(rfValue) && size>2)
				{
					/*
					 * reqText would look like: 
					 * { "part1" : ["notif1","notif2",...],
					 * 	 "part2" : ["notif1","notif2",...],
					 *   "part3" : ["notif1","notif2",...],
					 * }
					 * whihc represents the satisfied consume request.
					 */
					final String reqText = harentry.getResponse().getContent().getText();
			
					//split into single participant attached crids
					String[] textArr = reqText.split("],");
					
					for(String strText : textArr)
					{
						//split into left part(pt) and right part(crid)
						String[] splitArr = strText.split(":");
						
						//get the specific PT
						String partiName = splitArr[0].substring(splitArr[0].indexOf('\"')+1,splitArr[0].lastIndexOf('\"'));
						
						//split crids and store them in an array
						String[] notifArr = splitArr[1].split(",");
						
						//look for the participant whose CR is satisfied by comparing the Pt(partiName) and the Pt stored in the map
						Entry<String, String> textEntry = filterEntryOfMap(regTextNameMap,partiName);
						
						//get the uri of the participant
						String partiUri = textEntry.getValue();
						singleNotifResult = notificationString.replace(PARTICIPANTURI,partiUri);
				
						for(String strNotif : notifArr)
						{
							//get the specific crid
							final String notifId = strNotif.substring(strNotif.indexOf('\"')+1,strNotif.lastIndexOf('\"'));
							
							//look for the actual notification text by comparing the crid(notifId) and the crid stored in the map
							final Entry<String, String> textcridEntry = filterEntryOfMap(regTextCridMap,notifId);
					
							//all of the html elements like "/"" "<li> should be escaped to be shown in script of JMeter
							final String escapedCRs = escapeHtml4(textcridEntry.getValue());
					
							singleNotifResult = singleNotifResult.replace(NOTIFICATIONTEXT,escapedCRs); //need to be corrected
							
							//only requires one crid for each participant
							break;
				
						}
				
						notificationResult.add(singleNotifResult);
					}
				}
			}
		);	
		
		return notificationResult;
	}
	
	/**
	 * Gets the request notification map including -key crid, and -value actual notification text
	 *
	 * @param harEntries the har entries
	 * @return the request notification map
	 */
	private Map<String,String> getRequestNotificationMap(final List<HarEntry> harEntries)
	{
		final List<HarEntry> notifEntries = filterEntries(harEntries,UriPostfix.REQUESTNOTIFICATION.val());
		Map<String,String> regTextCridMap = new HashMap<String,String>();
		
		notifEntries.stream()
		.forEach
		(
			harentry->
			{
				List<HarPostDataParam> harParaList = harentry.getRequest().getPostData().getParams().stream()
													.filter(harpara->HarParameter.CRIDPARA.val().equals(harpara.getName()))
													.collect(Collectors.toList());
			
				for(HarPostDataParam harPara : harParaList)
				{
					String notifCrid = harPara.getValue();
					String notifText = harentry.getResponse().getContent().getText();
					regTextCridMap.put(notifCrid,notifText);
				}    
			}
		);
		
		return regTextCridMap;
		
	}
	
	private Map<String,String> regTextNameMap = null;

	private static final String NOTIFICATIONTEXT = "**NOTIFICATIONTEXT**";
	private static final String PARTICIPANTURI = "**PUTURI**";
	private static final String POLLSTYLE = "**STYLE**";
}
