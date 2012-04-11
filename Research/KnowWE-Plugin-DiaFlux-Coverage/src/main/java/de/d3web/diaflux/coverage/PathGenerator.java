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
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.SnapshotNode;
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

	private final EdgeEvaluator evaluator;
	private final KnowledgeBase kb;
	private final Collection<Path> paths;

	public PathGenerator(KnowledgeBase kb, EdgeEvaluator evaluator) {
		this.evaluator = evaluator;
		this.kb = kb;
		this.paths = new HashSet<Path>();
	}

	public Collection<Path> createPaths() {


		for (Path path : evaluator.getStartPaths()) {
			createPath(path);
		}

		return paths;

	}

	private void createPath(Path path) {

		Node currentNode = path.getTail();

		if (currentNode instanceof ComposedNode) {
			// we reached a composed node
			ComposedNode composedNode = (ComposedNode) currentNode;
			StartNode startNode = DiaFluxUtils.getCalledStartNode(kb, composedNode);

			// as long as it is no wait node, enter it
			if (!isWaitNode(startNode.getFlow())) {
				path.enterFlow(composedNode);
				newPath(path, startNode);
				return;
			} // we reached a wait node
			// else {
			// // the wait node is at "the end" of a path, so stop and create a
			// // new one
			// if (path.getLength() > 1) {
			// foundPath(path);
			// createPath(new Path(currentNode, path.getCallStack()));
			// return;
			// }
			// else {
			// // we just started a new path, so allow it to continue
			// }
			// }
		}

		List<Edge> edges = new LinkedList<Edge>();
		if (currentNode instanceof EndNode) {

			if (!path.hasEnteredFlow()) {
				foundPath(path);
				return;
			}

			ComposedNode callingNode = path.returnFromFlow();
			List<Edge> outgoingEdges = callingNode.getOutgoingEdges();
			for (Edge edge : outgoingEdges) {
				Condition condition = edge.getCondition();
				if (condition instanceof FlowchartProcessedCondition) {
					if (evaluator.followEdge(edge, path)) {
						edges.add(edge);
					}
				}
				else if (condition instanceof NodeActiveCondition) {
					if (((NodeActiveCondition) condition).getNodeName().equals(
							currentNode.getName())) {
						if (evaluator.followEdge(edge, path)) {
							edges.add(edge);
						}
					}
				}

			}


		}
		else {

			for (Edge edge : currentNode.getOutgoingEdges()) {
				if (evaluator.followEdge(edge, path)) {
					edges.add(edge);
				}

			}
		}

		continueOnEdges(path, edges);

	}

	/**
	 * 
	 * @created 03.04.2012
	 * @param flow
	 * @return
	 */
	public boolean isWaitNode(Flow flow) {
		return flow.getName().equalsIgnoreCase("wait");
	}

	/**
	 * 
	 * @created 21.03.2012
	 * @param path
	 * @param edges
	 */
	private void continueOnEdges(Path path, List<Edge> edges) {
		if (edges.isEmpty()) {
			foundPath(path);
		}
		else {
			for (Edge edge : edges) {
				newPath(path.append(edge), edge.getEndNode());

			}

		}
	}

	private boolean foundPath(Path path) {
		return paths.add(path);
	}

	private void newPath(Path path, Node node) {
		Path newPath = path.append(node);
		if (
node instanceof SnapshotNode ||
				path.isFinished()) {

			// we found a new path
			boolean foundPath = foundPath(path);

			// if it is NOT new, we
			if (!foundPath) {

			}

			if (node instanceof SnapshotNode) {
				// if the end was a SnapshotNode, we continue,
				// unless it was a loop around a single Snapshot
				//
				if (newPath.getHead() == newPath.getTail()) {
					return;
				}
				else {
					newPath = newPath.newPath();

				}
			}
			else {
				// we reached a node, already in the path
				return;
			}
		}
		else {
			// simply continue path on next node
			// newPath = path.append(node);
		}

		createPath(newPath);
	}


}
