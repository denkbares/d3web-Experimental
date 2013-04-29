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
package de.knowwe.rdfs.vis.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * 
 * @author jochenreutelshofer
 * @created 29.11.2012
 */
public class Utils {

	public static final String LINE_BREAK = "\\n";

	public static String getRDFSLabel(URI concept, Rdf2GoCore repo, String languageTag) {

		// try to find language specific label
		String label = getLanguageSpecifcLabel(concept, repo, languageTag);

		// otherwise use standard label
		if (label == null) {

			String query = "SELECT ?x WHERE { <" + concept.toString() + "> rdfs:label ?x.}";
			QueryResultTable resultTable = repo.sparqlSelect(query);
			for (QueryRow queryRow : resultTable) {
				Node node = queryRow.getValue("x");
				String value = node.asLiteral().toString();
				label = value;
				break; // we assume there is only one label

			}
		}
		return label;
	}

	/**
	 * 
	 * @created 29.04.2013
	 * @param concept
	 * @param repo
	 * @param languageTag
	 * @return
	 */
	private static String getLanguageSpecifcLabel(URI concept, Rdf2GoCore repo, String languageTag) {
		if (languageTag == null) return null;
		String label = null;

		String query = "SELECT ?x WHERE { <" + concept.toString()
				+ "> rdfs:label ?x. FILTER(LANGMATCHES(LANG(?x), \"" + languageTag + "\"))}";
		QueryResultTable resultTable = repo.sparqlSelect(query);
		for (QueryRow queryRow : resultTable) {
			Node node = queryRow.getValue("x");
			String value = node.asLiteral().toString();
			label = value;
			if (label.charAt(label.length() - 3) == '@') {
				label = label.substring(0, label.length() - 3);
			}
			break; // we assume there is only one label

		}
		return label;
	}

	public static String prepareLabel(String string) {
		// if (true) return string;
		String lb = LINE_BREAK;

		int length = string.length();
		if (length < 13) return clean(string, lb);

		// find possible line break positions
		Set<Integer> possibleLBs = new TreeSet<Integer>();

		// possible line breaks are before the following chars:
		// _ >= <= = . ( [ and white spaces
		Matcher m =
				Pattern.compile("_|>=|<=|=|\\.|\\([^\\)]{1}|\\[[^\\]]{1}").matcher(string);
		while (m.find()) {
			possibleLBs.add(m.start(0));
		}
		// line breaks at whitespace only if they are not in range of = or > or
		// <
		m = Pattern.compile("(?<=[^=<>]){3}( )(?=[^=<>]{3})").matcher(string);
		while (m.find()) {
			possibleLBs.add(m.start(1));
		}

		if (possibleLBs.isEmpty()) return clean(string, lb);

		// add the line breaks were it makes sense
		List<Integer> desiredLBs = new LinkedList<Integer>();
		Set<Integer> addedLBs = new TreeSet<Integer>();

		// optimal length is determined by the length of the given String
		double optimalLength = new Double(length) / Math.sqrt(length / 5);

		for (int i = 1; i < string.length() / optimalLength; i++) {
			// having the line breaks on these position would be optimal
			desiredLBs.add((int) Math.round(i * optimalLength));
		}

		// try to find those possible line breaks that closest to the optimal
		// line breaks
		int d = 0;
		for (Integer desLB : desiredLBs) {
			int bestCandiadate = 0;
			// to avoid breaks for only a few chars at the end, we make
			// extra efforts for the last line break
			// we get the line break that produces the smallest variance
			// we should actually calculate the best break via variance for
			// all line breaks, but that seems rather complex and not yet
			// justified right now, since the current simple algorithm
			// already produces nice results
			if (d == desiredLBs.size() - 1) {
				double bestVar = Double.MAX_VALUE;
				for (Integer posLB : possibleLBs) {
					Set<Integer> temp = new TreeSet<Integer>(addedLBs);
					temp.add(posLB);
					TreeSet<Integer> varianceCheck = new TreeSet<Integer>(temp);
					varianceCheck.add(length);
					double variance = getVariance(varianceCheck);
					if (variance <= bestVar) {
						bestVar = variance;
						bestCandiadate = posLB;
					}
				}
			}
			// for all other breakpoints, just get the one closest to the
			// desired position
			else {
				for (Integer posLB : possibleLBs) {
					if (Math.abs(desLB - posLB) <= Math.abs(desLB - bestCandiadate)) {
						bestCandiadate = posLB;
					}
				}
			}
			if (bestCandiadate != 0 && bestCandiadate != length) {
				addedLBs.add(bestCandiadate);
			}
			d++;
		}

		// but in the line breaks
		StringBuilder labelBuilder = new StringBuilder();
		List<String> split = new ArrayList<String>(addedLBs.size() + 1);
		int last = 0;
		for (Integer addedLB : addedLBs) {
			split.add(string.substring(last, addedLB));
			last = addedLB;
		}
		split.add(string.substring(last, string.length()));
		for (String s : split) {
			// clean the substrings
			labelBuilder.append(clean(s.trim(), lb) + lb);
		}

		String label = labelBuilder.toString();
		//
		// if (label.contains("/")) {
		// if (label.length() > 25) {
		// if (!label.contains("\\n")) {
		// label = label.replaceAll("/", "/\\\\n");
		// }
		// }
		// }
		//
		// if (label.endsWith("\\n")) {
		// label = label.substring(0, label.length() - 2);
		// }
		// label = "\"" + label + "\"";

		return label;
	}

	private static String clean(String text, String lineBreak) {
		String cleanText = StringEscapeUtils.escapeHtml(text);
		cleanText = cleanText.replaceAll("\\r\\n|\\n", lineBreak);
		return cleanText.replaceAll("\"", "'");
	}

	private static double getVariance(TreeSet<Integer> positions) {
		double average = 0;
		int last = 0;
		for (Integer position : positions) {
			average += position - last;
			last = position;
		}
		average = average / positions.size() - 1;
		double var = 0;
		last = 0;
		for (Integer position : positions) {
			var += Math.abs((position - last) - average);
			last = position;
		}
		return var;
	}

}
