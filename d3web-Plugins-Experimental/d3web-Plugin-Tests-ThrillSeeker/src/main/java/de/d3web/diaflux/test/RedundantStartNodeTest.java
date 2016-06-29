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
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;


/**
 * This test checks for start nodes that are not called by another flow.
 * 
 * @author Reinhard Hatko
 * @created 24.05.2013
 */
public class RedundantStartNodeTest extends DiaFluxTest {

	public RedundantStartNodeTest() {
		super("The knowledge base contains {0} flows with unused start nodes:");
	}

	@Override
	public String getDescription() {
		return "This test checks for start nodes that are not called by another flow";
	}

	@Override
	protected Collection<Flow> doTest(KnowledgeBase testObject, List<Flow> flows) {
		Collection<Flow> result = new HashSet<>();
		Collection<StartNode> startNodes = new HashSet<>();

		// collect all startnodes from the flows to test
		for (Flow flow : flows) {
			startNodes.addAll(flow.getStartNodes());
		}

		// remove called start nodes from ALL flows
		for (Flow flow : DiaFluxUtils.getFlowSet(testObject)) {
			for (ComposedNode composedNode : flow.getNodesOfClass(ComposedNode.class)) {
				startNodes.remove(DiaFluxUtils.getCalledStartNode(composedNode));
			}
		}

		// remove all autostart nodes
		startNodes.removeAll(DiaFluxUtils.getAutostartNodes(testObject));

		// TODO Omit flows which have none of their nodes called??? These should
		// be found by UnusedFlowTest

		// find the flows to the unused nodes
		for (StartNode startNode : startNodes) {
			result.add(startNode.getFlow());
		}

		return result;
	}

}
