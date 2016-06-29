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
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;


/**
 * Checks, if a comment node is reachable from a start node, i.e., if the
 * flowchart contains informal components
 * 
 * @author Reinhard Hatko
 * @created 18.06.2013
 */
public class InformalNodeTest extends DiaFluxTest {

	public InformalNodeTest() {
		super("The knowledge base contains {0} flowcharts with informal components:");
	}

	@Override
	public String getDescription() {
		return "Checks, if a comment node is reachable from a start node, i.e., if the flowchart contains informal components.";
	}

	@Override
	protected Collection<Flow> doTest(KnowledgeBase testObject, List<Flow> flows) {
		Collection<Flow> result = new HashSet<>();

		next:
		for (Flow flow : flows) {
			for (Node cNode : flow.getNodesOfClass(CommentNode.class)) {
				for (Node startNode : flow.getStartNodes()) {
					if (DiaFluxUtils.areConnectedNodes(startNode, cNode)) {
						result.add(flow);
						continue next;
					}
				}

			}
		}

		return result;
	}

}
