/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.we.ci4ke.anomalies;

import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.TestResultType;
import de.d3web.we.core.KnowWEEnvironment;

/**
 * Anomaly detection: test for unused elements
 * 
 * @author Gritje Meinke
 * @created 17.04.2011
 */
public class AnomalyUnusedElements extends AbstractCITest {

	@Override
	public CITestResult call() throws Exception {

		CITestResult res = new CITestResult(TestResultType.SUCCESSFUL,
				": No unused elements detected.");

		Integer numberOfUnusedElements = 0;
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

			// get all questions from KB
			List<Question> questions =
					kb.getManager().getObjects(Question.class);

			for (Question q : questions) {

				boolean isUsed = false;

				for (Flow flow : flowcharts) {

					List<Node> nodes = flow.getNodes();

					for (Node n : nodes) {

						if (n.getName().indexOf(q.getName()) >= 0) {
							isUsed = true;
						}

					} // end for each node

				} // end for each flowchart

				if (isUsed == false) {
					numberOfUnusedElements++;
					buf.append("Question >" + q.getName() + "< is not used.\n");
				}

			} // end for each question

			
			// get all solutions from KB
			List<Solution> solutions =
					kb.getManager().getObjects(Solution.class);

			for (Solution s : solutions) {

				boolean isUsed = false;

				for (Flow flow : flowcharts) {

					List<Node> nodes = flow.getNodes();

					for (Node n : nodes) {

						if (n.getName().indexOf(s.getName()) >= 0) {
							isUsed = true;
						}

					} // end for each node

				} // end for each flowchart

				if (isUsed == false) {
					numberOfUnusedElements++;
					buf.append("Solution >" + s.getName() + "< is not used.\n");
				}

			} // end for each solution

		} // end if KB

		if (numberOfUnusedElements > 0) {
			res = new
					CITestResult(TestResultType.FAILED, ": Anomaly detected - " +
							numberOfUnusedElements.toString() +
							" unused element(s) found: \n" + buf.toString());
		}

		return res;

	}
}