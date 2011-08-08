/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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

import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PostHookablePSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;


/**
 * 
 * @author Reinhard Hatko
 * @created 05.08.2011
 */
public class PSMDiaFluxCoverage extends PSMethodAdapter implements PostHookablePSMethod {

	private static Map<Edge, Integer> edges = new HashMap<Edge, Integer>();
	private static Map<Node, Integer> nodes = new HashMap<Node, Integer>();

	@Override
	public void postPropagate(Session session) {
		FluxSolver fluxSolver = session.getPSMethodInstance(FluxSolver.class);
		if (fluxSolver == null) {
			System.out.println("No DiaFlux");
			return;
		}

		if (!DiaFluxUtils.isFlowCase(session)) return;

		DiaFluxCaseObject caseObject = DiaFluxUtils.getDiaFluxCaseObject(session);

		for (Node node : caseObject.getTracedNodes()) {

			if (!nodes.containsKey(node)) {
				nodes.put(node, 0);
			}

			nodes.put(node, nodes.get(node) + 1);

		}

		for (Edge edge : caseObject.getTracedEdges()) {

			if (!edges.containsKey(edge)) {
				edges.put(edge, 0);
			}

			edges.put(edge, edges.get(edge) + 1);

		}
	}

	@Override
	public void init(Session session) {
		edges.clear();
		nodes.clear();
	}


	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {

	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return null;
	}

	@Override
	public boolean hasType(Type type) {
		return false;
	}

	@Override
	public double getPriority() {
		return 6;
	}

	public static int getCount(Node node) {
		Integer count = nodes.get(node);
		if (count == null) {
			return 0;
		}
		else return count;

	}

	public static int getCount(Edge edge) {
		Integer count = edges.get(edge);
		if (count == null) {
			return 0;
		}
		else return count;

	}

}
