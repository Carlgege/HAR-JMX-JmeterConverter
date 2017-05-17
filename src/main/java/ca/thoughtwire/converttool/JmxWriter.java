package ca.thoughtwire.converttool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * The Class JmxWriter, which defines functions for required behaviors of BufferedWriter
 */
public class JmxWriter 
{
	private BufferedWriter bw;
	
	/**
	 * Instantiates a new jmx writer.
	 *
	 * @param name, the name of JMX file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public JmxWriter(String name) throws IOException
	{
		File file = new File(name);
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				throw new IOException("The .jmx file can not be properly generated. The path is wrong.", e);
			}
		}
		
		FileWriter fw;
		
		try
		{
			fw = new FileWriter(file.getAbsoluteFile());
			this.bw = new BufferedWriter(fw);
		}
		catch (IOException e)
		{
			throw new IOException("The instance of .jmx file can not be properly generated.",e);
		}
	}
	
	/**
	 * Write list.
	 *
	 * @param textString the text string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void writeList(final List<String> textString) throws IOException
	{
		try 
		{
			for(String str : textString)
			{
				bw.newLine();
				bw.write(str);
			}
		} 
		catch (IOException e)
		{
			// Close the BW
			closeBufferedWriter();
			throw new IOException("List are not properly wrote to the JMX file.", e);
		}
		
	}
	
	/**
	 * Close buffered writer.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void closeBufferedWriter() throws IOException
	{
		try
		{
			bw.flush();
			bw.close();
		} 
		catch (IOException e) 
		{
			throw new IOException("The buffered writer cannot be properly closed.", e);
		}
	}
	
}
