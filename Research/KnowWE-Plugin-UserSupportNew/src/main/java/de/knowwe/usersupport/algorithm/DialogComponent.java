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
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;


/**
 * 
 * Searches for Terms in the Terminology of a KnowledgeBase. Can handle
 * approximative String Matching algorithms.
 * 
 * All used Algorithms have to implement the {@link MatchingAlgorithm} Interface
 * to be fully plugable. Add them in the private Constroctur if you have
 * implemented a new one. TODO Find better way to plug new Algorithms
 * 
 * The DialogComponent is configurable over the ResourceBundle
 * <em>UserSupport_configuration</em>
 * 
 * @author Johannes Dienst
 * @created 04.10.2011
 */
public class DialogComponent
{

	private final List<MatchingAlgorithm> algorithmsToken = new ArrayList<MatchingAlgorithm>();
	private final List<MatchingAlgorithm> algorithmsPhrase = new ArrayList<MatchingAlgorithm>();

	private final ResourceBundle bundle = ResourceBundle.getBundle("UserSupport_configuration");
	private int maxSuggestions;
	private double threshold;

	private MatchingAlgorithm usedTokenAlgorithm;
	private MatchingAlgorithm usedPhraseAlgorithm;

	private static DialogComponent uniqueInstance;

	private SuggestionCache cache; 
	
	/**
	 * To avoid instantiation
	 */
	private DialogComponent()
	{
		cache = SuggestionCache.getInstance();
		
		maxSuggestions =
				Integer.parseInt(
						bundle.getString("usersupport.dialogcomponent.maxSuggestions"));
		threshold =
				Double.parseDouble(
						bundle.getString("usersupport.dialogcomponent.threshold"));

		try
		{
			String className1 =
					bundle.getString("usersupport.dialogcomponent.standardtokenmatchingalgorithm");
			usedTokenAlgorithm = MatchingAlgorithm.class.cast(
					Class.forName(className1
							));
			String className2 =
					bundle.getString("usersupport.dialogcomponent.standardphrasematchingalgorithm");
			usedPhraseAlgorithm = MatchingAlgorithm.class.cast(
					Class.forName(className2
							));
		}
		catch (Exception e)
		{
			usedTokenAlgorithm = new LevenshteinAlgorithm();
			usedPhraseAlgorithm = new MongeElkanAlgorithm();
		}

		// Add all known Algorithms
//		algorithmsToken.add(new DoubleMetaphoneAlgorithm());
		algorithmsToken.add(new JaroWinklerAlgorithm());
		algorithmsToken.add(new LevenshteinAlgorithm());
		algorithmsToken.add(new QGramAlgorithm());
		algorithmsToken.add(new RefinedSoundexAlgorithm());

		algorithmsPhrase.add(new JaccardAlgorithm());
		algorithmsPhrase.add(new MongeElkanAlgorithm());
		algorithmsPhrase.add(new SmithWatermanAlgorithm());
		algorithmsPhrase.add(new SortedWinklerAlgorithm());
//		algorithmsPhrase.add(new PermutedWinkler());
		algorithmsPhrase.add(new NeedlemanWunschAlgorithm());
	}

	/**
	 * Singleton.
	 * 
	 * @created 21.11.2011
	 * @return
	 */
	public static DialogComponent getInstance()
	{
		if (uniqueInstance == null) uniqueInstance = new DialogComponent();
		return uniqueInstance;
	}

	/**
	 * 
	 * Collects the best suggestions found by all MatchingAlgorithms
	 * in the terminology.
	 * 
	 * This is slow but will probably find the Best matches!
	 * 
	 * @created 21.11.2011
	 * @param query
	 * @param termDefinitions
	 * @return
	 */
	public List<Suggestion> getBestSuggestionsAllAlgorithms(String query, List<String> termDefinitions) {

		List<Suggestion> bestSuggs = this.cache.getSuggestionsFromCache(query, termDefinitions);
		if (bestSuggs != null)
			return bestSuggs;
		
		List<SuggestionCountPair> matchList = new ArrayList<SuggestionCountPair>();
		bestSuggs = new ArrayList<Suggestion>();
		// When query contains Whitespace then use algorithmsPhrase
		if (query.contains(" "))
		{
			this.getBestSuggestionsPhrase(query, termDefinitions, matchList, true);
		}
		else if (!query.contains(" "))
		{
			this.getBestSuggestionsToken(query, termDefinitions, matchList, true);
		}

		// Sort the matchList and add the count of
		// maxSuggestions to best Suggestions
		Collections.sort(matchList, new SuggestionCountPairComparator());
		for (int i = 0; (i < maxSuggestions) && (i < matchList.size()); i++)
			bestSuggs.add(matchList.get(i).getSuggestion());

		this.cache.storeQueryResult(query, termDefinitions, bestSuggs);
		
		return bestSuggs;
	}

	/**
	 * Collects the best suggestions if query is a Token.
	 * So it only uses Matching-Algorithms for phrases.
	 * 
	 * @created 20.02.2012
	 * @param query
	 * @param termDefinitions
	 * @param matchList
	 * @param useAllAlgorithms
	 */
	private void getBestSuggestionsToken(String query, List<String> termDefinitions,
			List<SuggestionCountPair> matchList, boolean useAllAlgorithms)
	{
		List<String> tokenTerms = new ArrayList<String>();
		List<String> phraseTerms = new ArrayList<String>();
		for (String s : termDefinitions)
			if (s.contains(" "))
				phraseTerms.add(s);
			else
				tokenTerms.add(s);

		this.getBestSuggestionsPhrase(query, phraseTerms, matchList, useAllAlgorithms);

		List<Suggestion> suggs = new ArrayList<Suggestion>();

		List<MatchingAlgorithm> algorithms = new ArrayList<MatchingAlgorithm>();
		if (!useAllAlgorithms)
		{
			algorithms.add(usedTokenAlgorithm);
		}
		if (useAllAlgorithms)
		{
			algorithms = algorithmsToken;
		}

		for (MatchingAlgorithm algo : algorithms)
		{
			suggs = algo.getMatches(maxSuggestions, threshold, query, tokenTerms);
			boolean remove = true;
			suggs = this.removeExactMatches(suggs);
			while(remove) remove = suggs.remove(null);
			for (Suggestion s : suggs)
			{
				int exists = AlgorithmUtil.containsSuggestion(matchList, s);
				if (exists != -1)
				{
					matchList.get(exists).increment();
					matchList.get(exists).updateDistance(s);
				}
				else
					matchList.add(new SuggestionCountPair(s));
			}
		}
	}

