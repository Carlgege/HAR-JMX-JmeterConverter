package ca.thoughtwire.converttool;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
* The class HarPrototypes, which defines the function of getting the contents from the specific XML files under src/main/resources
*/
public class HarPrototypes
{
	private String environmentString;
	private String threadString;
	private String loginString ;
	private String collabString;
	private String registerString;
	private String shareString;
	private String idleWaitingRequestsString;
	private String notificationString;
	private String removeValueString;
	private String unregisterString;
	private String collabDestroyString;
	private String logoutString;
	private String footerString;
	private String viewResultsTreeString;
	
	public String getEnvironmentString() throws IOException
	{
		if(environmentString == null)
		{
			return convertToString(ENVIRONMENTFILE);
		}
		
		return environmentString;
	}
	
	public String getThreadString() throws IOException
	{
		if(threadString == null)
		{
			return convertToString(THREADFILE);
		}
		
		return threadString;
	}
		
	public String getLoginString() throws IOException
	{
		if(loginString == null)
		{
			return convertToString(LOGINFILE);
		}
		
		return loginString;
	}
	
	public String getCollaborationString() throws IOException
	{
		if(collabString == null)
		{
			return convertToString(COLLABFILE);
		}
		
		return collabString;
	}
	
	public String getRegisterString() throws IOException
	{
		if(registerString == null)
		{
			return convertToString(REGISTERFILE);
		}
		
		return registerString;
	}
	
	public String getShareString() throws IOException
	{
		if(shareString == null)
		{
			return convertToString(SHAREFILE);
		}
		
		return shareString;
	}
	
	public String getIdleWaitingRequestString() throws IOException
	{
		if(idleWaitingRequestsString == null)
		{
			return convertToString(WAITINGREQUESTSFILE);
		}
		
		return idleWaitingRequestsString;
	}
	
	public String getNotificationString() throws IOException
	{
		if(notificationString == null)
		{
			return convertToString(NOTIFICATIONFILE);
		}
		
		return notificationString;
	}
	
	public String getUnregisterString() throws IOException
	{
		if(unregisterString == null)
		{
			return convertToString(UNREGISTERFILE);
		}
		
		return unregisterString;
	}
	
	public String getRemoveValueString() throws IOException
	{
		if(removeValueString == null)
		{
			return convertToString(REMOVEVALUEFILE);
		}
		
		return removeValueString;
	}
	
	public String getCollabDestroyString() throws IOException
	{
		if(collabDestroyString == null)
		{
			return convertToString(COLLABDESTROYFILE);
		}
		
		return collabDestroyString;
	}
	
	public String getLogoutString() throws IOException
	{
		if(logoutString == null)
		{
			return convertToString(LOGOUTFILE);
		}
		
		return logoutString;
	}
	
	public String getFooterString() throws IOException
	{
		if(footerString == null)
		{
			return convertToString(FOOTERFILE);
		}
		
		return footerString;
	}
	
	public String getViewResultsTreeString() throws IOException
	{
		if(viewResultsTreeString == null)
		{
			return convertToString(VIEWRESULTFILE);
		}
		
		return viewResultsTreeString;
	}
	
	
	
	private String convertToString(String file) throws IOException
	{
		final InputStream is = HarConverter.class.getResourceAsStream(file);
	    try 
	    {
			return IOUtils.toString(is, CHARENCODING);
		} 
	    catch (IOException e) 
	    {
	    	throw new IOException("The stream cannot be converted to string.",e);
		}
	}

	
	private static final String CHARENCODING = "UTF-8";
	private static final String ENVIRONMENTFILE = "/twEnvironment.xml";
	private static final String THREADFILE = "/threadGroup.xml";
	private static final String LOGINFILE = "/login.xml";
	private static final String COLLABFILE = "/collaboration.xml";
	private static final String REGISTERFILE = "/register.xml";
	private static final String SHAREFILE = "/share.xml";
	private static final String WAITINGREQUESTSFILE = "/waitingRequests.xml";
	private static final String NOTIFICATIONFILE = "/notification.xml";
	private static final String REMOVEVALUEFILE = "/removeValues.xml";
	private static final String UNREGISTERFILE = "/unregister.xml";
	private static final String COLLABDESTROYFILE = "/collaborationDestroy.xml";
	private static final String LOGOUTFILE = "/logout.xml";
	private static final String FOOTERFILE = "/footer.xml";
	private static final String VIEWRESULTFILE = "/viewResultsTree.xml";
	
}
