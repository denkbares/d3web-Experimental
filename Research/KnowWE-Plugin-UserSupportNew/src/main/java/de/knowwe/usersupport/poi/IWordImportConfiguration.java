package de.knowwe.usersupport.poi;

import java.util.List;

/**
 * 
 * Interface for wordImportAction
 * 
 * @author Johannes Dienst
 * @created 13.04.2012
 */
public interface IWordImportConfiguration
{

	/**
	 * Converts html to wikisyntax
	 * 
	 * @param s
	 * @param docLines
	 * @return
	 */
	public String cleanHTMLLine(String s, List<String> docLines);
	
	/**
	 * 
	 * Creates WikiMarkup from List of Strings.
	 * 
	 * @param lines
	 * @return
	 */
	public String createWikiMarkup(List<String> lines);
	
}
