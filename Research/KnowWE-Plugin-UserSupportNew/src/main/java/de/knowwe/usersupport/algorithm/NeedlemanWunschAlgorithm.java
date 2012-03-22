package de.knowwe.usersupport.algorithm;

import java.util.List;
import java.util.PriorityQueue;

import uk.ac.shef.wit.simmetrics.similaritymetrics.NeedlemanWunch;

/**
 * 
 * 
 * @author Johannes Dienst
 * @created 18.03.2012
 *
 */
public class NeedlemanWunschAlgorithm implements MatchingAlgorithm
{

	@Override
	public List<Suggestion> getMatches(int maxCount, double threshold,
			String toMatch, List<String> termDefinitions)
	{
		NeedlemanWunch nW = new NeedlemanWunch();

		PriorityQueue<Suggestion> suggestions =
				new PriorityQueue<Suggestion>(maxCount, new SuggestionComparator());

		for (String match : termDefinitions)
		{
			double score = nW.getSimilarity(toMatch, match);
			if (score >= threshold) {
				suggestions.add(new Suggestion(match, score));
			}
		}

		List<Suggestion> toReturn = AlgorithmUtil.reduceSuggestionCount(maxCount, suggestions);

		return toReturn;
	}

	@Override
	public String toString()
	{
		return "NeedlemanWunschAlgorithm";
	}
}
