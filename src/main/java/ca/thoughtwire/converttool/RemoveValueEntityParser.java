package ca.thoughtwire.converttool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HarPostDataParam;

/**
 * The Class RemoveValueEntityParser, which parses the info of RemovedValue.
 */
public class RemoveValueEntityParser extends HarEntityParserBase implements HarEntityParser
{
	/**
	 * Instantiates a new RemoveValueEntityParser
	 *
	 * @param regTextNameMap, the map created and passed by RegisterEntityParser
	 */
	public RemoveValueEntityParser(final Map<String,String> regTextNameMap)
	{
		this.regTextNameMap = regTextNameMap;
	}
	

	/* (non-Javadoc)
	 * @see ca.thoughtwire.converttool.HarEntityParser#parse(java.util.List)
	 */
	@Override
	public List<String> parse(List<HarEntry> harEntries) throws IOException
	{
		final List<HarEntry> removeValueEntries = filterEntries(harEntries,UriPostfix.REMOVEVALUE.val());
		final List<String> removeValueResult = new ArrayList<String>();
		final String removeValueString = prototype.getRemoveValueString();
		
		for(HarEntry harentry : removeValueEntries)
		{
			final HarPostDataParam removePtPara = filterPara(harentry,HarParameter.PTPARA.val());
			final String removePtValue = removePtPara.getValue();
			final HarPostDataParam removeSIPara = filterPara(harentry,HarParameter.SIPARA.val());
			String removeSI = removeSIPara.getValue();

			try 
			{
				removeSI = java.net.URLDecoder.decode(removeSI,"UTF-8");
			} 
			catch (UnsupportedEncodingException e) 
			{
				logger.warn("The share id (removed value) "+removeSI+" cannot be decoded.");
			}
			
			//look for the participant whose value is removed by comparing the Pt(removePtValue) and the Pt stored in the map
			final Entry<String, String> nameptEntry = filterEntryOfMap(regTextNameMap,removePtValue);
			//get the name of the participant 
			final String partiRemoveName = nameptEntry.getValue().substring(nameptEntry.getValue().lastIndexOf("/")+1);
			final String singleRemoveResult = removeValueString.replace(PTVAR, "${regToken"+partiRemoveName+"}")
														       .replace(SIVAR,removeSI); 
			  
			removeValueResult.add(singleRemoveResult);  
		} 		 
		
		return removeValueResult;
		
	}
	

	private Map<String,String> regTextNameMap = null;
	
	private static final String PTVAR = "**REMOVEVALUEPT**";
	private static final String SIVAR = "**REMOVESI**";
	
	private static final Logger logger = Logger.getLogger(RemoveValueEntityParser.class.getName());
}
