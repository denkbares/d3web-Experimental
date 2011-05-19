/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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


package de.d3web.we.ci4ke.anomalies;

import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.TestResultType;
import de.d3web.we.core.KnowWEEnvironment;

/**
 * Anomaly detection: test for unconnected nodes
 * 
 * @author Gritje Meinke
 * @created 17.04.2011
 */
public class AnomalyUnconnectedNode extends AbstractCITest {

	@Override
        public CITestResult call() throws Exception {

		CITestResult res = new CITestResult(TestResultType.SUCCESSFUL,
				": No unconnected nodes detected.");

		Integer numberOfUnconnectedNodes = 0;
		StringBuffer buf = new
				StringBuffer();

		// get the first parameter = article whose KB should be searched for
		// anomaly
		String articleName = getParameter(0);
		// get the KB of this article
		KnowledgeBase kb =
				D3webModule.getAD3webKnowledgeServiceInTopic(
						KnowWEEnvironment.DEFAULT_WEB, articleName);

		if (kb != null) {

			// get all flowcharts
			List<Flow> flowcharts =
			kb.getManager().getObjects(Flow.class);

			// FlowSet set = new FlowSet();

			for (Flow flow : flowcharts) {

				List<Node> nodes = flow.getNodes();

				for (Node n : nodes) {

					if (n.getIncomingEdges().isEmpty()) {
						if (n.getOutgoingEdges().isEmpty()) {
							buf.append("FLOWCHART: " + flow.getName() + " NODE: " + n.getName()
									+ "\n");
							numberOfUnconnectedNodes++;
						} // end if
					} // end if

				} // end for each node

			} // end for each flowchart

		} // end if KB

		if (numberOfUnconnectedNodes > 0) {
			res = new
					CITestResult(TestResultType.FAILED, ": Anomaly detected - " +
							numberOfUnconnectedNodes.toString() +
							" unconnected node(s) found: \n" + buf.toString());
		}

		return res;

        }
}
