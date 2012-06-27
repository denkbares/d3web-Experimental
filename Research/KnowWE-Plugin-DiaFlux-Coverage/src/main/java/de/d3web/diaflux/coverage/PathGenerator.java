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
package de.d3web.diaflux.coverage;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FlowchartProcessedCondition;
import de.d3web.diaFlux.inference.NodeActiveCondition;

/**
 * 
 * @author Reinhard Hatko
 * @created 26.03.2012
 */
public class PathGenerator {

	private final DFSStrategy strategy;
	private final KnowledgeBase kb;

	private final Collection<Path> startPaths;

	public PathGenerator(KnowledgeBase kb, DFSStrategy evaluator) {
		this.strategy = evaluator;
		this.kb = kb;

		this.startPaths = new HashSet<Path>();
	}

	public void createPaths() {

		startPaths.addAll(strategy.getInitialStartPaths());

		while (!startPaths.isEmpty()) {
			Path path = startPaths.iterator().next();
			startPaths.remove(path);
			// System.out.println("Starting at: " + path);
			continuePath(path);

		}


	}

	private boolean addStartPath(Path path) {
		return startPaths.add(path);
	}

	private void continuePath(Path path) {

		Node currentNode = path.getTail();

		if (currentNode instanceof ComposedNode) {
			// we reached a composed node
			ComposedNode composedNode = (ComposedNode) currentNode;

			if (strategy.enterSubflow(composedNode, path)) {
				StartNode startNode = DiaFluxUtils.getCalledStartNode(kb, composedNode);
				// as long as it is no wait node, enter it
				// if (!isWaitNode(startNode.getFlow())) {
				path.enterFlow(composedNode);
				continueOnNode(path, startNode);
				return;

			}
			else {
				// just continue with the outgoing edges of the composed node
			}

		}

		List<Edge> edges = new LinkedList<Edge>();
		if (currentNode instanceof EndNode) {

			// we reached the end of the main flowchart
			if (!path.hasEnteredFlow()) {
				strategy.found(path);
				return;
			}

			ComposedNode callingNode = path.returnFromFlow();
			List<Edge> outgoingEdges = callingNode.getOutgoingEdges();
			for (Edge edge : outgoingEdges) {
				Condition condition = edge.getCondition();
				if (condition instanceof FlowchartProcessedCondition) {
					if (strategy.followEdge(edge, path)) {
						edges.add(edge);
					}
				}
				else if (condition instanceof NodeActiveCondition) {
					if (((NodeActiveCondition) condition).getNodeName().equals(
							currentNode.getName())) {
						if (strategy.followEdge(edge, path)) {
							edges.add(edge);
						}
					}
				}

			}


		}
		else {

			for (Edge edge : currentNode.getOutgoingEdges()) {
				if (strategy.followEdge(edge, path)) {
					edges.add(edge);
				}

			}
		}

		continueOnEdges(path, edges);

	}


	/**
	 * 
	 * @created 21.03.2012
	 * @param path
	 * @param edges
	 */
	private void continueOnEdges(Path path, List<Edge> edges) {
		if (edges.isEmpty()) {
			strategy.found(path);
		}
		else {
			for (Edge edge : edges) {
				Path newPath = path.copy();
				if (strategy.offer(edge, newPath)) {
					continueOnNode(newPath, edge.getEndNode());
				}
				else {
					foundPath(newPath);
				}

			}

		}
	}


	private void continueOnNode(Path path, Node node) {

		Path newPath = path.copy();
		if (strategy.offer(node, newPath)) {
			continuePath(newPath);
		}
		else {
			foundPath(newPath);
		}
	}

	/**
	 * 
	 * @created 24.04.2012
	 * @param newPath
	 */
	public void foundPath(Path newPath) {
		strategy.found(newPath);
		Path startPath = strategy.createStartPath(newPath);
		if (startPath != null) {
			addStartPath(startPath);
		}
	}


}
