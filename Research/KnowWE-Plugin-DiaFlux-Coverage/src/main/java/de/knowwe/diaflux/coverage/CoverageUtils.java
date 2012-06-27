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
package de.knowwe.diaflux.coverage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaflux.coverage.DiaFluxCoverageTrace;


/**
 * 
 * @author Reinhard Hatko
 * @created 08.02.2012
 */
public class CoverageUtils {

	private CoverageUtils() {
	}

	public static DiaFluxCoverageTrace getCoverage(Session session) {
		return session.getSessionObject(DiaFluxCoverageTrace.SOURCE);
	}


	public static Map<Flow, Collection<Flow>> createFlowStructure(KnowledgeBase base) {
		List<StartNode> nodes = DiaFluxUtils.getAutostartNodes(base);
		assert nodes.size() == 1; // TODO for now works only with 1

		Flow callingFlow = nodes.get(0).getFlow();
		return createFlowStructure(base, new HashMap<Flow, Collection<Flow>>(), callingFlow);

	}

	/**
	 * 
	 * @created 08.02.2012
	 * @param base
	 * @param result
	 * @param callingFlow
	 * @return
	 */
	private static Map<Flow, Collection<Flow>> createFlowStructure(KnowledgeBase base, Map<Flow, Collection<Flow>> result, Flow callingFlow) {
		Collection<ComposedNode> composed = callingFlow.getNodesOfClass(ComposedNode.class);
		for (ComposedNode composedNode : composed) {
			Flow calledFlow = DiaFluxUtils.getCalledFlow(base, composedNode);
			addFlow(result, callingFlow);
			addFlow(result, calledFlow);
			addCall(result, callingFlow, calledFlow);
			createFlowStructure(base, result, calledFlow);
		}

		return result;

	}

	/**
	 * 
	 * @created 08.02.2012
	 * @param result
	 * @param calledFlow
	 */
	private static void addFlow(Map<Flow, Collection<Flow>> result, Flow calledFlow) {
		Collection<Flow> flows = result.get(calledFlow);
		if (flows == null) {
			flows = new HashSet<Flow>();
			result.put(calledFlow, flows);
		}
	}

	/**
	 * 
	 * @created 03.04.2012
	 * @param flow
	 * @return
	 */
	public static boolean isWaitNode(Flow flow) {
		return flow.getName().equalsIgnoreCase("wait");
	}

	/**
	 * 
	 * @created 08.02.2012
	 * @param result
	 * @param callingFlow
	 * @param calledFlow
	 */
	private static void addCall(Map<Flow, Collection<Flow>> result, Flow callingFlow, Flow calledFlow) {
		for (Flow flow : result.keySet()) {
			//called flow is already traced as called from someone else
			// if (flow == calledFlow) {
			// return;
			// }
			// else {
				if (result.get(flow).contains(calledFlow)) {
					return; // TODO put empty list??
				}
			// }

		}
		
		
		Collection<Flow> flows = result.get(callingFlow);
		flows.add(calledFlow);
	}

}
