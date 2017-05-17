package ca.thoughtwire.converttool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HarPostDataParam;

/**
 * The Class RegisterRequest, called by RegisterEntityParser,  manages the functions of parsing the participants registered info.
 */
public class RegisterRequest extends HarEntityParserBase
{
	
	/**
	 * Instantiates a new register request.
	 *
	 * @param harentry the harentry
	 */
	public RegisterRequest(HarEntry harentry)
	{
		this.harentry = harentry;
	}

	public String getUri()
	{
		if(uriName == null)
		{
			final HarPostDataParam uriPara =  filterPara(harentry,HarParameter.URIPARA.val());
			uriName = uriPara.getValue();
			
			try 
			{
				uriName = java.net.URLDecoder.decode(uriName,"UTF-8");
			} 
			catch (UnsupportedEncodingException e) 
			{
				logger.warn("The uri, "+uriName+" ,for participant registered cannot be properly decoded.");
			}
		}
		
		return uriName;
	}
	
	public String getPt()
	{
		if(ptVal == null)
		{
			ptVal = harentry.getResponse().getContent().getText();
		}
		return ptVal;
	}
	
	private String getBaseUri()
	{
		if(baseUri == null)
		{
			baseUri = getUri().substring(0, getUri().lastIndexOf('/')+1);
		}
		
		return baseUri;
	}
	
	private String getRegName()
	{
		if(regName == null)
		{
			regName = getUri().substring(getUri().lastIndexOf('/')+1);
		}
		
		return regName;
	}
	
	private String getEor()
	{
		if(eorValue == null)
		{
			final HarPostDataParam eorPara = filterPara(harentry,HarParameter.EORPARA.val());
			eorValue = eorPara.getValue();
		}

		return eorValue;
	}
	
	private List<HarPostDataParam> getCRList()
	{
		if(crParaList == null)
		{
			crParaList = filterListParas(harentry,HarParameter.CRPARA.val());
		}

		return crParaList;
	}
	
	/**
	 * Get the CRs list.
	 * Pass the CRs to the function of 'decideTypeOfCr()' for further steps.
	 * The final purpose is to parse the info of participant registered.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void addTextandCR() throws IOException 
	{	
		registerString = prototype.getRegisterString();
		
		for(HarPostDataParam crPara : getCRList())
		{
			String crsUri = crPara.getValue();
			
			try 
			{
				crsUri = java.net.URLDecoder.decode(crsUri,"UTF-8");
			}
			catch(UnsupportedEncodingException e) 
			{
				logger.warn(crsUri+" cannot be properly decoded.");
			}

			if(crsUri.startsWith(getBaseUri()))
			{
				decideTypeOfCr(crsUri);
			}
			else
			{
				logger.warn("The base uri, "+getBaseUri()+"does not support the CRs, "+crsUri+".  Therefore, they are not recorded.  Please fix it.");
			}
		  }
	}
	
	/**
	 * Decide the type of participants registered based on the format of CRs they have, parse the scripts, and store the CRs in different ArrayList.
	 *
	 * @param decodedCrs, the CRs extracted from HAR
	 */
	private void decideTypeOfCr(String decodedCrs)
	{
		final String crsName = decodedCrs.replace(uriName+"/", "");
		
		//direcotyInfo CR
		if(isDirCrs(decodedCrs))
		{
			regDirResult = getRegDirResult();
		}
		//semantic-form CR
		else if(isSFCrs(decodedCrs))
		{	
			//add CRs of SF to a list, crArr
			if(crSFArr.isEmpty())
			{
				//only replace the corresponding XML file once
				regSFResult = getRegSFResult(); 
			}
			//CRs should be formatted and put in scripts after all of CRs being obtained; therefore, they should be stored in corresponding ArrayList for now
			crSFArr.add(crsName);
		 }
		 //non semantic-form CR
		 else
		 {
			 //add CRs of non-SF to a list, crNonSFArr
			 if(crNonSFArr.isEmpty())
			 {
				//only replace the corresponding XML file once
				 regNonResult = getRegNonResult(crsName);
			 }
			//CRs should be formatted and put in scripts after all of CRs being obtained; therefore, they should be stored in corresponding ArrayList for now
			 crNonSFArr.add(crsName);
		 }
	}
	
	public String getRegDirResult()
	{
		String registerText = registerString.replace(TESTNAME, "|directory info| "+getRegName())
									 .replace(BASEURI, getBaseUri())
									 .replace(PARTICIPANTNAME, getRegName())
									 .replace(PTVAR, "") //token variable for participant registered with dir CR could be empty				
									 .replace(EORVAR, "true") //default value of "enable on register" for participant registered with dir CR should be true
									 .replace(CRSVAR,"DirectoryInfo"); //each participant registered with dir CR only have one CR, which is .../regName/DirectoryInfo
		return registerText;
	}
	
