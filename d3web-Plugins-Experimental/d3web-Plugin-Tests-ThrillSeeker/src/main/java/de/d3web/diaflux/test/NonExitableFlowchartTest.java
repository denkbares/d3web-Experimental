/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.diaflux.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FlowchartProcessedCondition;

/**
 * 
 * @author Reinhard Hatko
 * @created 21.05.2013
 */
public class NonExitableFlowchartTest extends DiaFluxTest {

	public NonExitableFlowchartTest() {
		super(
				"The knowledge base contains {0} flowcharts, that try to resume from calling another flowchart, which can not be left, because it does not contain an exit node.");
	}

	/**
	 * 
	 * @created 21.05.2013
	 * @param testObject
	 * @param flows
	 * @return
	 */
	@Override
	protected Collection<Flow> doTest(KnowledgeBase testObject, List<Flow> flows) {
		Collection<Flow> erroneousFlows = new HashSet<>();

		for (Flow flow : flows) {
			Collection<ComposedNode> compNodes = flow.getNodesOfClass(ComposedNode.class);
			for (ComposedNode node : compNodes) {
				List<Edge> edges = node.getOutgoingEdges();
				for (Edge edge : edges) {
					if (edge.getCondition() instanceof FlowchartProcessedCondition) {
						Flow calledFlow = DiaFluxUtils.getCalledFlow(node);
						if (calledFlow.getExitNodes().isEmpty()) {
							erroneousFlows.add(flow);
						}

					}
				}

			}
		}
		return erroneousFlows;
	}

	@Override
	public String getDescription() {
		return "This test checks for flowcharts, that do not have an exit node, but are used in ComposedNodes, that have a leaving 'processed' condition.";
	}


}
