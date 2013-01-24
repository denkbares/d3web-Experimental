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

	private final int startLeftCo = 200;
	private final int startTopCo = 75;
	private final int add2Left = 250;
	private final int add2Top = 50;
	int leftCo = startLeftCo;
	int topCo = startTopCo;
	int leftCoMax = 0;
	int id = 1;

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
		GraphNode startNode = buildNode(startNodeName, getStartLeftCo(), getStartTopCo(), 1, false);

		do {
			nodeAndCoList.add(startNode);
			buildNodeAndCoList(startNode, startNode.getLeftCo() + add2Left, startNode.getId());
			temporalConnection = DataBaseHelper.getConnectedNodeNamesOfType(
					startNode.getName(), "temporalGraph", true);
			if (!temporalConnection.isEmpty()) {
				topCo = getStartTopCo();
				GraphNode tempNode = buildNode(temporalConnection.get(0), leftCoMax + add2Left,
						getStartTopCo(), ++id, false);
				connections.add(new GraphNodeConnection(startNode, tempNode, "temporal"));
				startNode = tempNode;
			}
		} while (!temporalConnection.isEmpty());
		
	}
	
	

	// Starknoten bauen

	public GraphNode buildNode(String nodeName, int leftCor, int topCor, int parentId, boolean haveChildren) {

		GraphNode startNode = new GraphNode(nodeName, leftCor, topCor, id++, parentId, haveChildren);
		topCo += add2Top;

		return startNode;

	}

	// -----------------------------------------------------------------------------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	public List<GraphNode> buildNodeAndCoList(GraphNode node, int leftCo, int parentID)
	{
		String nodeName = node.getName();
		// TODO connectionTypes
		List<String> childrenNames = DataBaseHelper.getConnectedNodeNamesOfType(nodeName,
				"unterkonzept", false);

		boolean hasChildren = false;

		if (!childrenNames.isEmpty()) {
			hasChildren = true;
		}

		node.setHasChildren(hasChildren);

		for (String string : childrenNames) {
			GraphNode node2add = new GraphNode(string, leftCo, topCo, ++id, node.getId(),
					false);
			System.out.println("neuer node cor " + leftCo);
			nodeAndCoList.add(node2add);
			connections.add(new GraphNodeConnection(node, node2add, "unterkonzept"));
			topCo += add2Top;
			// TODO Textformatierungen
			String oldFormatName = "";
			try {
				oldFormatName = URLEncoder.encode(node.getName(), "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			List<String> nextChildrenOfNode = DataBaseHelper.getConnectedNodeNamesOfType(
					oldFormatName, "unterkonzept", false);
			if (!nextChildrenOfNode.isEmpty()) {

				node2add.setHasChildren(true);

				int leftCoAct = leftCo + add2Left;
				this.leftCo = leftCoAct;
				if (leftCoAct > leftCoMax) {
					leftCoMax = leftCoAct;
				}
				buildNodeAndCoList(node2add, leftCoAct, id);
			}
		}

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


}
