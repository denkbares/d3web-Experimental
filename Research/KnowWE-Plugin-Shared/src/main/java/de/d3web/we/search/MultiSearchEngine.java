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

package de.d3web.we.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.knowwe.core.user.UserContext;
import de.knowwe.plugin.Instantiation;
import de.knowwe.plugin.Plugins;
import de.knowwe.search.GenericSearchResult;
import de.knowwe.search.SearchProvider;
import de.knowwe.search.SearchTerm;

/**
 * KnowWEs MultiSearchEngine manages multiple SearchProviders. When a search
 * query comes in each of the providers can be asked.
 * 
 * @author Jochen
 * 
 */
public class MultiSearchEngine implements Instantiation {

	private static MultiSearchEngine instance;

	public static MultiSearchEngine getInstance() {
		return instance;
	}

	private final Map<String, SearchProvider> searchProvider = new HashMap<String, SearchProvider>();

	public Map<String, SearchProvider> getSearchProvider() {
		return searchProvider;
	}

	/**
	 * Add a searchProvider to this MultiSearchEngine. It will be called on
	 * {@link #search(Collection, KnowWEParameterMap)}
	 * 
	 * @created 16.09.2010
	 * @param p
	 */
	public void addProvider(SearchProvider p) {
		this.searchProvider.put(p.getID(), p);
	}

	/**
	 * looks up searchProvider for the passed id
	 * 
	 * @created 16.09.2010
	 * @param id
	 * @return
	 */
	public SearchProvider getProvider(String id) {
		if (id == null) return null;
		return searchProvider.get(id);
	}

	/**
	 * @param searchText
	 * @param map
	 * @return
	 */
	public Map<String, Collection<GenericSearchResult>> search(
			String searchText, UserContext context) {
		return search(SearchWordPreprocessor.getInstance().processForSearch(
				searchText), context);
	}

	/**
	 * 
	 * Calls search on all registered searchProviders and returns all results in
	 * a result map.
	 * 
	 * @created 16.09.2010
	 * @param terms
	 * @param map
	 * @return
	 */
	public Map<String, Collection<GenericSearchResult>> search(
			Collection<SearchTerm> terms, UserContext context) {

		Map<String, Collection<GenericSearchResult>> all = new HashMap<String, Collection<GenericSearchResult>>();

		Set<SearchTerm> searchSet = new HashSet<SearchTerm>();

		searchSet.addAll(terms);

		for (SearchProvider provider : searchProvider.values()) {
			Collection<GenericSearchResult> singleResultSet = provider.search(
					searchSet, context);
			all.put(provider.getID(), singleResultSet);
		}

		return all;

	}

	@Override
	public void init() {

		instance = this;

		// get all SearchProvider
		Extension[] exts = PluginManager.getInstance().getExtensions(
				Plugins.EXTENDED_PLUGIN_ID,
				Plugins.EXTENDED_POINT_SearchProvider);
		for (Extension extension : exts) {
			Object o = extension.getSingleton();
			if (o instanceof SearchProvider) {
				addProvider(((SearchProvider) o));
			}
		}

	}

}
