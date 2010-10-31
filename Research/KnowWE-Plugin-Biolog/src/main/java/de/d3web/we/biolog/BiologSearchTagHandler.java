/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.we.biolog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.search.SearchTerm;
import de.d3web.we.search.SearchWordPreprocessor;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.wikiConnector.KnowWEUserContext;

/**
 * This tag embeds the biolog-search into a wiki page.
 * 
 * @author Jochen/Stefan
 */
public class BiologSearchTagHandler extends AbstractTagHandler {

	public BiologSearchTagHandler() {
		super("biologsearch");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, KnowWEUserContext userContext, Map<String, String> values) {
		String count = values.get("count");

		int number = 1;

		if (count != null) {
			try {
				number = Integer.parseInt(count);
			} catch (Exception e) {
				// not a valid number
			}
			
			if(number < 0) {
				number = 1;
			}
		}
		
		
		StringBuilder result = new StringBuilder();
		result.append(
				"<h2 style='color:#1D7D35;text-align:center;"
				+ "font-size:2.8em;font-weight:bold;'>BIOLOG Suche:</h2>");
		result.append("<div id=\"biologsearch-wrapper\" style=\"margin:auto;width:65%\">");
		result.append(renderTagCloud( "" ));
		
		result.append(
				"<div id=\"biologsearch-box\" style=\"margin-left:" +
				"auto;margin-right:auto;margin-top:20px;width:100%\">");
		result.append("<ul class=\"form\">"
			+ "<li><input type=\"text\" id=\"s\" name=\"s\" value=\"\" autocomplete=\"off\"/></li>"
			+ "<li><input type=\"button\" value=\"Suche\" id=\"searchsubmit\"/></li>"
			+ "</ul>");
		result.append("</div>");	
		result.append("</div>");
		result.append("<div id='biologsearch-result'> </div>");
		
		return result.toString();
	}
	/**
	 * Returns the TagCloud for the entered search term.
	 * @return
	 */
	public static String renderTagCloud( String query ){
		List<SearchTerm> terms = getTagCloudEntries(6, 14, query);
		
		// NOT SHUFFLING (Biolog wants alphabetical order)
		//Collections.shuffle( terms );
		
		StringBuilder string = new StringBuilder();
		string.append("<div id=\"biologsearch-tagcloud\">");
		
		if( terms.size() >= 36 ){
		    addLine(string, terms, 4, 40, 100);
		    addLine(string, terms, 8, 60, 100);
		    addLine(string, terms, terms.size()-12, 100, 100);
		    addLine(string, terms, 8, 60, 100);
		    addLine(string, terms, 4, 40, 100);
		} else {			
			addLine(string, terms, terms.size(), 60, 100);
		}
		string.append("</div>");
		return string.toString();
	}
	
	/**
	 * 
	 * @param string
	 * @param tmpTerms
	 * @param amount
	 * @param width
	 */
	private static void addLine(StringBuilder string, List<SearchTerm> tmpTerms,
			int amount, int width, int max){
	    string.append(
	    		"<div style=\"width:"
	    		+ width
	    		+ "%;text-align:center;margin-left:"
	    		+ ((max-width)/2)+"%\">");
	    addItem(string, tmpTerms, amount);
	    string.append("</div>\n");
	}
	
	/**
	 * 
	 * @param string
	 * @param tmpTerms
	 * @param amount
	 */
	private static void addItem(StringBuilder string, List<SearchTerm> tmpTerms, int amount){
		List<SearchTerm> added = new ArrayList<SearchTerm>();
		for( int i = 0; i < amount; i++ ){
			string.append(
					tagLink( tmpTerms.get(i).getTerm()
							, tmpTerms.get(i).getImportance() ));
			added.add( tmpTerms.get( i ) );
		}
		tmpTerms.removeAll( added );
	}
	
	/**
	 * Creates the links in the TagCloud within the BIOLOG search.
	 * @param term
	 * @param fontsize
	 * @return
	 */
	private static String tagLink(String term, double fontsize){
		if(term.contains(" ")) term = "\""+term+"\"";
		//text-decoration:underline;
		return "<span class=\"biolog-cloud-link\""
				+ " style=\"white-space:nowrap;font-size:"
				+ fontsize + "px; line-height:"+fontsize+"px\">"
				+ term + "</span> ";
	}
	
	/**
	 * Returns a HashMap of the tags and an integer, that can be used as
	 * font-size (scaled between minSize and maxSize).
	 * 
	 * @param minSize
	 * @param maxSize
	 * @return
	 */
	private static List<SearchTerm> getTagCloudEntries(int minSize,
			int maxSize, String query) {
		if (minSize > maxSize) {
			int t = minSize;
			minSize = maxSize;
			maxSize = t;
		}
		Collection<SearchTerm> weighted =
			SearchWordPreprocessor.getInstance().processForRecommendation(query);

		List<SearchTerm> sortedList = new ArrayList<SearchTerm>();
		
		sortedList.addAll(weighted);
		
		Collections.sort(sortedList, new AlphabeticalComparator());
		
		
		// SearchTerminologyHandler.getInstance().getRelevantSearchWords(query);
		List<SearchTerm> fontsize = new ArrayList<SearchTerm>(); //use SearchTerm list for font size storage
		
		float factor = maxSize - minSize;
		for (SearchTerm cur : sortedList) {
			fontsize.add(
					new SearchTerm(
							cur.getTerm(), (int) Math.round(
									minSize + (cur.getImportance() * factor))) );
		}
		return fontsize;
	}
	
	
	
}

class AlphabeticalComparator implements Comparator<SearchTerm> {


	@Override
	public int compare(SearchTerm o1, SearchTerm o2) {
		
		return o1.getTerm().compareTo(o2.getTerm());
	}

}
