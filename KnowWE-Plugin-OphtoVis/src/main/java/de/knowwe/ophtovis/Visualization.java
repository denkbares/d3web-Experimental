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

import de.knowwe.core.utils.Strings;


/**
 * 
 * @author adm_rieder
 * @created 07.11.2012
 */
public class Visualization {

	public static String visualize(String concept) {

		StringBuffer string = new StringBuffer();
		// wird nicht eingesetzt
		// String[] connection = {
		// "lns:unterkonzept", "lns:assoziation",
		// "lns:assoziationBidirektional", "lns:kann",
		// "lns:kannBidirektional", "lns:muss", "lns:temporalBevor" };

		// Startknoten
		String start = concept;

		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
				+ start);
		// -------------------------------------------------------------------------------------------
		// DataBaseHelper Helper Action
		// GraphBuilder Action
		// DataBaseHelper dbh = new DataBaseHelper();
		GraphBuilder gB = new GraphBuilder(start);
		gB.buildGraph();

		// int leftCo = gB.getStartLeftCo();
		// int topCo = gB.getStartTopCo();
		// int id = 0;
		// String stringID = "stringID";

		// ------------------------------------------------------------------------------------------------------------------------------
		// Startknoten
		// Node<Name, LeftCo, TopCo> startNode = new Node<Name, LeftCo,
		// TopCo>(start, leftCo, topCo);

		// GraphNode startNode = gB.buildStartNode(start);

		// leftCo += gB.getAdd2Left();
		// topCo += gB.getAdd2Top();

		// System.out.println("leftCo----------------------------------------------------------------------"
		// + leftCo);

		// ------------------------------------------------------------------------------------------------------------------------------

		// List<List<String>> listOfConnectedNodesLists = new
		// ArrayList<List<String>>();

		// List<String> connectedNodesList = gB.getConnectedNodesOfType(start,
		// "unterkonzept");
		// TODO

		// if (connectedNodesList.isEmpty()) {
		// // do nothing
		// }
		// else {
		//
		// }

		// List<GraphNode> nodeAndCoList =
		// gB.buildNodeAndCoList(connectedNodesList, leftCo, topCo);

		// ------------------------------------------------------------------------------------------------------------------------------

		// System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
		// + nodeAndCoList.size());
		//
		// System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
		// + nodeAndCoList.get(0).getName());
		//
		// System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
		// + nodeAndCoList.get(1).getName());
		// --------------------------------------------------------------------------------------------

		// design der knoten und connections
		String initDesign = "initDesign();";

		// der erste knoten position am bildschirm, der mittelknoten

		String knoten = "";
		//
		// // zeichne den Startknoten
		//
		// knoten += "createKnoten(\"" + startNode.getName() + "\", " +
		// startNode.getLeftCo() + ","
		// + startNode.getTopCo() + "," + startNode.getId() + "," + "\""
		// + startNode.getStringID() + "\""
		// + ");";

		// zeichne alle nachfolger knoten mit ihren berechneten positionen

		for (GraphNode node : gB.getNodeAndCoList()) {

			String name = node.getName();
			name = name.replace(" ", "_");
			int left = node.getLeftCo();
			int top = node.getTopCo();
			// int id1 = node.getId();
			String stringID = node.getStringID();
			// String stringID1 = "stringID" + id1;

			// System.out.println("node1-------------------------------------------------------------------: "
			// + id1);

			// System.out.println("stringnode1-------------------------------------------------------------------: "
			// + stringID1);

			knoten += "createKnoten(\"" + name + "\", " + left + ","
					+ top + "," + "\"" + stringID + "\"" + ");\r\n";

			// knoten += "connectKnoten(" + startNode.getStringID() + "," + "\""
			// + stringID1 + "\""
			// + ");\r\n";

		}

		LinkedList<GraphNodeConnection> listOfConnections = (LinkedList<GraphNodeConnection>) gB.getConnections();

		for (GraphNodeConnection graphNodeConnection : listOfConnections) {

			knoten += "connectKnoten(\"" + graphNodeConnection.sourceNode.getStringID() + "\","
					+ "\""
					+ graphNodeConnection.targetNode.getStringID() + "\""
					+ ");\r\n";
		}

		string.append(Strings.maskHTML(
				"	<link rel=\"stylesheet\" href=\"/KnowWE/KnowWEExtension/prototyp2.css\">"
						+
						"	<!--  Script importe -->	\r\n"
						+
						"	<!--  JSPLUMB -->\r\n"
						+
						"	<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js\"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.13/jquery-ui.min.js\"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\"/KnowWE/KnowWEExtension/jquery.jsPlumb-1.3.15-all-min.js \"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\"http://yui.yahooapis.com/3.3.0/build/simpleyui/simpleyui-min.js\"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\"/KnowWE/KnowWEExtension/jquery.js\"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\"/KnowWE/KnowWEExtension/jquery-ui.js\"></script>	\r\n"
						+
						"	<script type=\"text/javascript\" src=\"/KnowWE/KnowWEExtension/nodeCreatin.js \"></script>\r\n"
						+
						"	<script type=\"text/javascript\" src=\"/KnowWE/KnowWEExtension/initDefaultDesign.js \"></script>\r\n"
						+
						" <!-- im body befindet sich der sichtbare bereich der seite -->\r\n"
						// +
						// "	<div id=\"jsPlumb_1_4\" style=\"position:relative;margin-top:100px;\">\r\n"
						+
						"	<div id=\"demonstration\">\r\n"
						+
						"	<div id=knots>\r\n"
						+
						"	</div>"
						+
						"<script type=\"text/javascript\">"
						// +
						// "ajax();"
						+
						initDesign
						+
						"</script>"

						+
						"<script type=\"text/javascript\">"
						+
						knoten
						+
						"</script>"
						+
						"	</div>"
						+
						"	</div>\r\n"
						+
						// Starte script - zugemacht wird es in verbindungen
						// weiter oben
						"<script type=\"text/javascript\">"

						+
						"</script>"
				));

		return string.toString();
	}

}
