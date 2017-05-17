package ca.thoughtwire.converttool;

import java.io.IOException;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import de.sstoehr.harreader.HarReaderException;

// TODO: Auto-generated Javadoc
/**
 * HAR to JMeter converter used to generate a new JMeter test that can be loaded into JMeter and run successfully.
 * 
 * Using for command line:
 * The directory of the har file to be converted
 * -har /Users/yuanhui.cheng/Documents/har/vbox.dev.twamb.ca.har 
 * 
 * The directory of the jmx file to be generated (not required)
 * -jmx /Users/yuanhui.cheng/Documents/jmx/testplan.jmx
 * 
 * The default directory of the jmx file to be generated is /Users/yuanhui.cheng/Documents/har/vbox.dev.twamb.ca.jmx
 * @author yuanhui.cheng
 * @version 1.8
 * @since  2017-2-21
 */

public final class Builder 
{
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 * @throws HarReaderException the har reader exception
	 */
	public static void main(String[] args) throws IOException, ParseException, HarReaderException
	{	
		try 
		{
			CommandParser parser = new CommandParser(args);
			
			HarConverter converter = new HarConverter();
			converter.convertHar(parser.getHarPath(), parser.getJmxPath());
			
			System.exit(0);
		} 
		catch (CommandParserException e)
		{
			if(e.getLocalizedMessage() == null)
			{
				logger.error("The directory of the HAR file or JMX file is wrong.");
				
			}
			else
			{
				logger.error(e.getLocalizedMessage());
			}
			
			System.exit(-1);
		}
		
	}
	
	/**
	 * Instantiates a new builder.  Other programs would not use the object.
	 */
	private Builder()
	{ /* Do nothing */ }
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(Builder.class);

}
