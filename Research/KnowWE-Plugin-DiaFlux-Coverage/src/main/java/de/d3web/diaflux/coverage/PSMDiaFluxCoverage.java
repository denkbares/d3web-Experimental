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

import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PostHookablePSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;


/**
 * This PSM keeps track of the pathes taken in the DiaFlux models during a
 * session.
 * 
 * @author Reinhard Hatko
 * @created 05.08.2011
 */
public class PSMDiaFluxCoverage extends PSMethodAdapter implements PostHookablePSMethod, SessionObjectSource<CoverageSessionObject> {


	@Override
	public void postPropagate(Session session) {
		FluxSolver fluxSolver = session.getPSMethodInstance(FluxSolver.class);

		if (fluxSolver == null) return;
		if (!DiaFluxUtils.isFlowCase(session)) return;

		DiaFluxCaseObject caseObject = DiaFluxUtils.getDiaFluxCaseObject(session);
		CoverageSessionObject coverage = session.getSessionObject(this);

		for (Node node : caseObject.getTracedNodes()) {
			coverage.addTracedNode(node);
		}

		for (Edge edge : caseObject.getTracedEdges()) {
			coverage.addTracedEdge(edge);
		}
	}

	@Override
	public void init(Session session) {
	}


	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeError(facts);
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.consumer;
	}

	@Override
	public double getPriority() {
		return 6;
	}


	@Override
	public CoverageSessionObject createSessionObject(Session session) {
		return new CoverageSessionObject();
	}

	public static CoverageSessionObject getCoverage(Session session) {
		return session.getSessionObject(session.getPSMethodInstance(PSMDiaFluxCoverage.class));
	}



}
