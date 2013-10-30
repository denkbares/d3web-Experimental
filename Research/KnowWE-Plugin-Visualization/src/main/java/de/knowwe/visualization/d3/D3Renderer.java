/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.visualization.d3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.visualization.Edge;
import de.knowwe.visualization.GraphDataBuilder;
import de.knowwe.visualization.SubGraphData;
import de.knowwe.visualization.util.IncludeUtils;
import de.knowwe.visualization.util.IncludeUtils.FILE_TYPE;

/**
 * 
 * 
 * @author JohannaLatt
 * @created 25.06.2013
 */
public class D3Renderer {

	private static String htmlsource = "";
	private static String jsonSource = "";
	private static String arraySource = "";
	private static String arrayLinks = "";

	private static String context;

	// sectionID in paramter rein schreiben (im markup bekannt) und div dann so
	// benennen (id dann
	// mit ins javascript übergeben)
	public static String createD3HTMLSource(SubGraphData data, Map<String, String> parameters) {
		context = Environment.getInstance().getWikiConnector().getServletContext().getContextPath();

		String visualization = parameters.get(GraphDataBuilder.VISUALIZATION);

		htmlsource = "<div id=\"d3" + parameters.get(GraphDataBuilder.SECTION_ID)
				+ "\" style=\"overflow:scroll\">\r\n";

		// include the necessary d3.js sources
		htmlsource += IncludeUtils.includeFile(FILE_TYPE.JAVASCRIPT, context
				+ "/KnowWEExtension/scripts/d3.v3.js");

		// default visualization: wheel
		if (visualization != null && visualization.equals("force")) {
			drawForce(data, parameters);
		}
		else {
			drawWheel(data, parameters);
		}

		htmlsource += "</div>";
		return htmlsource;
	}

	private static void drawWheel(SubGraphData data, Map<String, String> parameters) {
		// write the JSON source for the wheel-visualization
		String concept = parameters.get(GraphDataBuilder.CONCEPT);
		if (concept == null) return;
		writeJSONWheelSource(data, concept);

		// include all necessary scripts and files
		htmlsource += IncludeUtils.includeFile(FILE_TYPE.JAVASCRIPT, context
				+ "/KnowWEExtension/scripts/d3wheel.js");
		htmlsource += IncludeUtils.includeFile(FILE_TYPE.CSS, context
				+ "/KnowWEExtension/css/d3wheel.css");

		// draw the wheel-visualization
		htmlsource += "<script>";
		htmlsource += " drawWheel("
				+ parameters.get(GraphDataBuilder.GRAPH_SIZE)
				+ ", " + jsonSource
				+ ", " + "\"" + parameters.get(GraphDataBuilder.SECTION_ID) + "\""
				+ ") ";
		htmlsource += "</script>";
	}

	private static void drawForce(SubGraphData data, Map<String, String> parameters) {
		// write the JSON source for the force-visualization
		writeJSONForceSource(data);

		// include all necessary scripts and files
		htmlsource += IncludeUtils.includeFile(FILE_TYPE.JAVASCRIPT, context
				+ "/KnowWEExtension/scripts/d3force.js");
		htmlsource += IncludeUtils.includeFile(FILE_TYPE.CSS, context
				+ "/KnowWEExtension/css/d3force.css");

		// draw the force-visualization
		htmlsource += "<script>";
		htmlsource += " drawForce("
				+ parameters.get(GraphDataBuilder.GRAPH_SIZE)
				+ ", " + arraySource
				+ ", " + arrayLinks
				+ ", " + "\"" + GraphDataBuilder.createBaseURL() + "\""
				+ ", " + "\"" + parameters.get(GraphDataBuilder.TITLE) + "\""
				+ ", " + "\"" + parameters.get(GraphDataBuilder.SECTION_ID) + "\""
				+ ")";
		htmlsource += "</script>";
	}

	/**
	 * Writes the JSON source for the wheel visualization.
	 * 
	 * @created 20.06.2013
	 * @param data
	 * @param the main concept on which the data bases on
	 */
	private static void writeJSONWheelSource(SubGraphData data, String concept) {
		jsonSource = "{\n";
		jsonSource += "\"concept\": \"" + concept + "\"";

		HierarchyTree tree = new HierarchyTree(data.getConcept(concept), data);
		HierarchyNode root = tree.getRoot();

		if (root.hasChildren()) {
			addChildrenToSource(root);
		}

		jsonSource += "\n}";
	}

	/**
	 * Adds the children of the given HierarchyNode to the jsonSource
	 * 
	 * @created 25.06.2013
	 * @param root
	 */
	private static void addChildrenToSource(HierarchyNode root) {
		jsonSource += ",\n\"children\": [\n";
		List<HierarchyNode> children = root.getChildren();
		Iterator<HierarchyNode> iterator = children.iterator();
		while (iterator.hasNext()) {
			HierarchyNode next = iterator.next();
			// if the child is not in the source yet: Add it so source and loop
			// through it's children
			if (!next.isInSourceYet()) {
				jsonSource += "{\"concept\": \"" + next.getName() + "\"";
				next.setIsInSourceYet(true);
				if (next.hasChildren()) {
					addChildrenToSource(next);
				}
				jsonSource += "}";
			}
			// ...otherwise only add the child but don't go further in the tree
			// (-> endless loop)
			else {
				jsonSource += "{\"concept\": \"" + next.getName() + "\"\n}";
			}
			if (iterator.hasNext()) {
				// not last element yet
				jsonSource += ",\n";
			}
		}
		jsonSource += "\n]";
	}

	/**
	 * Writes the JSON source for the force visualization.
	 * 
	 * @created 08.07.2013
	 * @param data
	 */
	private static void writeJSONForceSource(SubGraphData data) {
		List<String> links = new ArrayList<String>();

		// SOURCE
		arraySource = "[\n";
		Iterator<Edge> iterator = data.getEdges().iterator();
		while (iterator.hasNext()) {
			Edge next = iterator.next();
			String p = next.getPredicate();

			String subjectLabel = next.getSubject().getConceptLabel();
			if (subjectLabel == null) {
				subjectLabel = next.getSubject().getName();
			}

			String objectLabel = next.getObject().getConceptLabel();
			if (objectLabel == null) {
				objectLabel = next.getObject().getName();
			}

			arraySource += "{source: \"" + subjectLabel + "\", ";
			arraySource += "target: \"" + objectLabel + "\", ";
			arraySource += "type: \"" + p + "\"}";
			if (iterator.hasNext()) {
				arraySource += ",";
			}
			arraySource += "\n";

			if (!links.contains(p)) {
				links.add(p);
			}
		}
		arraySource += "]";

		// LINKS
		arrayLinks = "[";
		Iterator<String> iter = links.iterator();
		while (iter.hasNext()) {
			String next = iter.next();
			arrayLinks += " \"" + next + "\"";
			if (iter.hasNext()) {
				arrayLinks += ",";
			}
		}
		arrayLinks += "]\n";
	}
}