	private String getRegSFResult()
	{
		String registerText = registerString.replace(TESTNAME, "|directory info| |semantic fields| "+getRegName())
									 .replace(BASEURI, getBaseUri())
									 .replace(PARTICIPANTNAME, getRegName()) 
									 .replace(PTVAR, "regToken"+getRegName()) //token variable for participant registered with SF dir CRs should be regTokenREGNAME
									 .replace(EORVAR, getEor());
		return registerText;
	}
	
	private String getRegNonResult(String crsName)
	{
		String registerText = registerString.replace(TESTNAME, getRegName()+"/"+crsName)
						             .replace(BASEURI, getBaseUri())
						             .replace(PARTICIPANTNAME, getRegName())
						             .replace(PTVAR, "regToken"+getRegName()) //token variable for participant registered with non-SF dir CRs should be regTokenREGNAME
						             .replace(EORVAR, getEor());
		return registerText;
	}
	
	
	/**
	 * Format CRs and get the string of CRs.
	 * the format of CRs without base uri should be separated by comma like 'Fields/Grid/DirectoryInfo,Fields/Checkbox/DirectoryInfo,Fields/Link/DirectoryInfo'
	 * @param crArr, the list storing all of the CRs of some specific participant 
	 * @return the String of CRs, to be put in the script
	 */
	//
	private String getCrs(List<String> crArr)
	{
		StringBuffer crLongStr = new StringBuffer();
		
		for(String crStr : crArr)
		{
			crLongStr.append(crStr).append(',');
		 }
		crLongStr = crLongStr.deleteCharAt(crLongStr.length()-1);
		
		return crLongStr.toString();
	}
	
	/**
	 * Checks if is a CR of directoryInfo.
	 * if the uri's format is .../regName/DirectoryInfo, then the CR is dir CR.  The uri, .../regName/.../DirectoryInfo, is SF CR.
	 * @param decodedCrs the decoded CR
	 * @return true, if is dir CR
	 */
	private boolean isDirCrs(String decodedCrs)
	{
		//middleUri is to get the string after "regName" (participant's name).  If the CR is of only DirectoryInfo, middleUri should be "/DirectoryInfo" rather than sth other
		final String middleUri = decodedCrs.substring(decodedCrs.lastIndexOf(getRegName())+getRegName().length());
		return !decodedCrs.contains(URIELEMENT) && URIFOOT.equals(middleUri);
	}
	
	/**
	 * Checks if is a CR of SF directoryInfo.
	 * CR of SF should contains "/Fields" or if the uri which ends with "/DirectoryInfo" is not a dir CR, it is SF CR.
	 * @param decodedCrs the decoded CR
	 * @return true, if is SF CR
	 */
	private boolean isSFCrs(String decodedCrs)
	{
		return decodedCrs.contains(URIELEMENT) || decodedCrs.endsWith(URIFOOT);
	}
	
	public boolean isRegDirResult()
	{
		return regDirResult != null;
	}
	
	public boolean isRegSFResult()
	{
		return regSFResult != null;
	}
	
	public boolean isRegNonSFResult()
	{
		return regNonResult != null;
	}

	public String getSingleRegDirResult()
	{
		return regDirResult;
	}
	
	public String getSingleRegSFResult()
	{
		final String crSF = getCrs(crSFArr);
		regSFResult = regSFResult.replace(CRSVAR, crSF);
		return regSFResult;
	}
	
	public String getSingleNonResult()
	{
		 final String crNonSF = getCrs(crNonSFArr);
		 regNonResult = regNonResult.replace(CRSVAR, crNonSF);
		 return regNonResult;
	}
	
	private HarEntry harentry;
	private String uriName;
	private String eorValue;
	private List<HarPostDataParam> crParaList;
	private String baseUri;
	private String regName;
	private String ptVal;
	private String registerString ;
	
	/** The reg dir result, which stores the script of participant with CRs of directoryInfo */
	private String regDirResult; 
	/** The reg SF result, which stores the script of participant with CRs of semantic form */
	private String regSFResult; 
	/** The reg non result, which stores the script of participant with CRs of non-semantic form */
	private String regNonResult; 
	
	/** The cr SF arr, which store the names of CR of SF, without base uri */
	private List<String> crSFArr = new ArrayList<String>(); 
	
	/** The cr non SF arr, which stores the name of CR of non-SF, without base uri */
	private List<String> crNonSFArr = new ArrayList<String>(); 
	
	private static final String TESTNAME = "**INSERT NAME**";
	private static final String BASEURI = "**BaseURI**";
	private static final String PARTICIPANTNAME = "**ParticipantName**";
	private static final String PTVAR = "**Pt**";
	private static final String EORVAR = "**EOR**";
	private static final String CRSVAR = "**CRS**";
	
	private static final String URIFOOT = "/DirectoryInfo";
	private static final String URIELEMENT = "/Fields";
	
	private static final Logger logger = Logger.getLogger(RegisterRequest.class.getName());

}
