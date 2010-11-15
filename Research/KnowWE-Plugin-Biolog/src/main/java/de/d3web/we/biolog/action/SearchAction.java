/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.d3web.we.biolog.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

import de.d3web.we.action.DeprecatedAbstractKnowWEAction;
import de.d3web.we.biolog.BiologSearchTagHandler;
import de.d3web.we.biolog.search.AnnotationsProvider;
import de.d3web.we.biolog.search.BibtexSearchProvider;
import de.d3web.we.biolog.search.EMLSearchProvider;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.search.GenericSearchResult;
import de.d3web.we.search.KnowWESearchProvider;
import de.d3web.we.search.MultiSearchEngine;
import de.d3web.we.search.SearchTerm;
import de.d3web.we.search.SearchWordPreprocessor;
import de.knowwe.tagging.TaggingMangler;

/**
 * @author Jochen
 *
 *         This action is called when someone hits the search-button in the
 *         biolog-search mask
 * @see BiologSearchTagHandler
 *
 */
public class SearchAction extends DeprecatedAbstractKnowWEAction {

	private static final String JSP_WIKI_SEARCH = "JSPWiki search";

	@Override
	public String perform(KnowWEParameterMap parameterMap) {

		String searchText = parameterMap.get("searchText");

		if (searchText == null || searchText.length() == 0) return "<p class=\"box error\">Please enter a search text!</p>";

		Collection<SearchTerm> expandedSearchTerms = SearchWordPreprocessor
				.getInstance().processForSearch(searchText);

		// retrieving search results from the MultiSearchEngine
		Map<String, Collection<GenericSearchResult>> results = MultiSearchEngine
				.getInstance().search(expandedSearchTerms, parameterMap);

		// this methods filters findings from freetext-search, which are also
		// found by other search-providers
		filterFreeTextFindings(results);

		// rendering results
		StringBuffer resultBuffy = new StringBuffer();

		resultBuffy.append("<h2>Suchergebnisse:</h2>");
		resultBuffy.append("f&uuml;r: " + StringEscapeUtils.escapeHtml(searchText) + " +(  ");

		// show keyword set after expansion
		for (SearchTerm searchTerm : expandedSearchTerms) {
			if (!searchText.contains(searchTerm.getTerm())) {
				resultBuffy.append(StringEscapeUtils.escapeHtml(searchTerm.getTerm()) + ", ");
			}
		}
		resultBuffy.deleteCharAt(resultBuffy.length() - 2);
		resultBuffy.append(")\n\n");

		List<String> searchProviderKeys = new ArrayList<String>();

		// Defines order of presentation
		searchProviderKeys.add(BibtexSearchProvider.getInstance().getID());
		searchProviderKeys.add(EMLSearchProvider.getInstance().getID());
		searchProviderKeys.add(TaggingMangler.getInstance().getID());
		searchProviderKeys.add(AnnotationsProvider.getInstance().getID());
		searchProviderKeys.add(JSP_WIKI_SEARCH);

		// render each result set
		for (String searchProviderID : searchProviderKeys) {
			KnowWESearchProvider provider = MultiSearchEngine.getInstance()
					.getProvider(searchProviderID);
			Collection<GenericSearchResult> localResults = results
					.get(searchProviderID);
			String verbalization = provider.getVerbalization(Locale
					.getDefault());

			resultBuffy.append("<div id=\"" + provider.getID()
					+ "\" class=\"panel\">");
			resultBuffy.append("<h3>" + verbalization + ": " + localResults.size() + " Treffer"
					+ "</h3>");
			resultBuffy.append("<div class=\"results\">");

			List<GenericSearchResult> resultList = new ArrayList<GenericSearchResult>();
			resultList.addAll(localResults);

			Collections.sort(resultList, new AlphabeticalComparator());

			// check whether searchprovider likes to render his results by
			// himself
			String rendered = provider.renderResults(resultList, searchText);
			if (rendered != null) {
				resultBuffy.append(rendered);
			}
			else {
				// if not use default rendering
				for (GenericSearchResult genericSearchResult : resultList) {
					resultBuffy
							.append("<strong>"
									+ genericSearchResult.getPagename()
									+ ": </strong>");
					if (genericSearchResult.getContexts().length > 0) {
						resultBuffy
								.append(genericSearchResult.getContexts()[0]);
					}
					else {
						resultBuffy.append("<no context>");
					}
					resultBuffy.append("<br />");
				}
				if (localResults.size() == 0) {
					// resultBuffy.append("<p class=\"box info\">no results</p>");
					resultBuffy.append("");
				}
			}
			resultBuffy.append("</div></div>");
		}

		return resultBuffy.toString();
	}

	/**
	 * this methods filters findings from freetext-search, which are also found
	 * by other search-providers
	 *
	 * @param results
	 */
	private void filterFreeTextFindings(
			Map<String, Collection<GenericSearchResult>> results) {
		if (results.get(JSP_WIKI_SEARCH) == null) return;

		Set<String> pages = new HashSet<String>();

		for (Entry<String, Collection<GenericSearchResult>> entry : results.entrySet()) {
			if (entry.getKey().equals(JSP_WIKI_SEARCH)) continue;
			for (GenericSearchResult genericSearchResult : entry.getValue()) {
				pages.add(genericSearchResult.getPagename());
			}
		}

		Collection<GenericSearchResult> toRemove = new HashSet<GenericSearchResult>();
		Collection<GenericSearchResult> freeTextFindings = results.get(JSP_WIKI_SEARCH);

		for (GenericSearchResult freeTextResult : freeTextFindings) {
			if (pages.contains(freeTextResult.getPagename())) {
				toRemove.add(freeTextResult);
			}
		}

		freeTextFindings.removeAll(toRemove);

	}

	class AlphabeticalComparator implements Comparator<GenericSearchResult> {

		@Override
		public int compare(GenericSearchResult o1, GenericSearchResult o2) {
			if (o1.getScore() != o2.getScore()) {
				if (o1.getScore() > o2.getScore()) {
					return -1;
				}
				if (o1.getScore() < o2.getScore()) {
					return 1;
				}
			}
			return o1.getPagename().compareTo(o2.getPagename());
		}

	}
}
