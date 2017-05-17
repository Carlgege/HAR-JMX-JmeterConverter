package ca.thoughtwire.converttool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HarPostDataParam;

/**
 * The Class UnregisterEntityParser, which parses the info of Unregistered participants.
 */
public class UnregisterEntityParser extends HarEntityParserBase implements HarEntityParser
{	
	/**
	 * Instantiates a new unregister entity parser.
	 *
	 * @param regTextNameMap, the map created and passed by RegisterEntityParser
	 */
	public UnregisterEntityParser(final Map<String,String> regTextNameMap)
	{
		this.regTextNameMap = regTextNameMap;
	}

	/* (non-Javadoc)
	 * @see ca.thoughtwire.converttool.HarEntityParser#parse(java.util.List)
	 */
	@Override
	public List<String> parse(List<HarEntry> harEntries) throws IOException 
	{
		final List<HarEntry> unregisterEntries = filterEntries(harEntries,UriPostfix.UNREGISTER.val());
		List<String> unregisterResult = new ArrayList<String>();
		final String unregisterString = prototype.getUnregisterString();
		
		unregisterEntries.stream()
		.forEach
		  (
				  harentry->
				  {
					  	final HarPostDataParam unregisPtPara = filterPara(harentry,HarParameter.PTPARA.val());
						final String unregisPtValue = unregisPtPara.getValue();
						//look for the participant who is unregistered by comparing the Pt(unregisPtValue) and the Pt stored in the map
						final Entry<String, String> nameptEntry = filterEntryOfMap(regTextNameMap,unregisPtValue);
						//get the name of the participant
						final String unregisName = nameptEntry.getValue().substring(nameptEntry.getValue().lastIndexOf("/")+1);
						final String singleUnregisResult = unregisterString.replace(PARTICIPANTNAME, unregisName)
																		   .replace(PTVAR,"${regToken"+unregisName+"}");
						unregisterResult.add(singleUnregisResult);
				  }	  
		  ); 
		
		return unregisterResult;
		
	}
	
	private Map<String,String> regTextNameMap = null;
	
	private static final String PARTICIPANTNAME = "**PARTICIPANTNAME**";
	private static final String PTVAR = "**UNREGISTERPT**";
}
