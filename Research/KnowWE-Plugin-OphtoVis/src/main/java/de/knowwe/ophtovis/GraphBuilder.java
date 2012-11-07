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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 
 * @author adm_rieder
 * @created 22.10.2012
 */
public class GraphBuilder {
	
	public GraphBuilder(String startNodeName) {
		this.startNodeName = startNodeName;
	}

	// FINALE Start-Koordinaten
	// left: 0px; top: 75px ist ca ganz oben links
	private final int startLeftCo = 0;
	private final int startTopCo = 75;

	// je weiteren knoten müssen die koordinaten verschoben werden
	private final int add2Left = 250;
	private final int add2Top = 50;

	// laufende Coordinaten
	int leftCo = startLeftCo;
	int topCo = startTopCo;

	int id = 0;

	List<String> temporalConnection;
	String startNodeName;
	
	List<GraphNode> nodeAndCoList = new ArrayList<GraphNode>();
	
	public List<GraphNode> getNodeAndCoList() {
		return nodeAndCoList;
	}

	public void setNodeAndCoList(List<GraphNode> nodeAndCoList) {
		this.nodeAndCoList = nodeAndCoList;
	}

	public List<GraphNodeConnection> getConnections() {
		return connections;
	}

	public void setConnections(List<GraphNodeConnection> connections) {
		this.connections = connections;
	}

	List<GraphNodeConnection> connections = new LinkedList<GraphNodeConnection>();
	


	// Initialisierung
	
	public void buildGraph(){
		
		// Startknoten
		GraphNode startNode = buildNode(startNodeName, getStartLeftCo(), getStartTopCo());

		do {
		// Startknoten hinzugefügt
		nodeAndCoList.add(startNode);
		//
		// parent id 0;
		buildNodeAndCoList(startNode, leftCo, 0);
			temporalConnection = DataBaseHelper.getConnectedNodeNamesOfTypeReverse(
					startNode.getName(), "temporalBevor");
			System.out.println("tempconsinze" + temporalConnection.size() + startNode.getName());
			if (!temporalConnection.isEmpty()) {
				topCo = getStartTopCo();
				GraphNode tempNode = buildNode(temporalConnection.get(0), leftCo, getStartTopCo());
				connections.add(new GraphNodeConnection(startNode, tempNode, "temporal"));
				startNode = tempNode;
			}
		} while (!temporalConnection.isEmpty());
		
	}
	
	

	// Starknoten bauen

	public GraphNode buildNode(String nodeName, int leftCor, int topCor) {

		GraphNode startNode = new GraphNode(nodeName, leftCor, topCor, id++);

		leftCo += add2Left;
		topCo += add2Top;

		return startNode;

	}

	// -----------------------------------------------------------------------------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	public List<GraphNode> buildNodeAndCoList(GraphNode node, int leftCo, int parentID)
	{
		// für alle knoten in connectedNodesList werden die Koordinaten
		// berechnet
		// angefangen beim Startknoten mit Startwerten


		String nodeName = node.getName();
		// TODO connectionTypes
		// hole liste mit nachfolgerknoten
		List<String> childrenNames = DataBaseHelper.getConnectedNodeNamesOfType(nodeName,
				"unterkonzept");

		// wenn childrennames leer ist wird die forschleife nicht durchlaufen
		for (String string : childrenNames) {

			GraphNode node2add = new GraphNode(string, leftCo, topCo, id++);

			nodeAndCoList.add(node2add);

			connections.add(new GraphNodeConnection(node, node2add, "unterkonzept"));

			// nachdem Hinzufügen des Knotens, aendert sich die topCo Coordinate
			topCo += add2Top;

			// TODO
			// Textformatierungen
			String oldFormatName = "";

			try {
				oldFormatName = URLEncoder.encode(node.getName(), "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			// Hat der gerade eingefuegte Knoten auch Unterkonzepte

			List<String> nextChildrenOfNode = DataBaseHelper.getConnectedNodeNamesOfType(
					oldFormatName,
					"unterkonzept");

			System.out.println("nextChildrenOfNode.size()------------------------------"
					+ nextChildrenOfNode.size());
			System.out.println("node.getName()----------------------------------------------------"
					+ node.getName());
			// wenn der gerade eingefuegte nachfolger hat dann
			if (!nextChildrenOfNode.isEmpty()) {

				// bilde vom hinzugefuegten die node and co list
				buildNodeAndCoList(node2add, leftCo + add2Left, id);

			}


		}// forschleife

		return nodeAndCoList;

	}
	
	public int getAdd2Left() {
		return add2Left;
	}

	public int getAdd2Top() {
		return add2Top;
	}

	public int getStartLeftCo() {
		return startLeftCo;
	}

	public int getStartTopCo() {
		return startTopCo;
	}

	// -----------------------------------
//	public void buildGraph(String startNode, int startLeft, int startTop, String conType) {
//
//		List<String> connectedNodesList = new ArrayList<String>();
//
//		connectedNodesList = getConnectedNodesOfType(startNode, conType);
//
//		List<GraphNode> nodeAndCoList = buildNodeAndCoList(connectedNodesList, leftCo, topCo);
//
//		if (connectedNodesList.size() > 1) {
//
//			// es gibt keine weiteren Nachfolger vom Typ conType,
//			// der Startknoten ist der einzige Knoten
//		}
//		else {
//			// es gibt keine weiteren Nachfolger vom Typ conType,
//			// der Startknoten ist der einzige Knoten
//
//			GraphNode<String, Integer, Integer> node = new GraphNode<String, Integer, Integer>(
//					startNode,
//					startLeft, startTop);
//
//		}

	// }
	


}
