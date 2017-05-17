package ca.thoughtwire.converttool;

import java.io.IOException;
import java.util.List;

import de.sstoehr.harreader.model.HarEntry;

/**
 * The Interface HarEntityParser.
 */
public interface HarEntityParser 
{
	/**
	 * Parses the info recoded in HAR rile.
	 *
	 * @param harEntries the har entries
	 * @return the list storing specific scripts of actions
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	List<String> parse(final List<HarEntry> harEntries) throws IOException;
}
