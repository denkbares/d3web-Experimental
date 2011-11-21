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
package de.d3web.we.dialog.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;


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

	private MatchingAlgorithm usedAlgorithm;

	private static DialogComponent uniqueInstance;

	/**
	 * To avoid instantiation
	 */
	private DialogComponent() {
		maxSuggestions =
			Integer.parseInt(
					bundle.getString("usersupport.dialogcomponent.maxSuggestions"));

		try {
			usedAlgorithm = MatchingAlgorithm.class.cast(
					Class.forName(
							bundle.getString("usersupport.dialogcomponent.standardmatchingalgorithm")));
		}
		catch (ClassNotFoundException e) {
			usedAlgorithm = new LevenshteinAlgorithm();
		}

		// Add all known Algorithms
		algorithms.add(new DoubleMetaphoneAlgorithm());
		algorithms.add(new JaroWinklerAlgorithm());
		algorithms.add(new LevenshteinAlgorithm());
		algorithms.add(new MonkeElkanAlgorithm());
		algorithms.add(new RefinedSoundexAlgorithm());
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
			Collection<Section<? extends TermDefinition>> localTermMatches) {

		List<Suggestion> bestSuggs = new ArrayList<Suggestion>();

		// Put all Suggestions in List
		List<Suggestion> suggs = new ArrayList<Suggestion>();
		List<SuggestionValuePair> matchList = new ArrayList<SuggestionValuePair>();

		for (MatchingAlgorithm algo : algorithms) {
			suggs = algo.getMatches(maxSuggestions, toMatch, localTermMatches);

			for (Suggestion s : suggs) {
				int exists = AlgorithmUtil.containsSuggestion(matchList, s);
				if (exists != -1)
					matchList.get(exists).increment();
				else
					matchList.add(new SuggestionValuePair(s));
			}

		}

		// Sort the matchList and add the count of
		// maxSuggestions to best Suggestions
		Collections.sort(matchList, new SuggestionValuePairComparator());
		for (int i = 0; i < maxSuggestions; i++)
			bestSuggs.add(matchList.get(i).getSuggestion());
		return bestSuggs;
	}

	public List<Suggestion> getBestSuggestionsUsedAlgorithm(String toMatch,
			Collection<Section<? extends TermDefinition>> localTermMatches) {

		List<Suggestion> bestSuggs = new ArrayList<Suggestion>();

		// Put all Suggestions in List
		List<Suggestion> suggs = new ArrayList<Suggestion>();
		List<SuggestionValuePair> matchList = new ArrayList<SuggestionValuePair>();

		suggs = usedAlgorithm.getMatches(maxSuggestions, toMatch, localTermMatches);

		for (Suggestion s : suggs) {
			int exists = AlgorithmUtil.containsSuggestion(matchList, s);
			if (exists != -1)
				matchList.get(exists).increment();
			else
				matchList.add(new SuggestionValuePair(s));
		}

		// Sort the matchList and add the count of
		// maxSuggestions to best Suggestions
		Collections.sort(matchList, new SuggestionValuePairComparator());
		for (int i = 0; i < maxSuggestions; i++)
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
	public boolean setUsedMatchingAlgorithm(MatchingAlgorithm algorithm) {
		if (algorithms.contains(algorithm)) {
			usedAlgorithm = algorithm;
			return true;
		}
		return false;
	}

	/*
	 *  Helper Classes
	 */
	public class SuggestionValuePair {
		private final Suggestion s;
		private int val;

		public SuggestionValuePair(Suggestion suggestion) {
			this(suggestion, 0);
		}
		public SuggestionValuePair(Suggestion suggestion, int value) {
			s = suggestion;
			val = value;
		}
		public Suggestion getSuggestion() {
			return s;
		}
		public int getValue() {
			return val;
		}
		public void increment(){
			val++;
		}
	}

	public class SuggestionValuePairComparator implements Comparator<SuggestionValuePair> {

		@Override
		public int compare(SuggestionValuePair o1, SuggestionValuePair o2) {
			if (o1.getValue() > o2.getValue()) return 1;
			if (o1.getValue() < o2.getValue()) return -1;
			return 0;
		}

	}
}
