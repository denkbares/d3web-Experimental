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
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.NodeActiveCondition;


/**
 * This test checks for flows whose is result (ie, which exit node was reached)
 * is not used in at least one calling flowchart.
 * 
 * @author Reinhard Hatko
 * @created 27.05.2013
 */
public class RedundantExitNodeTest extends DiaFluxTest {

	
	public RedundantExitNodeTest() {
		super("The knowledge base contains {0} flows whose exit nodes are not all used:");
	}
	
	@Override
	protected Collection<Flow> doTest(KnowledgeBase testObject, List<Flow> flows) {
		Collection<Flow> result = new HashSet<Flow>();

		nextFlow: for (Flow flow : flows) {
			// at least 2 results are needed.
			List<EndNode> exitNodes = new LinkedList<EndNode>(flow.getExitNodes());
			if (exitNodes.size() < 2) continue;

			Collection<ComposedNode> callingNodes = DiaFluxUtils.getCallingNodes(flow);
			// ignored uncalled flows
			if (callingNodes.isEmpty()) continue nextFlow;

			for (ComposedNode callingNode : callingNodes) {
				List<Edge> edges = callingNode.getOutgoingEdges();
				for (Edge edge : edges) {
					Condition condition = edge.getCondition();
					if (condition instanceof NodeActiveCondition) {
						EndNode exitNode = DiaFluxUtils.findExitNode(testObject,
								(NodeActiveCondition) condition);
						// if the result is at least used once, its ok
						exitNodes.remove(exitNode);
					}

				}
			}
			if (!exitNodes.isEmpty()) {
				result.add(flow);
			}

		}

		return result;
	}

	@Override
	public String getDescription() {
		return "This test checks for exit nodes which are not used in at least one calling flowchart. To be tested, flows need to be called and have more than one exit node.";
	}


}
