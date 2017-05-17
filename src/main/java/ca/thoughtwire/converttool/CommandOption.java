package ca.thoughtwire.converttool;

/**
 * The Enum CommandOption.
 */
public enum CommandOption
{
	HARPATH_ARG("hpath"),
	JMXPATH_ARG("jpath"),
	HELP_ARG("help")
	;
	
	public String val()
	{
		return s;
	}
	
	private CommandOption(String s)
	{
		this.s = s;
	}
	
	private final String s;
}
