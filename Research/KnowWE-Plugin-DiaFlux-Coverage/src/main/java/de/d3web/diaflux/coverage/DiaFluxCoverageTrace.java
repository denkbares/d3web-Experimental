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
import java.util.HashMap;
import java.util.Map;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.PropagationListener;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;
import de.knowwe.diaflux.DiaFluxTrace;
import de.knowwe.diaflux.coverage.CoverageUtils;

/**
 * 
 * @author Reinhard Hatko
 * @created 02.04.2012
 */
public class DiaFluxCoverageTrace implements SessionObject {

	public static final PropagationListener LISTENER = new PropagationListener() {

		@Override
		public void propagationStarted(Session session, Collection<PropagationEntry> entries) {
		}

		@Override
		public void propagationFinished(Session session, Collection<PropagationEntry> entries) {
		}

		@Override
		public void postPropagationStarted(Session session, Collection<PropagationEntry> entries) {
			if (!DiaFluxUtils.isFlowCase(session)) {
				return;
			}
			CoverageUtils.getCoverage(session).update(session);
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj.getClass() == getClass();
		}

		@Override
		public int hashCode() {
			return getClass().hashCode();
		}

	};

	public static final SessionObjectSource<DiaFluxCoverageTrace> SOURCE = new SessionObjectSource<DiaFluxCoverageTrace>() {

		@Override
		public DiaFluxCoverageTrace createSessionObject(Session session) {
			return new DiaFluxCoverageTrace();
		}
	};

	private final Map<Edge, Integer> edges;
	private final Map<Node, Integer> nodes;
	private final Map<Path, Integer> paths;

	public DiaFluxCoverageTrace() {
		edges = new HashMap<Edge, Integer>();
		nodes = new HashMap<Node, Integer>();
		paths = new HashMap<Path, Integer>();
	}

	void update(Session session) {
		traceNodesAndEdges(session);
		tracePaths(session);
	}

	/**
	 * 
	 * @created 05.04.2012
	 * @param session
	 */
	public void tracePaths(Session session) {

		CoveredPathsStrategyShallow evaluator = new CoveredPathsStrategyShallow(session);
		new PathGenerator(session.getKnowledgeBase(), evaluator).createPaths();
		Collection<Path> paths = evaluator.getPaths(); // TODO

		for (Path path : paths) {
			addTracedPath(path);
		}

		System.out.println("Covered paths:" + paths.size());

		// for (Path path : this.paths.keySet()) {
		// System.out.println(path.getHead() + " (" + path.getLength() + ") = "
		// + this.paths.get(path));
		//
		// }

	}

	/**
	 * 
	 * @created 05.04.2012
	 * @param session
	 */
	public void traceNodesAndEdges(Session session) {
		DiaFluxCaseObject caseObject = DiaFluxUtils.getDiaFluxCaseObject(session);
		Collection<SnapshotNode> enteredSnapshots = caseObject.getActivatedSnapshots(session);
		Collection<Node> activeNodes = DiaFluxTrace.collectActiveNodes(caseObject, enteredSnapshots);

		for (Node node : activeNodes) {
			this.addTracedNode(node);
			for (Edge edge : node.getOutgoingEdges()) {
				if (FluxSolver.evalEdge(session, edge)) {
					this.addTracedEdge(edge);
				}
			}
		}
	}

	private void addTracedNode(Node node) {
		countElement(node, nodes);
	}

	private void addTracedEdge(Edge edge) {
		countElement(edge, edges);
	}

	private void addTracedPath(Path path) {
		countElement(path, paths);
	}

	private static <T> void countElement(T elem, Map<T, Integer> map) {
		if (!map.containsKey(elem)) {
			map.put(elem, 0);
		}

		map.put(elem, map.get(elem) + 1);

	}

	protected Map<Edge, Integer> getEdgeCounts() {
		return edges;
	}

	protected Map<Node, Integer> getNodeCounts() {
		return nodes;
	}

	protected Map<Path, Integer> getPathCounts() {
		return paths;
	}


}