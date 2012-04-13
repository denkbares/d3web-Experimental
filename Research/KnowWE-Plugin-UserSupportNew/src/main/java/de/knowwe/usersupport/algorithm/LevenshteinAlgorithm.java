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

import java.util.List;
import java.util.PriorityQueue;

import com.wcohen.ss.Levenstein;

/**
 * 
 * @author Johannes Dienst
 * @created 04.10.2011
 */
public class LevenshteinAlgorithm implements MatchingAlgorithm {

	@Override
	public List<Suggestion> getMatches(int maxCount, double threshold, String query, List<String> termDefinitions)
	{

		Levenstein l = new Levenstein();

		PriorityQueue<Suggestion> suggestions =
				new PriorityQueue<Suggestion>(maxCount, new SuggestionComparator());

		for (String term : termDefinitions)
		{
			double score = l.score(query, term);
			int max = Math.max(term.length(), query.length());
			double minuend = score / max;
			double result = 1.0 + (minuend);
			if (result >= threshold) {
				suggestions.add(new Suggestion(term, result));
			}
		}

		List<Suggestion> toReturn = AlgorithmUtil.reduceSuggestionCount(maxCount, suggestions);

		return toReturn;
	}

	@Override
	public String toString()
	{
		return "LevenshteinAlgorithm";
	}
}
