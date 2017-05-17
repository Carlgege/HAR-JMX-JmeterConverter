package ca.thoughtwire.converttool;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The Class CommandParser.
 */
public class CommandParser 
{
	
	/**
	 * Instantiates a new command parser.
	 *
	 * @param args the args -har ... (required) , -jmx ... (not required)
	 * @throws ParseException the parse exception
	 * @throws CommandParserException the command parser exception
	 */
	public CommandParser(String[] args) throws ParseException, CommandParserException
	{
		if(args == null)
		{
			this.args = new String[0];
		}
		else
		{
			this.args = Arrays.copyOf(args, args.length);
		}
		
		initHarPath();
		initJmxPath();
	}
	
	/**
	 * Creates the options, hpath and jpath 
	 * Corresponding short single-character names of the options are har and jmx
	 * @return the options
	 */
	private Options createOptions()
	{
		Options options = new Options();
		options.addRequiredOption("har", CommandOption.HARPATH_ARG.val(), true, "The path of the har file is required");
		options.addOption("jmx",CommandOption.JMXPATH_ARG.val(), true, "The desired path of the produced jmx file, not required");
		
		return options;
	}
	
	/**
	 * Parses the commands.
	 *
	 * @param args the args
	 * @return the command line
	 * @throws ParseException the parse exception
	 */
	private CommandLine parseCommands( final String args[] ) throws ParseException
	{
		CommandLineParser parser = new DefaultParser();
		Options option = createOptions();
		return parser.parse(option, args);
	}
	
	/**
	 * Gets the har path.
	 *
	 * @return the har path
	 * @throws CommandParserException the command parser exception
	 */
	public String getHarPath() throws CommandParserException 
	{
		if(harPath == null)
		{
			initHarPath();
		}
		
		return harPath;
	}
	
	/**
	 * Gets the jmx path.
	 *
	 * @return the jmx path
	 * @throws CommandParserException the command parser exception
	 */
	public String getJmxPath() throws CommandParserException
	{
		if(jmxPath == null)
		{
			initJmxPath();
		}
		
		return jmxPath;
	}
	
	/**
	 * Prints the help.
	 */
	private void printHelp() 
	{
		Options option = createOptions();
		new HelpFormatter().printHelp("Har Converter",option);
	}
	
	/**
	 * Instantiates the har path.
	 *
	 * @throws CommandParserException the command parser exception
	 */
	private void initHarPath() throws CommandParserException
	{
		try 
		{
			CommandLine cmdline = parseCommands(args);
			harPath = cmdline.getOptionValue(CommandOption.HARPATH_ARG.val());
			
			File test = new File(harPath);
			if(!test.canRead())
			{
				throw new CommandParserException("Har not readable,possibly the directory is wrong.");
			}
			
		} 
		catch (ParseException e)
		{
			printHelp();
			throw new CommandParserException("The arguments are invalid or empty",e);
		}
	}
	
	/**
	 * Instantiates the jmx path.
	 *
	 * @throws CommandParserException the command parser exception
	 */
	private void initJmxPath() throws CommandParserException
	{
		try
		{
			CommandLine cmdline = parseCommands(args);
			
			//jmx path is not required
			if(cmdline.hasOption(CommandOption.JMXPATH_ARG.val()))
			{
				jmxPath = cmdline.getOptionValue(CommandOption.JMXPATH_ARG.val());
			}
			
			//keep the name and directory, only make sure the format of the output file is correct.
			//change .har to .jmx if user did not input the directory of output file
			if(jmxPath == null)
			{
				jmxPath = getHarPath().replace(getHarPath().substring(getHarPath().lastIndexOf('.')), ".jmx");
			}
			
			//add ".jmx" after the name of JMX if the format of the JMX file is incorrect.
			if(!jmxPath.endsWith(".jmx"))
			{
				jmxPath += ".jmx";
			}
			
			/*
			File test = new File(jmxPath);
			if(!test.canRead())
			{
				throw new CommandParserException("JMX not readable,possibly the directory is wrong.");
			}
			*/
			
		} 
		catch (ParseException e)
		{
			printHelp();
			throw new CommandParserException("The arguments are invalid or empty",e);
		}
	}
	
	private String harPath;
	private String jmxPath;
	
	private String[] args = null;
	
}
