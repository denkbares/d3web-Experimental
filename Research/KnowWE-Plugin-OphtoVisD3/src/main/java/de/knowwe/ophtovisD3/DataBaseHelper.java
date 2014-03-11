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
package de.knowwe.ophtovisD3;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.ophtovisD3.utils.Connections;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * @author chris
 * @created 01.10.2012
 */
class DataBaseHelper {

	// Teilmethode fragt nach Verbindungen in DBconType unterkonzept, liefert
	public static List<String> getConnectedNodeNamesOfType(String startNode,
														   String conType, boolean reverse) {
		List<String> connectedNodesList = new ArrayList<String>();
		QueryResultTable table = null;

		try {
			startNode = URLDecoder.decode(startNode, "UTF-8");
		}
		catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		startNode = createSparqlURI(startNode);

		if (reverse) {
			table = Rdf2GoCore.getInstance().sparqlSelect(
					"SELECT ?a WHERE {" + startNode + " lns:" + conType
							+ " ?a}");
		}
		else {
			table = Rdf2GoCore.getInstance().sparqlSelect(

					"SELECT ?a WHERE { ?a  lns:" + conType + " " + startNode + "}");
		}
		for (QueryRow row : table) {
			Node node = row.getValue("a");// .toString();// in der Hashmap das
			String keyurl = Rdf2GoUtils.getLocalName(node); // Praedikat
			try {
				keyurl = URLDecoder.decode(keyurl, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			String key = keyurl.substring(keyurl.indexOf("=") + 1);// hier wird
			if (!key.contains("Resource")) {
				connectedNodesList.add(key);
			}

		}

		return connectedNodesList;

	}

	public List<String> getConectedObject(String connection, boolean from) {

		LinkedList<String> result = new LinkedList<String>();
		connection = createSparqlURI(connection);
		QueryResultTable table = null;
		if (from) {
			table = Rdf2GoCore.getInstance().sparqlSelect(
					"SELECT ?a WHERE { ?a lns:" + connection + " ?b}");
		}
		else {
			table = Rdf2GoCore.getInstance().sparqlSelect(
					"SELECT ?a WHERE { ?b lns:" + connection + " ?a}");
		}
		for (QueryRow row : table) {
			Node node = row.getValue("a");// .toString();// in der Hashmap das
			String keyurl = Rdf2GoUtils.getLocalName(node); // Praedikat
			try {
				keyurl = URLDecoder.decode(keyurl, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			String key = keyurl.substring(keyurl.indexOf("=") + 1);// hier wird
			if (!key.contains("Resource")) {
				result.add(key);
			}

		}

		return result;

	}

	public static int countQuerytresults(String concept) {
		QueryResultTable table = getAllConnections(concept, false);
		int i = 0;
		for (@SuppressWarnings("unused")
		QueryRow queryRow : table) {
			i++;
		}
		return i;
	}

	public static String countQuerytresultstoString(String concept) {
		QueryResultTable table = getAllConnections(concept, false);
		int i = 0;
		for (@SuppressWarnings("unused")
		QueryRow queryRow : table) {
			i++;
		}
		return i + "";
	}

	private static QueryResultTable getAllConnections(String concept,
													  boolean reverse) {
		QueryResultTable table = null;
		try {
			concept = URLDecoder.decode(concept, "UTF-8");
		}
		catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String sparqlConcept = createSparqlURI(concept);
		System.out
				.println("SELECT ?a ?b WHERE {  " + sparqlConcept + " ?a ?b}");
		if (reverse) {
			table = Rdf2GoCore.getInstance().sparqlSelect(
					"SELECT ?a ?b WHERE {  ?b ?a  " + sparqlConcept + "}");
		}
		else {
			table = Rdf2GoCore.getInstance().sparqlSelect(
					"SELECT ?a ?b WHERE {  " + sparqlConcept + " ?a ?b}");
		}
		return table;
	}

	private static LinkedList<String[]> querytableToList(QueryResultTable table) {
		LinkedList<String[]> result = new LinkedList<String[]>();
		for (QueryRow queryRow : table) {
			Node node = queryRow.getValue("a");
			String connectionType = MarkupUtils.getConceptName(node)
					.toExternalForm();
			node = queryRow.getValue("b");
			String connectedNode = MarkupUtils.getConceptName(node)
					.toExternalForm();
			String[] pair = {
					connectionType, connectedNode };
			if (!connectionType.contains("rdf-")) {
				result.add(pair);
			}
		}
		return result;
	}

	public static Connections getConnectionObject(String concept) {
		QueryResultTable out = getAllConnections(concept, false);
		QueryResultTable in = getAllConnections(concept, true);
		LinkedList<String[]> outList = querytableToList(out);
		LinkedList<String[]> inList = querytableToList(in);
		return new Connections(inList, outList);
	}

	/**
	 * This Method tries to get the first Object of a Chain in Order to get the whole DB connected by this Type
	 *
	 * @param relationType Defines the type of the connection to the desired Chain member
	 * @return
	 * @created 12.03.2013
	 */
	public static String getFirstObjectOfChain(String relationType) {

		return relationType;

	}

	// before CCC

	private static String createSparqlURI(String name) {
		name = Strings.unquote(name);
		Collection<Section<? extends SimpleDefinition>> definitions = IncrementalCompiler
				.getInstance().getTerminology()
				.getTermDefinitions(new Identifier(name));
		if (definitions.size() > 0) {
			Section<? extends SimpleDefinition> def = definitions.iterator()
					.next();
			return "<" + RDFSUtil.getURI(def) + ">";
		}
		name = name.replaceAll(" ", "+");
		if (name.contains("+") || name.contains(".")) {
			String localNamespace = Rdf2GoCore.getInstance()
					.getLocalNamespace();

			return "<" + localNamespace + name + ">";
		}

		try {
			return "lns:" + URLDecoder.decode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	// liefert den Parent Node, fuer Breadcrubs verwenden
	public static String getRootConcept(String startConcept,
										String connectionType) {
		do {
			ArrayList<String> parent = (ArrayList<String>) getConnectedNodeNamesOfType(
					startConcept, connectionType, true);
			if (!parent.isEmpty()) {
				startConcept = parent.get(0);
			}
			else {
				break;
			}
		} while (true);

		return startConcept;

	}

	public static boolean conceptIsInHierachy(String concept) {
		List<String> matches = DataBaseHelper.getConnectedNodeNamesOfType(
				concept, "unterkonzept", true);
		matches.addAll(DataBaseHelper.getConnectedNodeNamesOfType(concept,
				"unterkonzept", false));
		if (matches.size() > 0) return true;
		else return false;
	}

	public static Map<String, String> getAllObjectsConnectedBy(
			String connectionType) {
		Map<String, String> result = new HashMap<String, String>();
		QueryResultTable table = null;
		System.out
				.println("SELECT ?a ?b WHERE { ?a " + connectionType + " ?b}");
		table = Rdf2GoCore.getInstance().sparqlSelect(
				"SELECT ?a ?b WHERE { ?a lns:" + connectionType + " ?b}");

		for (QueryRow row : table) {
			// Child
			Node node = row.getValue("a");
			// Parent
			Node node2 = row.getValue("b");
			String childConcept = MarkupUtils.getConceptName(node)
					.toExternalForm();
			String parentConcept = MarkupUtils.getConceptName(node2)
					.toExternalForm();

			// String keyurl = Rdf2GoUtils.getLocalName(node);
			// String keyurl2 = Rdf2GoUtils.getLocalName(node2);
			// try {
			// keyurl = URLDecoder.decode(keyurl, "UTF-8");
			// keyurl2 = URLDecoder.decode(keyurl2, "UTF-8");
			// }
			// catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			// }
			// Rdf2GoUtils.
			// String key = keyurl.substring(keyurl.indexOf("=") + 1);
			// String key2 = keyurl2.substring(keyurl2.indexOf("=") + 1);
			// if (!key.contains("Resource")) {
			result.put(childConcept, parentConcept);

		}
		return result;
	}

}
