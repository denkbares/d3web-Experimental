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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.datamanagement.FrameStack;
import de.d3web.we.diaflux.datamanagement.MOValue;
import de.d3web.we.diaflux.datamanagement.NumValue;
import de.d3web.we.diaflux.evaluators.Evaluator;
import de.d3web.we.diaflux.evaluators.EvaluatorManager;
import de.d3web.we.diaflux.pathcoloring.AnomalyManager;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class DeadPathTest extends AbstractAnomalyTest {

	private enum Status {
		UNVISITED,
		VISITED,
		PROCESSING,
		SAVED
	}

	private final EvaluatorManager evalManager = EvaluatorManager.getEvalManager();

	/**
	 *
	 */
	// @Override
	// public CITestResult call() throws Exception {
	// List<Node> allNodes = new LinkedList<Node>();
	// HashMap<Node, Status> status = new HashMap<Node, Status>();
	// String errormsg = "";
	//
	// String articleName = getParameter(0);
	// String config = "knowledge base article: " + articleName;
	//
	// KnowledgeBase kb =
	// D3webUtils.getKnowledgeBase(
	// Environment.DEFAULT_WEB, articleName);
	//
	// CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);
	//
	// if (null != kb) {
	// FrameStack stack = initStack(kb);
	//
	// List<Flow> flowcharts =
	// kb.getManager().getObjects(Flow.class);
	//
	// for (Flow flow : flowcharts) {
	// allNodes.addAll(flow.getNodes());
	// }
	// for (Node node : allNodes) {
	// status.put(node, Status.UNVISITED);
	// }
	// for (Flow flow : flowcharts) {
	// if (flow.isAutostart()) {
	// for (Node node : flow.getStartNodes()) {
	// errormsg += visit(node, status, stack);
	// }
	// }
	// }
	//
	// }
	// if (!errormsg.isEmpty()) {
	// errormsg = "Dead Path:<br>" + errormsg;
	// Strings.maskHTML(errormsg);
	// res = new CITestResult(Type.FAILED, errormsg, config);
	// }
	//
	// return res;
	// }
	/**
	 * 
	 * @created 08.05.2012
	 * @param node
	 * @param status
	 * @param stack
	 * @return
	 */
	private String visit(Node node, HashMap<Node, Status> status, FrameStack stack) {
		String result = "";

		if (node instanceof EndNode) {
			status.put(node, Status.PROCESSING);
			for (ComposedNode cNode : getComposedfromExit((EndNode) node)) {
				result += visit(cNode, status, stack);
			}
		}
		status.put(node, Status.PROCESSING);
		for (Edge e : node.getOutgoingEdges()) {
			EvalResult eRes = evaluateEdge(e);
			if (!stack.isEmpty()) {

				if (!stack.peek().contains(eRes)) {
					AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
					anomalyManager.addAnomaly(node.getFlow(), node, "Dead Path");
					result += "on " + e + "<br>";
				}

			}
			stack.push(eRes);
			Node nextNode = e.getEndNode();
			if (status.get(nextNode).equals(Status.UNVISITED)) {
				if (nextNode instanceof ComposedNode) {
					StartNode sNode = getStartFromComposed((ComposedNode) nextNode);
					if (sNode != null) {
						nextNode = sNode;
					}
				}
				result += visit(nextNode, status, stack);
			}
			if (!stack.isEmpty()) {
				stack.pop();
			}
		}
		status.put(node, Status.VISITED);

		return result;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param e
	 * @return
	 */
	private EvalResult evaluateEdge(Edge e) {
		EvalResult result = new EvalResult();
		Condition con = e.getCondition();
		for (Evaluator eval : evalManager.getEvaluator()) {
			if (eval.canEvaluate(con)) {
				result = eval.evaluate(con);
			}
		}
		return result;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param kb
	 * @return
	 */
	private FrameStack initStack(KnowledgeBase kb) {
		FrameStack stack = new FrameStack();
		EvalResult eRes = new EvalResult();
		for (QContainer q : kb.getManager().getQContainers()) {
			for (Question k : KnowledgeBaseUtils.getSuccessors(q, Question.class)) {
				if (k instanceof QuestionChoice) {
					Domain<MOValue> domain = new Domain<MOValue>();
					HashSet<String> posValue = new HashSet<String>();
					for (Choice c : ((QuestionChoice) k).getAllAlternatives()) {
						posValue.add(c.getName());
					}
					MOValue movalue = new MOValue(new HashSet<String>(), posValue,
							(k instanceof QuestionOC));
					domain.add(movalue);

					eRes.add(k.getName(), domain);

				}
				if (k instanceof QuestionNum) {
					Domain<NumValue> domain = new Domain<NumValue>();
					domain.add(new NumValue(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
							false, false));
					eRes.add(k.getName(), domain);

				}

			}
		}
		stack.push(eRes);
		return stack;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param node
	 * @return
	 */
	private StartNode getStartFromComposed(ComposedNode node) {
		String calledFlow = node.getCalledFlowName();
		String calledStart = node.getCalledStartNodeName();
		KnowledgeBase kb = node.getFlow().getKnowledgeBase();
		List<Flow> flowcharts =
				kb.getManager().getObjects(Flow.class);
		for (Flow flow : flowcharts) {
			if (flow.getName().equals(calledFlow)) {
				for (Node n : flow.getNodes()) {
					if (n.getName().equals(calledStart)) {
						return (StartNode) n;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param node
	 * @return
	 */
	private List<ComposedNode> getComposedfromExit(EndNode node) {
		List<ComposedNode> result = new LinkedList<ComposedNode>();
		String subFlow = node.getFlow().getName();
		KnowledgeBase kb = node.getFlow().getKnowledgeBase();
		List<Flow> flowcharts =
				kb.getManager().getObjects(Flow.class);
		for (Flow flow : flowcharts) {
			for (Node n : flow.getNodes()) {
				if (n instanceof ComposedNode) {
					ComposedNode cNode = (ComposedNode) n;
					if (cNode.getCalledFlowName().equals(subFlow)) {
						result.add(cNode);
					}
				}
			}
		}
		return result;
	}

	@Override
	protected String test(KnowledgeBase kb) {
		List<Node> allNodes = new LinkedList<Node>();
		HashMap<Node, Status> status = new HashMap<Node, Status>();
		String errormsg = "";
		if (null != kb) {
			FrameStack stack = initStack(kb);

			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);

			for (Flow flow : flowcharts) {
				allNodes.addAll(flow.getNodes());
			}
			for (Node node : allNodes) {
				status.put(node, Status.UNVISITED);
			}
			for (Flow flow : flowcharts) {
				if (flow.isAutostart()) {
					for (Node node : flow.getStartNodes()) {
						errormsg += visit(node, status, stack);
					}
				}
			}

		}
		return errormsg;
	}

}
