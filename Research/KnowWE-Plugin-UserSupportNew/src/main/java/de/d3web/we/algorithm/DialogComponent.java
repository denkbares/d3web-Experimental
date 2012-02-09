/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;


/**
 * 
 * Searches for Terms in the Terminology of a KnowledgeBase.
 * Can handle approximative String Matching algorithms.
 * 
 * All used Algorithms have to implement the {@link MatchingAlgorithm} Interface
 * to be fully plugable. Add them in the private Constroctur if you have implemented
 * a new one. TODO Find better way to plug new Algorithms
 * 
 * The DialogComponent is configurable over the ResourceBundle <em>UserSupport_configuration</em>
 * 
 * @author Johannes Dienst
 * @created 04.10.2011
 */
public class DialogComponent {

	private final List<MatchingAlgorithm> algorithms = new ArrayList<MatchingAlgorithm>();
	private final ResourceBundle bundle = ResourceBundle.getBundle("UserSupport_configuration");
	private int maxSuggestions;

	private MatchingAlgorithm usedTokenAlgorithm;
	private MatchingAlgorithm usedPhraseAlgorithm;

	private static DialogComponent uniqueInstance;

	/**
	 * To avoid instantiation
	 */
	private DialogComponent() {
		maxSuggestions =
				Integer.parseInt(
						bundle.getString("usersupport.dialogcomponent.maxSuggestions"));

		try {
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
		catch (Exception e) {
			usedTokenAlgorithm = new LevenshteinAlgorithm();
			usedPhraseAlgorithm = new MongeElkanAlgorithm();
		}

		// Add all known Algorithms
		algorithms.add(new DoubleMetaphoneAlgorithm());
		algorithms.add(new JaroWinklerAlgorithm());
		algorithms.add(new LevenshteinAlgorithm());
		algorithms.add(new MongeElkanAlgorithm());
		//		algorithms.add(new RefinedSoundexAlgorithm());
		algorithms.add(new SmithWatermanAlgorithm());
	}

	/**
	 * Singleton.
	 * 
	 * @created 21.11.2011
	 * @return
	 */
	public static DialogComponent getInstance() {
		if (uniqueInstance == null)
			uniqueInstance = new DialogComponent();
		return uniqueInstance;
	}

	/**
	 * 
	 * Collects the best suggestions found by all
	 * Matching in the terminology.
	 * 
	 * This is slow but will definitely find the Best matches!
	 * 
	 * @created 21.11.2011
	 * @param toMatch
	 * @param localTermMatches
	 * @return
	 */
	public List<Suggestion> getBestSuggestions(String toMatch,
			List<String> localTermMatches) {

		List<Suggestion> bestSuggs = new ArrayList<Suggestion>();

		// Put all Suggestions in List
		List<Suggestion> suggs = new ArrayList<Suggestion>();
		List<SuggestionValuePair> matchList = new ArrayList<SuggestionValuePair>();

		for (MatchingAlgorithm algo : algorithms) {
			suggs = algo.getMatches(maxSuggestions, toMatch, localTermMatches);
			boolean remove = true;
			while(remove) remove = suggs.remove(null);
			for (Suggestion s : suggs) {
				int exists = AlgorithmUtil.containsSuggestion(matchList, s);
				if (exists != -1)
				{
					matchList.get(exists).increment();
					matchList.get(exists).updateDistance(s);
				}
				else
					matchList.add(new SuggestionValuePair(s));
			}

		}

		// Sort the matchList and add the count of
		// maxSuggestions to best Suggestions
		Collections.sort(matchList, new SuggestionValuePairComparator());
		for (int i = 0; (i < maxSuggestions) && (i < matchList.size()); i++)
			bestSuggs.add(matchList.get(i).getSuggestion());
		return bestSuggs;
	}

	public List<Suggestion> getSuggestions(String toMatch,
			List<String> localTermMatches, MatchingAlgorithm algorithm) {

		if (algorithm == null)
			return getBestSuggestions(toMatch, localTermMatches);

		List<Suggestion> bestSuggs = new ArrayList<Suggestion>();

		// Put all Suggestions in List
		List<Suggestion> suggs = new ArrayList<Suggestion>();
		List<SuggestionValuePair> matchList = new ArrayList<SuggestionValuePair>();

		// TODO returns a list of nulls!
		suggs = algorithm.getMatches(maxSuggestions, toMatch, localTermMatches);

		for (Suggestion s : suggs) {
			// TODO HOTFIX for problem above
			if (s == null) continue;
			int exists = AlgorithmUtil.containsSuggestion(matchList, s);
			if (exists != -1)
				matchList.get(exists).increment();
			else
				matchList.add(new SuggestionValuePair(s));
		}

		// Sort the matchList and add the count of
		// maxSuggestions to best Suggestions
		Collections.sort(matchList, new SuggestionValuePairComparator());
		for (int i = 0; (i < maxSuggestions) && (i < matchList.size()); i++)
			bestSuggs.add(matchList.get(i).getSuggestion());
		return bestSuggs;
	}

	// Getters and Setters for Suggestion staff
	public void setMaxSuggestions(int max) {
		this.maxSuggestions = max;
	}
	public int getMaxSuggestions() {
		return this.maxSuggestions;
	}

	// Getters and setters for MatchingAlgorithm staff
	public List<MatchingAlgorithm> getPossibleMatchingAlgorithms() {
		return Collections.unmodifiableList(algorithms);
	}
	public boolean setUsedTokenMatchingAlgorithm(MatchingAlgorithm algorithm) {
		if (algorithms.contains(algorithm)) {
			usedTokenAlgorithm = algorithm;
			return true;
		}
		return false;
	}
	public boolean setUsedPhraseMatchingAlgorithm(MatchingAlgorithm algorithm) {
		if (algorithms.contains(algorithm)) {
			usedPhraseAlgorithm = algorithm;
			return true;
		}
		return false;
	}

}
