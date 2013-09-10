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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author adm_rieder
 * @created 22.10.2012
 */
public class GraphBuilder {

	static GraphBuilder getInstance() {
		return new GraphBuilder("");
	}

	public GraphBuilder(String startNodeName) {
		this.startNodeName = startNodeName;
	}

	private final int startLeftCo = 200;
	private final int add2Left = 250;
	int leftCo = startLeftCo;
	int leftCoMax = 0;
	int id = 1;

	List<String> temporalConnection;
	String startNodeName;

	List<GraphNode> nodeAndCoList = new LinkedList<GraphNode>();

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

	public void buildGraph() {

		// Startknoten
		GraphNode startNode = buildNode(startNodeName, getStartLeftCo(), 1, false);

		do {
			nodeAndCoList.add(startNode);
			buildNodeAndCoList(startNode, startNode.getLeftCo() + add2Left, startNode.getId(), true);
			temporalConnection = DataBaseHelper.getConnectedNodeNamesOfType(
					startNode.getName(), "temporalGraph", true);
			if (!temporalConnection.isEmpty()) {
				GraphNode tempNode = buildNode(temporalConnection.get(0), leftCoMax + add2Left,
						++id, false);
				connections.add(new GraphNodeConnection(startNode, tempNode, "temporal"));
				startNode = tempNode;
			}
		} while (!temporalConnection.isEmpty());

	}

	// Starknoten bauen

	public GraphNode buildNode(String nodeName, int leftCor, int parentId, boolean haveChildren) {
		GraphNode startNode = new GraphNode(nodeName, leftCor, id++, parentId, haveChildren);
		return startNode;
	}

	public List<GraphNode> buildNodeAndCoList(GraphNode node, int leftCo, int parentID, boolean getChilds)
	{
		String nodeName = node.getName();
		id = node.getId();
		// TODO connectionTypes
		List<String> childrenNames = DataBaseHelper.getConnectedNodeNamesOfType(nodeName,
				"unterkonzept", false);

		node.hasChildren = !childrenNames.isEmpty();
		boolean first = true;
		GraphNode node2add;
		for (String name : childrenNames) {
			if (first) {
				node2add = new GraphNode(name, leftCo, ++id, parentID,
						false);
				first = false;
			}
			else {
				node2add = new GraphNode(name, leftCo, ++id, node.getId(),
						false);
			}
			nodeAndCoList.add(node2add);
			connections.add(new GraphNodeConnection(node, node2add, "unterkonzept"));
			String queryName = "";
			try {
				queryName = URLEncoder.encode(name, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			List<String> nextChildrenOfNode = DataBaseHelper.getConnectedNodeNamesOfType(
					queryName, "unterkonzept", false);
			if (!nextChildrenOfNode.isEmpty()) {

				node2add.setHasChildren(true);
				int leftCoAct = leftCo + add2Left;
				this.leftCo = leftCoAct;
				if (leftCoAct > leftCoMax) {
					leftCoMax = leftCoAct;
				}
				if (getChilds) {
					buildNodeAndCoList(node2add, leftCoAct, id, getChilds);
				}
			}
		}

		return nodeAndCoList;

	}

	public int getAdd2Left() {
		return add2Left;
	}

	public int getStartLeftCo() {
		return startLeftCo;
	}

	public void buildMinGraph() {

		// Startknoten
		GraphNode startNode = buildNode(startNodeName, getStartLeftCo(), 1, false);

		do {
			nodeAndCoList.add(startNode);
			buildNodeAndCoList(startNode, startNode.getLeftCo() + add2Left, startNode.getId(),
					false);
			temporalConnection = DataBaseHelper.getConnectedNodeNamesOfType(
					startNode.getName(), "temporalGraph", true);
			if (!temporalConnection.isEmpty()) {
				GraphNode tempNode = buildNode(temporalConnection.get(0), leftCoMax + add2Left,
						++id, false);
				connections.add(new GraphNodeConnection(startNode, tempNode, "temporal"));
				startNode = tempNode;
			}
		} while (!temporalConnection.isEmpty());

	}

}
