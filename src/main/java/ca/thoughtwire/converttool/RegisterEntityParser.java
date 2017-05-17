package ca.thoughtwire.converttool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.sstoehr.harreader.model.HarEntry;

/**
 * The Class RegisterEntityParser for parsing the info of participants registered
 */
public class RegisterEntityParser extends HarEntityParserBase implements HarEntityParser
{

	/** The reg text name map. */
	private Map<String,String> regTextNameMap = null;
	
	/**
	 * Instantiates a new register entity parser.
	 *
	 * @param regTextNameMap the reg text name map
	 */
	public RegisterEntityParser(final Map<String,String> regTextNameMap)
	{
		this.regTextNameMap = regTextNameMap;
	}
	
	/* (non-Javadoc)
	 * @see ca.thoughtwire.converttool.HarEntityParser#parse(java.util.List)
	 */
	@Override
	public List<String> parse(final List<HarEntry> harEntries) throws IOException 
	{
		final List<HarEntry> registeEntries = filterEntries(harEntries,UriPostfix.REGISTER.val());
		final List<String> dirResult = new ArrayList<String>(); //used to store all participants registered info with dir CRs
		final List<String> sfResult = new ArrayList<String>(); //used to store all participants registered info with SF dir CRs
		final List<String> nonsfResult = new ArrayList<String>(); //used to store all participants registered info with non-SF CRs
		final List<String> registerResult = new ArrayList<String>();
		
		for(HarEntry harentry : registeEntries)
		{
			RegisterRequest register = new RegisterRequest(harentry); 

			//put -key the name and -value pt of participnat into the map,
			//which would be used for deciding which participant is involved in the other actions according to the token (pt)
			regTextNameMap.put(register.getPt(),register.getUri()); //token variable (pt) set by registers
			register.addTextandCR();
			
			//for sorting the scripts in the order of participants with dir CR, participants with SF CR, and participants with Non-SF CR
			if(register.isRegDirResult())
			{
				dirResult.add(register.getSingleRegDirResult());
			}
			else if(register.isRegSFResult())
			{
				sfResult.add(register.getSingleRegSFResult());
			}
			else
			{
				nonsfResult.add(register.getSingleNonResult());
			}
		}

		registerResult.addAll(dirResult);
		registerResult.addAll(sfResult);
		registerResult.addAll(nonsfResult);
		
		return registerResult;
	}
	
}
