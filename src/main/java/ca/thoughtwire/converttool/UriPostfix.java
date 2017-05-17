package ca.thoughtwire.converttool;

/**
* The Enum UriPostfix, which defines the constants of the postfix of URL
*/

public enum UriPostfix 
{
	REGISTER("agent-server/register"),
	COLLABORATION("agent-server/collaboration"),
	SHAREVALUE("agent-server/shareValues"),
	WAITINGREQUEST("agent-server/waitingRequests"),
	REQUESTNOTIFICATION("agent-server/requestNotification"),
	REMOVEVALUE("/removeValues"),
	UNREGISTER("/unregister")
	;
	
	public String val()
	{
		return s;
	}
	
	private UriPostfix(String s)
	{
		this.s = s;
	}
	
	private final String s;
}
