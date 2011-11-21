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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;


/**
 * 
 * @author Johannes Dienst
 * @created 04.10.2011
 */
public class DialogComponent {

	private final List<MatchingAlgorithm> algorithms = new ArrayList<MatchingAlgorithm>();
	private final ResourceBundle bundle = ResourceBundle.getBundle("UserSupport");
	private int maxSuggestions;

	// TODO ResourceBundle

	private static DialogComponent uniqueInstance;

	private DialogComponent(){
		maxSuggestions =
				Integer.parseInt(
						bundle.getString("usersupport.dialogcomponent.maxSuggestions"));
	}

	public static DialogComponent getInstance() {
		if (uniqueInstance == null)
			uniqueInstance = new DialogComponent();
		return uniqueInstance;
	}

	public List<Suggestion> getBestSuggestions(String toMatch,
			Collection<Section<? extends TermDefinition>> localTermMatches) {

		List<Suggestion> bestSuggs = new ArrayList<Suggestion>();

		// Put all Suggestions in HashMap
		List<Suggestion> suggs = new ArrayList<Suggestion>();
		HashMap<Suggestion, Integer> matchHash = new HashMap<Suggestion, Integer>();
		for (MatchingAlgorithm algo : algorithms) {
			suggs = algo.getMatches(maxSuggestions, toMatch, localTermMatches);
			for (Suggestion s : suggs)
				if (matchHash.containsKey(s.getSuggestion()))
					matchHash.put(s, matchHash.get(s)+1);
				else
					matchHash.put(s, 1);
		}

		// Sort the HashMap
		matchHash.entrySet();
		//		Collections.sort(list, c);
		return bestSuggs;
	}

	public void setMaxSuggestions(int max) {
		this.maxSuggestions = max;
	}
	public int getMaxSuggestions() {
		return this.maxSuggestions;
	}

}
