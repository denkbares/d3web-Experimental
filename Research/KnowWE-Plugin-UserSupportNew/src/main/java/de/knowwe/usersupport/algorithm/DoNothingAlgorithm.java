package de.knowwe.usersupport.algorithm;

import java.util.LinkedList;
import java.util.List;

public class DoNothingAlgorithm implements MatchingAlgorithm {


	@Override
	public List<Suggestion> getMatches(int maxCount, double threshold,
			String query, List<String> termDefinitions) {
		LinkedList<Suggestion> l = new LinkedList<Suggestion>(); 
		return l;
	}

}
