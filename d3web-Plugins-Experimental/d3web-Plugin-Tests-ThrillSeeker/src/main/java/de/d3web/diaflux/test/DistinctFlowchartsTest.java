package de.d3web.diaflux.test;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;


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

/**
 * 
 * @author Reinhard Hatko
 * @created 21.05.2013
 */
public class DistinctFlowchartsTest extends DiaFluxTest {

	public DistinctFlowchartsTest() {
		super(
				"The knowledge base contains {0} flowcharts, that contain more than one distinct models:");
	}

	@Override
	protected Collection<Flow> doTest(KnowledgeBase testObject, List<Flow> flows) {

		Collection<Flow> erroneousFlows = new HashSet<Flow>();

		next: for (Flow flow : flows) {
			for (StartNode startNode : flow.getStartNodes()) {
				for (EndNode exitNode : flow.getExitNodes()) {
					if (!DiaFluxUtils.areConnectedNodes(startNode, exitNode)) {
						erroneousFlows.add(flow);
						continue next;
					}

				}

			}
		}

		return erroneousFlows;
	}

	@Override
	public String getDescription() {
		return "This test checks for flowcharts, whose start nodes are not all connected to each end node. Such a flowchart contains more than one distinct models.";
	}


}
