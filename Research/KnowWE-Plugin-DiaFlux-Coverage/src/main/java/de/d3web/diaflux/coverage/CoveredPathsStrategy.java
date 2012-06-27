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
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowRun;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 01.04.2012
 */
class CoveredPathsStrategy extends CoveredPathsStrategyShallow {

	public CoveredPathsStrategy(Session session) {
		super(session);
	}


	/**
	 * Creates a list of callstacks (calls to composed nodes), that let to the
	 * activation of the supplied node.
	 * 
	 * @param activeNode
	 * @created 03.04.2012
	 * @return
	 */
	Collection<Deque<ComposedNode>> createCallStacks(Node activeNode) {

		Collection<Deque<ComposedNode>> stacks = new LinkedList<Deque<ComposedNode>>();
		fillCallStack(new LinkedList<ComposedNode>(), activeNode, stacks);

		return stacks;

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

		// Collection<SnapshotNode> enteredSnapshots =
		// caseObject.getActivatedSnapshots(session);
		Collection<SnapshotNode> startSnapshots = new LinkedList<SnapshotNode>();
		for (FlowRun flowRun : runs) {
			for (Node startingNode : flowRun.getStartNodes()) {
				if (startingNode instanceof SnapshotNode) {
					startSnapshots.add((SnapshotNode) startingNode);
				}
			}
		}

		// TODO this does not find pathes from one snapshot to the next in the
		// same flow
		for (Node node : startSnapshots) {
			Collection<Deque<ComposedNode>> callStacks = createCallStacks(node);
			for (Deque<ComposedNode> stack : callStacks) {
				startingPaths.add(new Path(node, stack));
			}
		}

		return startingPaths;
	}

	/**
	 * 
	 * @created 04.04.2012
	 * @param stack
	 * @param calledFlow
	 * @param stacks
	 */
	public void fillCallStack(Deque<ComposedNode> stack, Node activeNode, Collection<Deque<ComposedNode>> stacks) {
		DiaFluxCaseObject caseObject = DiaFluxUtils.getDiaFluxCaseObject(session);
		Collection<StartNode> activeStartNodes = new HashSet<StartNode>();
		Flow calledFlow = activeNode.getFlow();

		// active start nodes in the current flow
		for (StartNode node : calledFlow.getStartNodes()) {
			for (FlowRun run : caseObject.getRuns()) {
				// start node must be active and leading to the active Node
				boolean connectedNodes = DiaFluxUtils.areConnectedNodes(node, activeNode);
				if (run.isActive(node) && connectedNodes) {
					activeStartNodes.add(node);
				}
			}

		}

		// if none are found, we finished
		if (activeStartNodes.isEmpty()) {
			stacks.add(stack);
			return;
		}
		Collection<ComposedNode> callingActiveCompNodes = new HashSet<ComposedNode>();
		for (StartNode node : activeStartNodes) {
			// all composed nodes calling this startnode...
			List<ComposedNode> callingNodes =
					DiaFluxUtils.getCallingNodes(session.getKnowledgeBase(), node);

			for (ComposedNode composedNode : callingNodes) {
				for (FlowRun run : caseObject.getRuns()) {
					if (run.isActive(composedNode)) {
						// ... for each one that is active, a new stack is
						// created
						callingActiveCompNodes.add(composedNode);

					}
				}
			}
		}

		for (ComposedNode composedNode : callingActiveCompNodes) {
			Deque<ComposedNode> newStack = new LinkedList<ComposedNode>(stack);
			newStack.push(composedNode);
			fillCallStack(newStack, composedNode, stacks);

		}

	}


	@Override
	public boolean enterSubflow(ComposedNode node, Path path) {
		return true;
	}

}