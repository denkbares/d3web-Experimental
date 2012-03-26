/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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

package de.knowwe.usersupport.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Cache for saving former results of earlier queries
 * for the {@link DialogComponent}.
 * 
 * @author Johannes Dienst
 * @created 26.03.2012
 *
 */
public class SuggestionCache
{
	private List<Map<String, ItemProperties>> formerSuggs;
	private static SuggestionCache unique;
	private static int formerCount = 5;
	
	private SuggestionCache()
	{
		this.formerSuggs = new ArrayList<Map<String, ItemProperties>>();
	}
	
	public static SuggestionCache getInstance()
	{
		if (unique == null)
			return new SuggestionCache();
		return unique;
	}
	
	/**
	 * 
	 * If a query has been stored returns the suggestions for it.
	 * Otherwise null.
	 * 
	 * @param query
	 * @param termDefinitions
	 * @return
	 */
	public List<Suggestion> getSuggestionsFromCache(String query, List<String> termDefinitions)
	{
		boolean cacheUpdated = this.updateCache(query, termDefinitions);
		if (cacheUpdated)
		{
			return this.getSuggestionList(query);
		}
		return null;
	}
	
	/**
	 * 
	 * Stores the results of a query.
	 * 
	 * @param query
	 * @param termDefinitions
	 * @param suggestions
	 */
	public void storeQueryResult(String query, List<String> termDefinitions, List<Suggestion> suggestions)
	{
		Map<String, ItemProperties> item = new HashMap<String, ItemProperties>();
		item.put(query, new ItemProperties(query, termDefinitions, suggestions));
		formerSuggs.add(0, item);
		
		if (formerSuggs.size() > formerCount)
			formerSuggs.remove(formerCount);
	}
	
	private List<Suggestion> getSuggestionList(String query)
	{
		for (Map<String, ItemProperties> item : formerSuggs)
		{
			if (item.containsKey(query)) return item.get(query).getSuggs();
		}
		return null;
	}
	
	private boolean updateCache(String query, List<String> termDefinitions)
	{	
		// remove all formerSuggs that are timed out (older than 1 minute)
		long current = System.currentTimeMillis();
		Map<String, ItemProperties> item = null;
		List<Integer> toRemove = new ArrayList<Integer>();
		for (int i = 0; i < formerSuggs.size(); i++)
		{		
			item = formerSuggs.get(i);
			Set<String> itemKeySet = item.keySet();
			String key = itemKeySet.iterator().next();
			if ( (current - item.get(key).getTimestamp()) > 60000)
			{
				toRemove.add(i);
			}
		}

		for (int j = toRemove.size()-1; j >= 0; j--)
		{
			formerSuggs.remove(j);
		}
		
		boolean updated = false;
		for (int i = 0; i < formerSuggs.size(); i++)
		{
			item = formerSuggs.get(i);
			if (item.containsKey(query))
			{
				ItemProperties iP = item.get(query);
				// TODO how to evaluate if termDefinitions are the same?
				if (iP.getTermDefSize() == termDefinitions.size())
				{
					iP = new ItemProperties(query, termDefinitions, iP.getSuggs());
					formerSuggs.remove(i);
					item = new HashMap<String, ItemProperties>();
					item.put(query, iP);
					formerSuggs.add(0, item);
					updated = true;
					break;
				}
			}
		}
		
		return updated;
	}
	
	private class ItemProperties
	{
		private long timestamp;
		private String query;
		private int termDefSize;
		private List<Suggestion> suggs;
		
		public ItemProperties(String query, List<String> termDefinitions, List<Suggestion> suggestions)
		{
			this.timestamp = System.currentTimeMillis();
			this.query = query;
			this.termDefSize = termDefinitions.size();
			this.suggs = suggestions;
		}
		
		public String getQuery() {
			return query;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public int getTermDefSize() {
			return termDefSize;
		}

		public List<Suggestion> getSuggs() {
			return suggs;
		}
	}
	
	
}
