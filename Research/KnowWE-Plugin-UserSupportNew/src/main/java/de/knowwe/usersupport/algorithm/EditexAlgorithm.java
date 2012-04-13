/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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

/**
 * 
 * Character edit-distance: find the edit distance (also called Levenstein
 * distance) between two strings. contain the edit distance and the editex
 * variant (combine edit and soundex phonix)
 * 
 * TODO Where code taken from?
 * 
 * @author Johannes Dienst
 * @created 04.04.2012
 */
public class EditexAlgorithm implements MatchingAlgorithm {

	/**
	 * zero if a and b are identical 1 otherwise
	 * 
	 * static int redit(int a, int b) { if(a==b) return 0; return 1; }
	 */

	/**
	 * same as r(a,b) zero if a and b are identical 1 if a and b in the same
	 * group, 2 otherwise
	 */
	static int reditexEnglish(int a, int b)
	{
		if (a == b) return 0;
		if ((editexGroupENGLISH((char) a) & editexGroupENGLISH((char) b)) != 0) return 1;
		return 2;
	}

	static int d(int a, int b)
	{
		if (a == 'H' && b == 'W' && a != b) return 1;
		if (a == 'W' && b == 'H' && a != b) return 1;
		return reditexEnglish(a, b);
	}

	/**
	 * @return the editex group. Caution: some letters appears twice. use
	 *         bitwise operator and to determine matchings group1 & group2 is
	 *         not zero if class match
	 */
	private static int editexGroupENGLISH(char c)
	{
		int rep = 0;
		if ("AEIOUY".indexOf(c) != -1) rep += 1;
		if ("BP".indexOf(c) != -1) rep += 2;
		if ("CKQ".indexOf(c) != -1) rep += 4;
		if ("DT".indexOf(c) != -1) rep += 8;
		if ("LR".indexOf(c) != -1) rep += 16;
		if ("MN".indexOf(c) != -1) rep += 32;
		if ("GJ".indexOf(c) != -1) rep += 64;
		if ("FPV".indexOf(c) != -1) rep += 128;
		if ("SXZ".indexOf(c) != -1) rep += 256;
		if ("CSZ".indexOf(c) != -1) rep += 512;
		return rep;
	}

	/**
	 * combine edit and soundex w1 must be already uppercased
	 */
	public static int editexDistanceEnglish(String w1, String w2, int tolerance)
	{
		// boost??
		if (Math.abs(w1.length() - w2.length()) > tolerance) return tolerance + 1; // refused

		return editexEnglish(w1.length(), w2.length(), w1, w2.toUpperCase());
	}

	/**
	 * combine edit and soundex for English
	 */
	static int editexEnglish(int i, int j, final String w1, final String w2)
	{
		if (i == 0 && j == 0) return 0;
		if (i == 0) return editexEnglish(0, j - 1, w1, w2)
				+ (j < w2.length() ? d(w2.charAt(j - 1), w2.charAt(j)) : 0);
		if (j == 0) return editexEnglish(i - 1, 0, w1, w2)
				+ (i < w1.length() ? d(w1.charAt(i - 1), w1.charAt(i)) : 0);
		int p1 = editexEnglish(i - 1, j - 1, w1, w2)
				+ reditexEnglish(w1.charAt(i - 1), w2.charAt(j - 1));
		if (p1 == 0) return 0;
		int p2 = editexEnglish(i - 1, j, w1, w2)
				+ (i < w1.length() ? d(w1.charAt(i - 1), w1.charAt(i)) : 0);
		int p3 = editexEnglish(i, j - 1, w1, w2)
				+ (j < w2.length() ? d(w2.charAt(j - 1), w2.charAt(j)) : 0);

		return Math.min(
				Math.min(p2, p3),
				p1);
	}

	private static int min(int a, int b, int c)
	{
		int mi = a;
		if (b < mi)
		{
			mi = b;
		}
		if (c < mi)
		{
			mi = c;
		}
		return mi;
	}

	/**
	 * @return the edit distance, also called Levenshtein distance. is the
	 *         number of inserts, deletes to transform string s into string t
	 */
	public static int editDistance(String s, String t, int maxDistance)
	{
		int n = s.length();
		int m = t.length();
		if (n == 0) return m;
		if (m == 0) return n;
		if (n - m > maxDistance) return n - m; // Boost for great unequal length
		if (m - n > maxDistance) return m - n;

		int d[][] = new int[n + 1][m + 1];

		for (int i = 0; i <= n; i++)
			d[i][0] = i;
		for (int j = 0; j <= m; j++)
			d[0][j] = j;

		for (int i = 1; i <= n; i++)
		{
			char s_i = s.charAt(i - 1);
			for (int j = 1; j <= m; j++)
			{
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1,
						d[i - 1][j - 1] + (s_i == t.charAt(j - 1) ? 0 : 1));
			}
		}

		return d[n][m];
	}

	public static void main(String[] args) {

		System.out.println(editexDistanceEnglish("RRED", "RED", 6));
	}

	@Override
	public List<Suggestion> getMatches(int maxCount, double threshold, String query, List<String> termDefinitions) {

		query = query.toUpperCase();
		PriorityQueue<Suggestion> suggestions =
				new PriorityQueue<Suggestion>(maxCount, new SuggestionComparator());

		for (String term : termDefinitions)
		{
			int dist = editexDistanceEnglish(query, term.toUpperCase(), query.length());
			// equal if dist == 0: score is 1
			if (dist == 0) suggestions.add(new Suggestion(term, 1));
		}

		List<Suggestion> toReturn = AlgorithmUtil.reduceSuggestionCount(maxCount, suggestions);
		return toReturn;
	}

}
