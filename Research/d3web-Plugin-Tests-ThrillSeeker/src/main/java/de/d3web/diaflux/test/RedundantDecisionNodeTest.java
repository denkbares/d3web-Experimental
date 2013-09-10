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

import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.NOOPAction;


/**
 * This test checks for redundant decision nodes, ie. decision nodes with no
 * conditions on outgoing edges.
 * 
 * @author Reinhard Hatko
 * @created 24.05.2013
 */
public class RedundantDecisionNodeTest extends DiaFluxTest {

	public RedundantDecisionNodeTest() {
		super("The knowledge base contains {0} flows with redundant decision nodes:");
	}

	@Override
	public String getDescription() {
		return "This test checks for decision nodes, that do not have conditions on any outgoing edges.";
	}

	@Override
	protected Collection<Flow> doTest(KnowledgeBase testObject, List<Flow> flows) {
		Collection<ActionNode> decisionNodes = new HashSet<ActionNode>();

		for (Flow flow : flows) {
			nextNode:
			for (ActionNode actionNode : flow.getNodesOfClass(ActionNode.class)) {
				if (actionNode.getAction() instanceof NOOPAction) {
					for (Edge edge : actionNode.getOutgoingEdges()) {
						if (!(edge.getCondition() instanceof ConditionTrue))
							continue nextNode;
					}
					decisionNodes.add(actionNode);

				}
			}

		}

		Collection<Flow> result = new HashSet<Flow>();
		// find the flows to the redundant nodes
		for (ActionNode node : decisionNodes) {
			result.add(node.getFlow());
		}

		return result;
	}

}
