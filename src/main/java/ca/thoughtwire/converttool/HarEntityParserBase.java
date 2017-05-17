package ca.thoughtwire.converttool;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HarPostDataParam;

/**
 * The Class HarEntityParserBase.
 */
public abstract class HarEntityParserBase
{
	/**
	 * Filter entries to get all of the HarEntry whose URL of requests ends with 'urlname'
	 *
	 * @param harEntries the har entries
	 * @param urlname the urlname
	 * @return the list of HarEntry
	 */
	protected List<HarEntry> filterEntries(final List<HarEntry> harEntries,final String urlname)
	{
		final List<HarEntry> harEntryList = harEntries.stream()
			  										  .filter(harEntry->harEntry.getRequest().getUrl().endsWith(urlname))
			  										  .collect(Collectors.toList());
		return harEntryList;
		
	}
	
	/**
	 * Filter para. Only get one parameter whose name equals 'paraname'.
	 *
	 * @param harEntry the har entry
	 * @param paraname the paraname
	 * @return the specific HarPostDataParam whose name equals 'paraname'.
	 */
	protected HarPostDataParam filterPara(final HarEntry harEntry, final String paraname)
	{
		final List<HarPostDataParam> harParaList = harEntry.getRequest().getPostData().getParams();
		final HarPostDataParam harPara = harParaList.stream()
													.filter(harParam->harParam.getName().equals(paraname))
													.findFirst()
													.orElse(null);
		return harPara;
	}
	
	/**
	 * Filter list parameters. Get all parameters whose name equals 'paraname';
	 *
	 * @param harEntry the har entry
	 * @param paraname the paraname
	 * @return the list of HarPostDataParam whose name equals 'paraname';
	 */
	protected List<HarPostDataParam> filterListParas(final HarEntry harEntry, final String paraname)
	{
		final List<HarPostDataParam> harParaList = harEntry.getRequest().getPostData().getParams();
		final List<HarPostDataParam> finalHarParaList = harParaList.stream()
															       .filter(harParam->harParam.getName().equals(paraname))
																   .collect(Collectors.toList());
		return finalHarParaList;
	}
	
	/**
	 * Filter entry of map.
	 *
	 * @param mapname, some specific map
	 * @param valueCompared, the value to be compared
	 * @return the entry of map, whose value equals 'valueCompared'
	 */
	protected Entry<String,String> filterEntryOfMap(final Map<String,String> mapname,final String valueCompared)
	{
		final Entry<String, String> someEntry = mapname.entrySet()
													   .stream()
													   .filter(entry -> valueCompared.equals(entry.getKey()))
													   .findFirst()
													   .orElse(null);
		return someEntry;
	}
	
	/** The Constant prototype. */
	protected static final HarPrototypes prototype = new HarPrototypes();
}
