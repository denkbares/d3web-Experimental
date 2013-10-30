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

}
