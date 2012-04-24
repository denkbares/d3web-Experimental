package de.d3web.we.diaflux.pathcoloring;

import java.util.HashMap;
import java.util.Map;

import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Node;


public class HighlightSessionObject implements SessionObject {

	private final Map<Edge, Integer> edges;
	private final Map<Node, Integer> nodes;

	public HighlightSessionObject() {
		edges = new HashMap<Edge, Integer>();
		nodes = new HashMap<Node, Integer>();
	}

	public void addTracedNode(Node node) {
		if (!nodes.containsKey(node)) {
			nodes.put(node, 0);
		}

		nodes.put(node, nodes.get(node) + 1);
	}

	public void addTracedEdge(Edge edge) {
		if (!edges.containsKey(edge)) {
			edges.put(edge, 0);
		}

		edges.put(edge, edges.get(edge) + 1);
	}

	protected Map<Edge, Integer> getEdgeCounts() {
		return edges;
	}

	protected Map<Node, Integer> getNodeCounts() {
		return nodes;
	}



}