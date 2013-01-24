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
package de.knowwe.ophtovis;

import java.util.LinkedList;

import de.knowwe.core.Environment;
import de.knowwe.core.utils.Strings;


/**
 * 
 * @author adm_rieder
 * @created 07.11.2012
 */
public class Visualization {

	public static String visualize(String concept) {

		StringBuffer string = new StringBuffer();
		String start = concept;
		GraphBuilder gB = new GraphBuilder(start);
		gB.buildGraph();
		String initDesign = "initDesign();";
		String knoten = "";
		LinkedList<Integer> drawnLists = new LinkedList<Integer>();
		LinkedList<GraphNodeConnection> listOfConnections = (LinkedList<GraphNodeConnection>) gB.getConnections();
		for (GraphNode node : gB.getNodeAndCoList()) {
			int source = getSoureOf(node.getParentId(), listOfConnections);
			if (!drawnLists.contains(source)) {
				if (node.getParentId() == node.getId()) {
					knoten += "createULRelative(\"e" + node.getId() + "\",\"knots\", \""
							+ node.getLeftCo() + "px\" , \"" + node.getTopCo() + "px\");\r\n";
				}
				else {
					if (source == -1) {
						if (!drawnLists.contains(node.getParentId())) {
						knoten += "createUL(" + node.getParentId() + ",\"e" + node.getParentId()
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
			int top = node.getTopCo();
			String stringID = node.getStringID();
			int parentID = node.getParentId();

			if (node.hasChildren) {
				if (!(node.getParentId() == node.getId())) {
					knoten += "createKnoten(\"" + name + "\", " + left + ","
							+ top + "," + "\"" + stringID + "\"," + parentID + ");\r\n";
				}
				else {
					knoten += "createKnoten(\"" + name + "\", " + left + ","
							+ top + "," + "\"" + stringID + "\"," + "\"e" +
							parentID + "\");\r\n";
				}
			}
			else {

				if (!(node.getParentId() == node.getId())) {
					knoten += "createLeafNode(\"" + name + "\", " + left + ","
							+ top + "," + "\"" + stringID + "\"," + parentID + ");\r\n";
				}
				else {
					knoten += "createLeafNode(\"" + name + "\", " + left + ","
							+ top + "," + "\"" + stringID + "\"," + "\"e" +
							parentID + "\");\r\n";
				}

			}

		}



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
		String context = Environment.getInstance().getWikiConnector().getServletContext().getContextPath();

		string.append(Strings.maskHTML(
				"	<link rel=\"stylesheet\" href=\""
						+ context
						+ "/KnowWEExtension/prototyp2.css\">"
						+
						"	<!--  Script importe -->	\r\n"
						+
						"	<!--  JSPLUMB -->\r\n"
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
						+ "/KnowWEExtension/nodeCreatin.js \"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\""
						+ context
						+ "/KnowWEExtension/initDefaultDesign.js \"></script>\r\n"
						+
						" <!-- im body befindet sich der sichtbare bereich der seite -->\r\n"
						+
						"	<div id=\"demonstration\">\r\n"
						// zoom buttons
						+ "<input type=\"button\" value=\"+\" onClick=\"zoomIn()\"/>"
						+ "<input type=\"button\" value=\"-\" onClick=\"zoomOut()\"/>"
						//
						+
						"	<div id=knots>\r\n"
						+
						"	</div>"
						+
						"<script type=\"text/javascript\">"
						+
						initDesign
						+
						"</script>\r\n"

						+
						"<script type=\"text/javascript\">\r\n"
						+
						knoten
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
				));

		return string.toString();
	}

	static int getSoureOf(int target, LinkedList<GraphNodeConnection> links) {
		for (GraphNodeConnection connection : links) {
			if (connection.connectionType.matches("unterkonzept")) {
			if (connection.getTargetID() == target) return connection.getSourceID();
			}
		}
		return -1;

	}

}
