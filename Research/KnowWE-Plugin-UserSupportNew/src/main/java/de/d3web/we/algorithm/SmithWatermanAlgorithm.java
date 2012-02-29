/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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

import java.util.List;
import java.util.PriorityQueue;

import com.wcohen.ss.SmithWaterman;


/**
 * 
 * @author Johannes Dienst
 * @created 03.02.2012
 */
public class SmithWatermanAlgorithm implements MatchingAlgorithm
{

	@Override
	public List<Suggestion> getMatches(int maxCount, double threshold, String toMatch, List<String> localTermMatches)
	{

		SmithWaterman sM = new SmithWaterman();

		PriorityQueue<Suggestion> suggestions =
				new PriorityQueue<Suggestion>(maxCount, new SuggestionComparator());

		for (String term : localTermMatches)
		{
			double score = sM.score(toMatch, term);
			int div = Math.max(term.length(), toMatch.length()); // could also be min or 0.5*(s1+s2)
			double exactMatchScore = 5.0; // TODO where is this from?
			double result = score / (div * exactMatchScore);
			if (result >= threshold)
			{
				suggestions.add(new Suggestion(term, result));
			}
		}

		List<Suggestion> toReturn = AlgorithmUtil.reduceSuggestionCount(maxCount, suggestions);

		return toReturn;
	}

}
