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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.wcohen.ss.JaroWinkler;


/**
 * 
 * @author Johannes Dienst
 * @created 21.02.2012
 */
public class PermutedWinkler implements MatchingAlgorithm
{

	@Override
	public List<Suggestion> getMatches(int maxCount, double threshold, String toMatch, List<String> termDefinitions)
	{
		JaroWinkler jW = new JaroWinkler();
		List<String> toMatchPermutations = new ArrayList<String>();
		String[] a = toMatch.split(" ");
		List<String> permutedMatch = Arrays.asList(a);
		if (toMatch.contains(" "))
		{
			toMatchPermutations.addAll(PermutedWinkler.permuteListRecursive(permutedMatch));
		}
		else
		{
			toMatchPermutations.add(toMatch);
		}
		
		PriorityQueue<Suggestion> suggestions =
				new PriorityQueue<Suggestion>(maxCount, new SuggestionComparator());

		for (String term : termDefinitions) {
			String term2 = term;
			Map<String, String> termPermutations = new HashMap<String,String>();
			if (term.contains(" "))
			{
				a = term.split(" ");
				List<String> permuteMe = Arrays.asList(a);
				permuteMe = PermutedWinkler.permuteListRecursive(permuteMe);
				for (String p : permuteMe)
				{
					termPermutations.put(p, term);
				}
			}
			else
			{
				termPermutations.put(term, term);
			}
			
			
			double score = 0.0;
			for (String match : toMatchPermutations)
			{
				
				for (String t : termPermutations.keySet())
				{
					score = jW.score(match, t);
					if (score >= threshold)
					{
						suggestions.add(new Suggestion(termPermutations.get(t), score));
					}
				}
			}
		}

		List<Suggestion> toReturn = AlgorithmUtil.reduceSuggestionCount(maxCount, suggestions);
		return toReturn;
	}

	  public static List<String> permuteListRecursive(List<String> toPermute)
	  {

	    if (toPermute.size() == 1)
	    {
	      return toPermute;
	    }

	    List<String> permuted = new ArrayList<String>();
	    String fixed = toPermute.get(0);
	    List<String> permutedSublist = PermutedWinkler.permuteListRecursive(toPermute.subList(1, toPermute.size()));

	    // every permutation
	    for (int i = 0; i < permutedSublist.size(); i++)
	    {
	      String permutation = permutedSublist.get(i);
	      String[] permWords = permutation.split(" ");

	      // insert in every position
	      for (int k = 0; k <= permWords.length; k++)
	      {
	        boolean inserted = false;
	        StringBuilder result = new StringBuilder();
	        for (int j = 0; j < permWords.length; j++)
	        {
	          if (j == k)
	          {
	            inserted = true;
	            result.append(" " + fixed + " " + permWords[j]);
	          }
	          else
	          {
	            result.append(" " + permWords[j]);
	          }
	        }

	        // insert at last position
	        if (!inserted)
	        {
	          result.append(" " + fixed);
	        }

	        result.deleteCharAt(0);
	        permuted.add(result.toString());
	      }
	    }

	    return permuted;
	  }

	@Override
	public String toString()
	{
		return "PermutedWinklerAlgorithm";
	}

}