	/**
	 * 
	 * Cleans the given list from exact matches.
	 * 
	 * @created 20.02.2012
	 * @param suggs
	 */
	private List<Suggestion> removeExactMatches(List<Suggestion> suggs)
	{
		List<Suggestion> toReturn = new ArrayList<Suggestion>();
		for (Suggestion s : suggs)
			if (s.getDistance() != 1.0)
				toReturn.add(s);
		return toReturn;
	}

	/**
	 * Collects the best suggestions if query is a Phrase.
	 * So it only uses Matching-Algorithms for phrases.
	 * 
	 * @created 20.02.2012
	 * @param query
	 * @param termDefinitions
	 * @param matchList
	 * @param useAllAlgorithms
	 */
	private void getBestSuggestionsPhrase(String query, List<String> termDefinitions, List<SuggestionCountPair> matchList, boolean useAllAlgorithms)
	{
		List<MatchingAlgorithm> algorithms = new ArrayList<MatchingAlgorithm>();
		if (!useAllAlgorithms)
		{
			algorithms.add(usedPhraseAlgorithm);
		}
		if (useAllAlgorithms)
		{
			algorithms = algorithmsPhrase;
		}

		List<Suggestion> suggs = new ArrayList<Suggestion>();
		for (MatchingAlgorithm algo : algorithms)
		{
			suggs = algo.getMatches(maxSuggestions, threshold, query, termDefinitions);
			boolean remove = true;
			while(remove) remove = suggs.remove(null);
			suggs = this.removeExactMatches(suggs);
			for (Suggestion s : suggs) {
				int exists = AlgorithmUtil.containsSuggestion(matchList, s);
				if (exists != -1)
				{
					matchList.get(exists).increment();
					matchList.get(exists).updateDistance(s);
				}
				else
					matchList.add(new SuggestionCountPair(s));
			}
		}
	}

	/**
	 * Collects suggestions using the given MatchingAlgorithm
	 * 
	 * @param query
	 * @param termDefinitions
	 * @param algorithm
	 * @return
	 */
	public List<Suggestion> getSuggestions(String query,
			List<String> termDefinitions, MatchingAlgorithm algorithm)
	{
		if (algorithm == null)
			return getBestSuggestionsAllAlgorithms(query, termDefinitions);

		List<Suggestion> bestSuggs = this.cache.getSuggestionsFromCache(query, termDefinitions);
		if (bestSuggs != null)
			return bestSuggs;
		
		bestSuggs = new ArrayList<Suggestion>();

		// Put all Suggestions in List
		List<Suggestion> suggs = new ArrayList<Suggestion>();
		List<SuggestionCountPair> matchList = new ArrayList<SuggestionCountPair>();

		suggs = algorithm.getMatches(maxSuggestions, threshold, query, termDefinitions);
		boolean remove = true;
		while(remove) remove = suggs.remove(null);
		suggs = this.removeExactMatches(suggs);
		for (Suggestion s : suggs) {
			int exists = AlgorithmUtil.containsSuggestion(matchList, s);
			if (exists != -1)
			{
				matchList.get(exists).increment();
				matchList.get(exists).updateDistance(s);
			}
			else
				matchList.add(new SuggestionCountPair(s));
		}

		// Sort the matchList and add the count of
		// maxSuggestions to best Suggestions
		Collections.sort(matchList, new SuggestionCountPairComparator());
		for (int i = 0; (i < maxSuggestions) && (i < matchList.size()); i++)
			bestSuggs.add(matchList.get(i).getSuggestion());

		this.cache.storeQueryResult(query, termDefinitions, bestSuggs);
		
		return bestSuggs;
	}

	// Getters and Setters for Suggestion staff
	public void setMaxSuggestions(int max)
	{
		this.maxSuggestions = max;
	}

	public int getMaxSuggestions()
	{
		return this.maxSuggestions;
	}

	public void setThreshold(double threshold)
	{
		this.threshold = threshold;
	}

	public double getThreshold()
	{
		return this.threshold;
	}

	// Getters and setters for MatchingAlgorithm staff
	public List<MatchingAlgorithm> getPossibleMatchingAlgorithms()
	{
		List<MatchingAlgorithm> toReturn = new ArrayList<MatchingAlgorithm>();
		toReturn.addAll(algorithmsToken);
		toReturn.addAll(algorithmsPhrase);
		return Collections.unmodifiableList(toReturn);
	}
	public boolean setUsedTokenMatchingAlgorithm(MatchingAlgorithm algorithm)
	{
		if (algorithmsToken.contains(algorithm))
		{
			usedTokenAlgorithm = algorithm;
			return true;
		}
		return false;
	}
	public boolean setUsedPhraseMatchingAlgorithm(MatchingAlgorithm algorithm)
	{
		if (algorithmsPhrase.contains(algorithm))
		{
			usedPhraseAlgorithm = algorithm;
			return true;
		}
		return false;
	}

}
