package ca.thoughtwire.converttool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HarPostDataParam;

/**
 * The Class CollaborationEntityParser for parsing the info of created collaboration 
 */
public class CollaborationEntityParser extends HarEntityParserBase implements HarEntityParser
{

	/* (non-Javadoc)
	 * @see ca.thoughtwire.converttool.HarEntityParser#parse(java.util.List)
	 */
	@Override
	public List<String> parse(List<HarEntry> harEntries) throws IOException
	{
		//get all of the HarEntries whose URL of request ends with "agent-server/collaboration"
		final List<HarEntry> collabEntries = filterEntries(harEntries,UriPostfix.COLLABORATION.val());
		
		//for storing all of the scripts of collaboration created in case more than one collaboration was created
		List<String> collabResult = new ArrayList<String>();
		final String collabString = prototype.getCollaborationString();
		
		for(HarEntry harentry : collabEntries)
		{
			//get the specific HarPostDataParam whose name equals with "curi" among the list of HarPostDataParam
			final HarPostDataParam collabParams = filterPara(harentry,HarParameter.CURIPARA.val());
			String collabName = collabParams.getValue();
			
			try
			{
				collabName = java.net.URLDecoder.decode(collabName,"UTF-8");
			}
			catch (UnsupportedEncodingException e) 
			{
				logger.warn("The collaboration uri, "+collabName+" cannot be decoded.");
			}
			
			final String singleCollabResult = collabString.replace(COLLABORATIONURI,collabName);
			collabResult.add(singleCollabResult);
		}
		
		return collabResult;
	}

	private static final String COLLABORATIONURI = "**COLLABORATION URI**";
	
	private static final Logger logger = Logger.getLogger(CollaborationEntityParser.class.getName());
}
