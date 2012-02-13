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
package de.d3web.we.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import com.wcohen.ss.JaroWinkler;

/**
 * 
 * @author Johannes Dienst
 * @created 04.10.2011
 */
public class JaroWinklerAlgorithm implements MatchingAlgorithm {

	@Override
	public List<Suggestion> getMatches(int maxCount, String toMatch,
			List<String> localTermMatches) {

		JaroWinkler jW = new JaroWinkler();
		PriorityQueue<Suggestion> suggestions =
				new PriorityQueue<Suggestion>(maxCount, new SuggestionComparator());

		for (String match : localTermMatches) {
			double score = jW.score(toMatch, match);
			// TODO threshold is experimental
			if (score >= 0.7) {
				suggestions.add(new Suggestion(match, score));
			}
		}

		List<Suggestion> toReturn = new ArrayList<Suggestion>();
		for (int i = 0; i < maxCount; i++) {
			Suggestion s = suggestions.poll();
			if (s != null) toReturn.add(s);
		}

		return toReturn;
	}

}
