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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowRun;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 20.05.2012
 */
class CoveredPathsStrategyShallow implements DFSStrategy {

	protected final Session session;
	protected final Collection<Path> paths;

	public CoveredPathsStrategyShallow(Session session) {
		this.session = session;
		this.paths = new HashSet<Path>();
	}

	/**
	 * Returns true, if the path generation should continue on the given edge.
	 */
	@Override
	public boolean followEdge(Edge edge, Path path) {
		return FluxSolver.evalEdge(session, edge);
	}

	@Override
	public boolean offer(DiaFluxElement el, Path path) {
		boolean finished = false;
		if (path.contains(el)) {
			finished = true;
		}
		path.append(el);
		return !finished;
	}

	/**
	 * does not create new start paths. Paths stop at snapshots and end there.
	 */
	@Override
	public Path createStartPath(Path path) {
		return null;
	}

	public Collection<Path> getPaths() {
		return paths;
	}

	@Override
	public void found(Path path) {
		paths.add(path);
	}

	/**
	 * Returns a list of nodes, at which the path generation should start.
	 */
	@Override
	public List<Path> getInitialStartPaths() {
		DiaFluxCaseObject caseObject = DiaFluxUtils.getDiaFluxCaseObject(session);

		if (caseObject == null) return Collections.emptyList();

		List<Path> startingPaths = new LinkedList<Path>();
		List<FlowRun> runs = caseObject.getRuns();

		for (Flow flow : DiaFluxUtils.getFlowSet(session)) {
			for (StartNode startNode : flow.getStartNodes()) {
				for (FlowRun flowRun : runs) {
					if (flowRun.isActive(startNode)) {
						startingPaths.add(new Path(startNode));

					}
				}

			}
		}

		for (FlowRun flowRun : runs) {
			for (Node node : flowRun.getStartNodes()) {
				if (node instanceof SnapshotNode) {
					startingPaths.add(new Path(node));
				}
			}
		}

		return startingPaths;
	}


	@Override
	public boolean enterSubflow(ComposedNode node, Path path) {
		return false;
	}

}