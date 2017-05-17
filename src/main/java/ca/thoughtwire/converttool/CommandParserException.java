package ca.thoughtwire.converttool;

/**
 * The Class CommandParserException to be thrown in the class of 'CommandParser', and caught in the class of 'Builder'.
 * The exception happens when the arguments are invalid or empty, or the directory of HAR or JMX specified is wrong.
 */
public class CommandParserException extends Exception
{
	
	/**
	 * Instantiates a new command parser exception.
	 *
	 * @param message the message
	 */
	CommandParserException(String message)
	{
		super(message);
	}
	
	/**
	 * Instantiates a new command parser exception.
	 *
	 * @param message the message
	 * @param e the e
	 */
	CommandParserException(String message, Throwable e)
	{
		super(message,e);
	}
}
