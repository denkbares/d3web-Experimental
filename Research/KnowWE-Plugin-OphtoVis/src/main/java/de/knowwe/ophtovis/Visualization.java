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
package de.knowwe.ophtovis;

import java.util.LinkedList;
import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.rendering.RenderResult;

/**
 * 
 * @author adm_rieder
 * @created 07.11.2012
 */
public class Visualization {

	public static void visualize(String concept, boolean min, RenderResult result) {
		String commands = "";
		GraphBuilder gB = new GraphBuilder(concept);
		if (min) {
			gB.buildMinGraph();
		}
		else {
			gB.buildGraph();
		}
		LinkedList<GraphNodeConnection> listOfConnections = (LinkedList<GraphNodeConnection>) gB.getConnections();
		commands += convertNodelistToJS(gB.getNodeAndCoList(), listOfConnections);
		commands += convertConnectionlistToJS(listOfConnections);
		appendHtmlWrap(commands, result);
	}

	/**
	 * Adds some of the Basic Websitefeatures with are common in all the
	 * Graphvisualisations
	 * 
	 */
	public static void appendHtmlWrap(String jsCommands, RenderResult result) {
		String context = Environment.getInstance().getWikiConnector().getServletContext().getContextPath();
		result.appendHTML(
				"	<link rel=\"stylesheet\" href=\""
						+ context
						+ "/KnowWEExtension/prototyp2.css\">"
						+
						"	<!--  Script importe -->	\r\n"
						+
						"	<!--  JSPLUMB -->\r\n"
						+
						"<script type=\"text/javascript\" src=\"/KnowWE/scripts/mootools.js\"></script>"
						+
						"	<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js\"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/jquery-ui.min.js\"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/jquery.jsPlumb-1.3.15-all-min.js \"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\"http://yui.yahooapis.com/3.3.0/build/simpleyui/simpleyui-min.js\"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/jquery.js\"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/jquery-ui.js\"></script>	\r\n"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/jquery.blockUI.js\"></script>	\r\n"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/nodeCreatin.js \"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/initDefaultDesign.js \"></script>\r\n"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/jquery-autosize.min.js'>"
						+
						"</script></script><script type='text/javascript' src='KnowWEExtension/scripts/jquery-compatibility.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-helper.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-notification.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/quicki.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/overviewGraph.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/correction.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/drag.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/jquery.treeTable.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-Rdf2GoSemanticCore.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-d3web-basic.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/testcaseplayer.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-CI4KE.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/loadQuicki.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/saveQuicki.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-Core.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/toolsMenuDecorator.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-InstantEdit.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-Plugin-AutoComplete.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/DefaultTableEditTool.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/DefaultEditTool.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/TextArea.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/KnowWE-EditCommons.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/TableEditTool.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/scripts/restoreActionScript.js'></script>"
						+
						"<script type='text/javascript' src='KnowWEExtension/jquery.blockUI.js'></script>"
						+
						"	<div id=\"demonstration\">\r\n"
						// zoom buttons
						+ "<input type=\"button\" value=\"+\" onClick=\"zoomIn()\"/>"
						+ "<input type=\"button\" value=\"-\" onClick=\"zoomOut()\"/>"
						+ "<input type=\"button\" value=\"Ajax\" onClick=\"testAjax()\"/>"
						+ "<input type=\"button\" value=\"AjaxVis\" onClick=\"startSparqlQuery()\"/>"
						//
						+
						//
						"	<div id=\"dropzone\" style=\"\r\n" +
						"    position: relative;\r\n" +
						"    left: 20%;\r\n" +
						"    margin: auto;\r\n" +
						"    width: 500;\r\n" +
						"    height: 100;\r\n" +
						"    color: red;\r\n" +
						"    background-color: #2EFEC8;\"\r\n" +
						"<p></p>" +
						"	</div>"
						//
						+
						"	<div id=knots>\r\n"
						+
						"	</div>"
						+
						"<script type=\"text/javascript\">initDesign();</script>\r\n"

						+
						"<script type=\"text/javascript\">\r\n"
						+
						jsCommands
						+
						"</script>"
						+
						"	</div>"
						+
						"	</div>\r\n"
						+
						"<script type=\"text/javascript\">"
						+
						"</script>"
				);
	}

