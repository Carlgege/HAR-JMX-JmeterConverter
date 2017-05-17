 package ca.thoughtwire.converttool;

/**
 * The Enum HarParameter, which defines the constants of the name of URL recorded in HAR file
 */
public enum HarParameter 
{
	CRPARA("crs"),
	EORPARA("eor"),
	URIPARA("uri"),
	CURIPARA("curi"),
	USERNAMEPARA("username"),
	PASSWORDPARA("password"),
	PTPARA("pt"),
	SIPARA("si"),
	SVPARA("sv"),
	POLLINGPARA("pollingStyle"),
	RFPARA("rf"),
	CRIDPARA("crid")
	;
	
	public String val()
	{
		return s;
	}
	
	private HarParameter(String s)
	{
		this.s = s;
	}
	
	private final String s;
}
