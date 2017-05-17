package ca.thoughtwire.converttool;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

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
 * The Class ShareValueEntityParser, which parses the info of SharedValue.
 */
public class ShareValueEntityParser extends HarEntityParserBase implements HarEntityParser
{		
	/**
	 * Instantiates a new ShareValueEntityParser.
	 *
	 * @param regTextNameMap, the map created and passed by RegisterEntityParser
	 */
	public ShareValueEntityParser(final Map<String,String> regTextNameMap)
	{
		this.regTextNameMap = regTextNameMap;
	}	

	/* (non-Javadoc)
	 * @see ca.thoughtwire.converttool.HarEntityParser#parse(java.util.List)
	 */
	@Override
	public List<String> parse(List<HarEntry> harEntries) throws IOException 
	{
		final List<HarEntry> shareEntries = filterEntries(harEntries,UriPostfix.SHAREVALUE.val());
		List<String> shareResult = new ArrayList<String>();
		final String shareString = prototype.getShareString();
		
		for(HarEntry harentry : shareEntries)
		{
			final HarPostDataParam siHarPara = filterPara(harentry,HarParameter.SIPARA.val());
			String shareID = siHarPara.getValue();
			
			try 
			{
				shareID = java.net.URLDecoder.decode(shareID,"UTF-8");
			} 
			catch (UnsupportedEncodingException e)
			{
				logger.warn("The share id "+shareID+" cannot be decoded");
			}
			
			final String shareTestName = shareID.substring(shareID.lastIndexOf('/')+1);
			
			final HarPostDataParam svHarPara = filterPara(harentry,HarParameter.SVPARA.val());
			String shareValue = svHarPara.getValue();
	
			try
			{
				shareValue = java.net.URLDecoder.decode(shareValue,"UTF-8");
			} 
			catch (UnsupportedEncodingException e) 
			{
				logger.warn("The share value "+shareValue+" cannot be decoded");
			}
			
			//all of the html elements like "/"" "<li> should be escaped to be shown in script of JMeter
			final String escapedDecodedShareValue = escapeHtml4(shareValue);
			
			final HarPostDataParam shareptHarPara = filterPara(harentry,HarParameter.PTPARA.val());
			final String sharePt = shareptHarPara.getValue();
			
			if(!shareID.isEmpty() && !shareValue.isEmpty() && !sharePt.isEmpty())
			{
				//look for the participant who shares the value by comparing the Pt(sharePt) and the Pt stored in the map
				final Entry<String, String> ptEntry = filterEntryOfMap(regTextNameMap,sharePt);
				//get the name of the participant
				final String partiShareName = ptEntry.getValue().substring(ptEntry.getValue().lastIndexOf("/")+1);
				final String singleShareResult = shareString.replace(TESTNAME,shareTestName)
						 				 			  .replace(SHAREID,shareID)
						 				 			  .replace(SHAREVALUE,escapedDecodedShareValue)
						 				 			  .replace(SHAREPT,"${regToken"+partiShareName+"}");
				shareResult.add(singleShareResult);
			}
		}
		
		return shareResult;
		
	}
	
	private Map<String,String> regTextNameMap = null;

	private static final String TESTNAME = "**INSERT NAME**";
	private static final String SHAREID = "**SHAREID**";
	private static final String SHAREVALUE = "**SHAREVALUES**";
	private static final String SHAREPT = "**SHAREPT**";

	private static final Logger logger = Logger.getLogger(ShareValueEntityParser.class.getName());
}
