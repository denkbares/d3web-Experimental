/*
 * Copyright (C) 2013 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.wisskont.searchbox;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

import com.ecyrd.jspwiki.SearchResult;
import com.ecyrd.jspwiki.WikiPage;

import de.knowwe.core.Environment;
import de.knowwe.jspwiki.JSPWikiConnector;
import de.knowwe.jspwiki.PluggedSearchResult;
import de.knowwe.jspwiki.SearchProvider;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.wisskont.ConceptMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 17.04.2013
 */
public class ConceptSearchProvider implements SearchProvider {

	@Override
	public List<? extends SearchResult> findResults(String query, int flags) {
		ArrayList<PluggedSearchResult> result = new ArrayList<PluggedSearchResult>();
		List<WikiPage> pages = new ArrayList<WikiPage>();
		String sparql = "SELECT ?x WHERE { ?x rdf:type <" + ConceptMarkup.WISSASS_CONCEPT + ">.}";
		QueryResultTable resultTable = Rdf2GoCore.getInstance().sparqlSelect(sparql);

		JSPWikiConnector connector = (JSPWikiConnector) Environment.getInstance().getWikiConnector();
		ClosableIterator<QueryRow> resultIterator = resultTable.iterator();
		while (resultIterator.hasNext()) {
			QueryRow conceptResult = resultIterator.next();
			Node value = conceptResult.getValue("x");

			String conceptName = getConceptName(value);
			if (conceptName.contains(query)) {
				WikiPage wikiPage = connector.getEngine().getPage(conceptName);
				if (wikiPage != null) {

					pages.add(wikiPage);
					addChildrenConcepts(value.asURI(), pages);
				}
				else {
					System.out.println("Wiki-Seite konnte nicht gefunden werden: " + conceptName);
				}
			}

		}
		for (WikiPage page : pages) {
			result.add(new PluggedSearchResult(page, 101, new String[] {}
					));
		}
		Collections.sort(result);
		return result;
	}

	/**
	 * 
	 * @created 17.04.2013
	 * @param value
	 * @return
	 */
	private String getConceptName(Node value) {
		String uriString = value.toString();
		String uriStringDecoded = null;
		try {
			uriStringDecoded = URLDecoder.decode(uriString, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String conceptName = uriStringDecoded.substring(uriStringDecoded.indexOf("=") + 1);
		return conceptName;
	}

	/**
	 * 
	 * @created 17.04.2013
	 * @param asURI
	 */
	private void addChildrenConcepts(URI concept, List<WikiPage> result) {
		String sparql = "SELECT ?x WHERE { ?x lns:unterkonzept <" + concept.toString() + ">.}";
		QueryResultTable resultTable = Rdf2GoCore.getInstance().sparqlSelect(sparql);

		JSPWikiConnector connector = (JSPWikiConnector) Environment.getInstance().getWikiConnector();
		ClosableIterator<QueryRow> resultIterator = resultTable.iterator();
		while (resultIterator.hasNext()) {
			QueryRow conceptResult = resultIterator.next();
			Node value = conceptResult.getValue("x");

			String conceptName = getConceptName(value);
			WikiPage wikiPage = connector.getEngine().getPage(conceptName);
			if (wikiPage != null) {
				if (!result.contains(wikiPage)) {
					result.add(wikiPage);
				}
			}
			else {
				System.out.println("Wiki-Seite konnte nicht gefunden werden: " + conceptName);
			}
		}

	}
}
