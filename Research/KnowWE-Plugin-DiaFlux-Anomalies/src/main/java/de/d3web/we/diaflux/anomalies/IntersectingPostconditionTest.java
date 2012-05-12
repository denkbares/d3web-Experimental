/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaflux.anomalies;

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.we.diaflux.evaluators.AnsweredEval;
import de.d3web.we.diaflux.evaluators.Evaluator;
import de.d3web.we.diaflux.evaluators.EvaluatorManager;
import de.d3web.we.diaflux.evaluators.KnownEval;
import de.d3web.we.diaflux.evaluators.UnknownEval;
import de.d3web.we.diaflux.pathcoloring.AnomalyManager;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class IntersectingPostconditionTest extends AbstractAnomalyTest {

	private final EvaluatorManager evalManager = EvaluatorManager.getEvalManager();

	/**
	 *
	 */
	// @Override
	// public CITestResult call() throws Exception {
	//
	// String articleName = getParameter(0);
	// String config = "knowledge base article: " + articleName;
	//
	// String errormsg = "";
	//
	// KnowledgeBase kb =
	// D3webUtils.getKnowledgeBase(
	// Environment.DEFAULT_WEB, articleName);
	//
	// CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);
	//
	// if (null != kb) {
	// AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
	// List<Flow> flowcharts =
	// kb.getManager().getObjects(Flow.class);
	// for (Flow flow : flowcharts) {
	// for (Node node : flow.getNodes()) {
	//
	// if (evaluateIntersect(node)) {
	// errormsg += "intersecting PostCondition at " + node.toString() + "<br>";
	// anomalyManager.addAnomaly(flow, node, "Intersecting Postcondition");
	// }
	// }
	// }
	// }
	//
	// if (!errormsg.isEmpty()) {
	// Strings.maskHTML(errormsg);
	// res = new CITestResult(Type.FAILED, "Postconditions are intersecting: " +
	// errormsg,
	// config);
	// }
	// return res;
	// }

	@Override
	protected String test(KnowledgeBase kb) {
		String errormsg = "";
		if (null != kb) {
			AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);
			for (Flow flow : flowcharts) {
				for (Node node : flow.getNodes()) {

					if (evaluateIntersect(node)) {
						errormsg += "intersecting PostCondition at " + node.toString() + "<br>";
						anomalyManager.addAnomaly(flow, node, "Intersecting Postcondition");
					}
				}
			}
		}
		return errormsg;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param node
	 * @return
	 */
	private boolean evaluateIntersect(Node node) {
		List<Condition> conditions = new LinkedList<Condition>();
		for (Edge edge : node.getOutgoingEdges()) {
			conditions.add(edge.getCondition());
		}
		for (int i = 0; i < conditions.size(); i++) {
			Condition cond1 = conditions.get(i);
			if (cond1 instanceof CondAnd) {

			}
			if (cond1 instanceof CondEqual) {
			}

			for (Evaluator eval1 : evalManager.getEvaluator()) {
				if (eval1.canEvaluate(cond1)) {

					for (int j = (i + 1); j < conditions.size(); j++) {
						Condition cond2 = conditions.get(j);
						for (Evaluator eval2 : evalManager.getEvaluator()) {
							if (eval2.canEvaluate(cond2)) {
								if (eval1 instanceof AnsweredEval
										|| eval2 instanceof AnsweredEval) {
									return true;
								}
								if (eval1 instanceof KnownEval
										&& !(eval2 instanceof UnknownEval)) {
									return true;

								}
								if (eval2 instanceof KnownEval
										&& !(eval1 instanceof UnknownEval)) {
									return true;

								}
								if (eval1 instanceof UnknownEval
										&& eval2 instanceof UnknownEval) {
									return true;

								}

								if (eval1.evaluate(cond1).intersects(eval2.evaluate(cond2))) {
									return true;
								}

							}
						}
					}
				}
			}
		}
		return false;
	}

}
