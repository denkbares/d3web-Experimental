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
package de.knowwe.usersupport.algorithm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import com.wcohen.ss.JaroWinkler;


/**
 * 
 * Uses {@link JaroWinklerAlgorithm} to compute a distance
 * with phrases as input.
 * 
 * @author Johannes Dienst
 * @created 21.02.2012
 */
public class SortedWinklerAlgorithm implements MatchingAlgorithm
{

	@Override
	public List<Suggestion> getMatches(int maxCount, double threshold, String toMatch, List<String> localTermMatches)
	{
		JaroWinkler jW = new JaroWinkler();
		if (toMatch.contains(" "))
		{
			String[] a = toMatch.split(" ");
			List<String> sortMe = Arrays.asList(a);
			Collections.sort(sortMe);
			toMatch = "";
			for (String s : a)
			{
				toMatch += s;
				toMatch += " ";
			}

			toMatch = toMatch.substring(0, toMatch.length()-1);
		}

		PriorityQueue<Suggestion> suggestions =
				new PriorityQueue<Suggestion>(maxCount, new SuggestionComparator());

		for (String term : localTermMatches) {
			String term2 = term;
			if (term.contains(" "))
			{
				String[] a = term.split(" ");
				List<String> sortMe = Arrays.asList(a);
				Collections.sort(sortMe);
				term2 = "";
				for (String s : a)
				{
					term2 += s;
					term2 += " ";
				}
				term2 = term2.substring(0, term2.length()- 1);
			}
			double score = jW.score(toMatch, term2);
			if (score >= threshold) {
				suggestions.add(new Suggestion(term, score ));
			}
		}

		List<Suggestion> toReturn = AlgorithmUtil.reduceSuggestionCount(maxCount, suggestions);
		return toReturn;
	}

	@Override
	public String toString()
	{
		return "SortedWinklerAlgorithm";
	}

}