	public static String convertConnectionlistToJS(LinkedList<GraphNodeConnection> listOfConnections) {
		String knoten = "";
		for (GraphNodeConnection graphNodeConnection : listOfConnections) {
			if (graphNodeConnection.connectionType.equalsIgnoreCase("temporal"))
			{
				knoten += "connectKnotenTemporal(\"" +
						graphNodeConnection.sourceNode.getStringID()
						+ "\","
						+ "\""
						+ graphNodeConnection.targetNode.getStringID() + "\""
						+ ");\r\n";

			}
			else {
				knoten += "connectKnoten(\"" + graphNodeConnection.sourceNode.getStringID() + "\","
						+ "\""
						+ graphNodeConnection.targetNode.getStringID() + "\""
						+ ");\r\n";

			}
		}
		return knoten;
	}

	static int getSoureOf(int target, LinkedList<GraphNodeConnection> links) {
		for (GraphNodeConnection connection : links) {
			if (connection.connectionType.matches("unterkonzept")) {
				if (connection.getTargetID() == target) return connection.getSourceID();
			}
		}
		return -1;
	}

	static String convertNodelistToJS(List<GraphNode> nodes, LinkedList<GraphNodeConnection> listOfConnections) {
		LinkedList<Integer> drawnLists = new LinkedList<Integer>();
		String knoten = "";
		for (GraphNode node : nodes) {
			int source = getSoureOf(node.getParentId(), listOfConnections);
			if (!drawnLists.contains(source)) {
				if (node.getParentId() == node.getId()) {
					knoten += "createULRelative(\"e" + node.getId() + "\",\"knots\", \""
							+ node.getLeftCo() + "px\" , \"" + 0 + "px\");\r\n";
				}
				else {
					if (source == -1) {
						if (!drawnLists.contains(node.getParentId())) {
							knoten += "createUL(" + node.getParentId() + ",\"e"
									+ node.getParentId()
									+ "\");\r\n";
							drawnLists.add(node.getParentId());
						}
					}
					else {
						knoten += "createUL(" + node.getParentId() + "," + source + ");\r\n";
						drawnLists.add(source);
					}
				}

			}
			else if (!drawnLists.contains(node.getParentId())) {
				knoten += "createUL(" + node.getParentId() + "," + source + ");\r\n";
				drawnLists.add(node.getParentId());
			}
			String name = node.getName();
			int left = node.getLeftCo();
			String stringID = node.getStringID();
			int parentID = node.getParentId();

			System.out.println("Knoten " + name + " hat Kinder " + node.hasChildren);

			if (node.hasChildren) {
				if (!(node.getParentId() == node.getId())) {
					knoten += "createKnoten(\"" + name + "\", " + left + ","
							+ "\"" + stringID + "\"," + parentID + ");\r\n";
				}
				else {
					knoten += "createKnoten(\"" + name + "\", " + left + ","
							+ "\"" + stringID + "\"," + "\"e" +
							parentID + "\");\r\n";
				}
			}
			else {

				if (!(node.getParentId() == node.getId())) {
					knoten += "createLeafNode(\"" + name + "\", " + left + ","
							+ "\"" + stringID + "\"," + parentID + ");\r\n";
				}
				else {
					knoten += "createLeafNode(\"" + name + "\", " + left + ","
							+ "\"" + stringID + "\"," + "\"e" +
							parentID + "\");\r\n";
				}

			}

		}
		return knoten;
	}

	static String convertNodelistToJSwithDrawnFather(List<GraphNode> nodes, LinkedList<GraphNodeConnection> listOfConnections, String id) {
		LinkedList<Integer> drawnLists = new LinkedList<Integer>();
		drawnLists.add(Integer.getInteger(id));
		int parentID = nodes.get(0).getId() - 1000;
		String knoten = "";
		for (GraphNode node : nodes) {
			int source = getSoureOf(node.getParentId(), listOfConnections);
			if (!drawnLists.contains(source)) {

				knoten += "createULAjax(" + (node.getId() - 1000) + "," + node.getParentId()
						+ " , " + id + ");\r\n";
				drawnLists.add(source);
			}

			String name = node.getName();
			int left = node.getLeftCo();
			String stringID = node.getStringID();

			System.out.println("Knoten " + name + " hat Kinder " + node.hasChildren);

			if (node.hasChildren) {
				knoten += "createKnoten(\"" + name + "\", " + left + ","
						+ "\"" + stringID + "\"," + parentID + ");\r\n";
			}
			else {
				knoten += "createLeafNode(\"" + name + "\", " + left + ","
						+ "\"" + stringID + "\"," + parentID + ");\r\n";
			}

		}
		return knoten;
	}
}
