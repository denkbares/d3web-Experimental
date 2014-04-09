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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.d3web.utils.Log;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.rdfs.vis.OntoGraphDataBuilder;
import de.knowwe.visualization.ConceptNode;
import de.knowwe.visualization.GraphDataBuilder;
import de.knowwe.visualization.SubGraphData;

/**
 * @author jochenreutelshofer
 * @created 29.11.2012
 */
public class Utils {

	public static final String LINE_BREAK = "\\n";

	public static String getRDFSLabel(Node concept, Rdf2GoCore repo, String languageTag) {

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
	 * @param concept
	 * @param repo
	 * @param languageTag
	 * @return
	 * @created 29.04.2013
	 */
	private static String getLanguageSpecifcLabel(Node concept, Rdf2GoCore repo, String languageTag) {
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

	public static ConceptNode createNode(Map<String, String> parameters, Rdf2GoCore rdfRepository, LinkToTermDefinitionProvider uriProvider, Section<?> section, SubGraphData data, Node toURI, boolean insertNewNode) {
		ConceptNode visNode = null;

		GraphDataBuilder.NODE_TYPE type = GraphDataBuilder.NODE_TYPE.UNDEFINED;
		Literal toLiteral = null;
		String label = null;
		String identifier = null;


		/*
		1. case: Node is Literal
		 */
		try {
			toLiteral = toURI.asLiteral();
			//add hashcode to identifier to have distinct dot nodes for literals used in different contexts, e.g. "1"
			identifier = toLiteral.toString() + toLiteral.hashCode();
			type = GraphDataBuilder.NODE_TYPE.LITERAL;
			label = toLiteral.toString();
			if (label.contains("@")) {
				label = label.substring(0, label.indexOf('@'));
			}

			visNode = new ConceptNode(identifier, type, createConceptURL(identifier, parameters,
					section,
					uriProvider), "\"" + label + "\"");
			if (insertNewNode) {
				data.addConcept(visNode);
			}
			return visNode;
		}
		catch (ClassCastException e) {
			// do nothing as this is just a type check
		}

		/*
		2. case: Node is BlankNode
		 */
		BlankNode bNode = null;
		try {
			bNode = toURI.asBlankNode();
			identifier = bNode.toString();

			visNode = data.getConcept(identifier);
			if (visNode == null) {
				type = GraphDataBuilder.NODE_TYPE.BLANKNODE;
				label = bNode.toString();
				visNode = new ConceptNode(identifier, type, createConceptURL(identifier, parameters,
						section,
						uriProvider), label);
				if (insertNewNode) {
					data.addConcept(visNode);
				}
			}
			return visNode;

		}
		catch (ClassCastException e) {
			// do nothing as this is just a type check
		}


		/*
		3. case: Node is URI-Resource
		 */
		try {
			URI uri = toURI.asURI();
			identifier = getConceptName(toURI, rdfRepository);
			visNode = data.getConcept(identifier);

			if (visNode == null) {

				type = GraphDataBuilder.NODE_TYPE.UNDEFINED;
				if (Rdf2GoUtils.isClass(rdfRepository, uri)) {
					type = GraphDataBuilder.NODE_TYPE.CLASS;
				}
				if (Rdf2GoUtils.isProperty(rdfRepository, uri)) {
					type = GraphDataBuilder.NODE_TYPE.PROPERTY;
				}
				if (parameters.get(GraphDataBuilder.USE_LABELS) != null
						&& parameters.get(GraphDataBuilder.USE_LABELS).equals("true")) {
					label = Utils.getRDFSLabel(
							toURI, rdfRepository,
							parameters.get(OntoGraphDataBuilder.LANGUAGE));
				}
				if (label == null) {
					label = identifier;
				}
				visNode = new ConceptNode(identifier, type, createConceptURL(identifier, parameters,
						section,
						uriProvider), label);
				if (insertNewNode) {
					data.addConcept(visNode);
				}
			}
			return visNode;
		}
		catch (ClassCastException e) {
			// do nothing as this is just a type check
		}

		// this case should/can never happen!
		Log.severe("No valid Node type!");
		return null;
	}

	private static String createConceptURL(String to, Map<String, String> parameters, Section<?> s, LinkToTermDefinitionProvider uriProvider) {
		if (parameters.get(OntoGraphDataBuilder.LINK_MODE) != null) {
			if (parameters.get(OntoGraphDataBuilder.LINK_MODE).equals(
					OntoGraphDataBuilder.LINK_MODE_BROWSE)) {
				Identifier identifier = new Identifier(to);
				String[] identifierParts = to.split(":");
				if (identifierParts.length == 2) {
					identifier = new Identifier(
							identifierParts[0], Strings.decodeURL(identifierParts[1]));

				}
				String url = uriProvider.getLinkToTermDefinition(identifier,
						parameters.get(OntoGraphDataBuilder.MASTER));
				if (!url.startsWith("http:")) {
					url = Environment.getInstance().getWikiConnector().getBaseUrl() + url;
				}
				return url;
			}
		}
		return OntoGraphDataBuilder.createBaseURL() + "?page="
				+ OntoGraphDataBuilder.getSectionTitle(s)
				+ "&concept=" + to;
	}

	public static String getConceptName(Node uri, Rdf2GoCore repo) {
		/*
		handle string/literal
		 */
		try {
			Literal string = uri.asLiteral();
			return string.toString();
		}
		catch (ClassCastException e) {
			// do noting, was for check only
		}
		/*
		handle URI
		 */
		try {
			String reducedNamespace = Rdf2GoUtils.reduceNamespace(repo,
					uri.asURI().toString());
			String[] splitURI = reducedNamespace.split(":");
			String namespace = splitURI[0];
			String name = splitURI[1];
			if (namespace.equals("lns")) {
				return urlDecode(name);
			}
			else {
				return namespace + ":" + urlDecode(name);
			}

		}
		catch (ClassCastException e) {
			return null;
		}
	}

	public static String urlDecode(String name) {
		try {
			return URLDecoder.decode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String createColorCodings(String relationName, Rdf2GoCore core) {
		StringBuffer result = new StringBuffer();

		String query = "SELECT ?property ?color WHERE {" +
				"?property rdf:type rdf:Property ." +
				"?property " + relationName + " ?color" +
				"}";
		QueryResultTable resultTable = core.sparqlSelect(query);
		ClosableIterator<QueryRow> iterator = resultTable.iterator();
		while (iterator.hasNext()) {
			QueryRow row = iterator.next();
			Node property = row.getValue("property");
			String color = row.getLiteralValue("color");
			String shortURI = Rdf2GoUtils.reduceNamespace(core, property.toString());
			result.append(shortURI + " " + color + ";");
		}

		return result.toString().trim();
	}

}
