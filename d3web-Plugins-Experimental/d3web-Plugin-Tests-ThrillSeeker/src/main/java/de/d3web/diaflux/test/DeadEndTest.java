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
import de.d3web.diaFlux.flow.CommentNode;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;


/**
 * Tests for nodes that are not connected to a cycle or an exit node.
 * 
 * @author Reinhard Hatko
 * @created 22.05.2013
 */
public class DeadEndTest extends DiaFluxTest {

	public DeadEndTest() {
		super("The knowledge base contains {0} flowcharts with dead ends.");
	}

	@Override
	public String getDescription() {
		return "Tests for non-cyclical paths that are not terminated by an exit node.";
	}

	@Override
	protected Collection<Flow> doTest(KnowledgeBase testObject, List<Flow> flows) {
		Collection<Flow> result = new HashSet<Flow>();

		for (Flow flow : flows) {
			nextnode:
			for (Node node : flow.getNodes()) {
				// don't test exit nodes, as they are not connected on a path.
				if (node instanceof EndNode) continue;

				// comment nodes are considered a dead end, if they are
				// connected to a start node
				if (node instanceof CommentNode) {
					boolean connected = false;
					for (Node start : flow.getStartNodes()) {
						if (DiaFluxUtils.areConnectedNodes(start, node)) connected = true;
					}

					// only if the comment is not connected to the "main flow",
					// we ignore it
					// better check if it is connected to a "normal" node?
					if (!connected) continue nextnode;
				}

				for (Node exit : flow.getExitNodes()) {
					if (DiaFluxUtils.areConnectedNodes(node, exit)) continue nextnode;
				}

				// Node is not connected to exit node, check for path that leads
				// to a cycle:
				// get all reachable nodes, if one is on a cycle, it's ok
				// if there are successors, that are not on a cycle, the test
				// will fail for them
				Collection<Node> reachableNodes = DiaFluxUtils.getReachableNodes(node);
				for (Node successor : reachableNodes) {
					if (DiaFluxUtils.areConnectedNodes(successor, successor)) continue nextnode;

					// a dead end is allowed on a composed node for a flow with
					// no exit nodes
					if (successor instanceof ComposedNode && successor.getOutgoingEdges().isEmpty()) {
						Flow calledFlow = DiaFluxUtils.getCalledFlow((ComposedNode) successor);

						// ignores KB errors
						if (calledFlow == null) continue nextnode;

						if (calledFlow.getExitNodes().isEmpty()) continue nextnode;
					}
				}
				result.add(flow);

			}

		}

		return result;
	}

}
