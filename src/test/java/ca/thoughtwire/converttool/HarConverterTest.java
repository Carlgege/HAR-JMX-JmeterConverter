package ca.thoughtwire.converttool;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.sstoehr.harreader.HarReaderException;

public class HarConverterTest
{
	HarConverter converter = null;
	String harFile = "";
	String jmxFile = "";
	String expectedFile = "";
	
	@Before
	public void initialize() throws HarReaderException, IOException
	{
		converter = new HarConverter();
		harFile = "src/test/resources/twamb.har";
		jmxFile = "src/test/resources/actualTwamb.jmx";
		expectedFile = "src/test/resources/expectedTwamb.jmx";
		converter.convertHar(harFile, jmxFile);
	}
	
	@Test
	public void isHarConverterRight() throws IOException 
	{
		assertEquals("The files differ!", 
			    FileUtils.readFileToString(new File(expectedFile), "utf-8"), 
			    FileUtils.readFileToString(new File(jmxFile), "utf-8"));
	}
	
}
