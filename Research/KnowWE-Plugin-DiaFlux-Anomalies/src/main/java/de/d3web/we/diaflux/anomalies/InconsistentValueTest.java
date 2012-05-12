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
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.indication.ActionNextQASet;
import de.d3web.we.diaflux.pathcoloring.AnomalyManager;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class InconsistentValueTest extends AbstractAnomalyTest {

	private enum Status {
		UNVISITED,
		VISITED,
		PROCESSING,
	}

	/**
	 *
	 */
	// @Override
	// public CITestResult call() throws Exception {
	// HashMap<Node, Status> status = new HashMap<Node, Status>();
	// List<Node> allNodes = new LinkedList<Node>();
	// List<String> unusedValues = new LinkedList<String>();
	// StringBuffer error = new StringBuffer();
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
	// List<Flow> flowcharts =
	// kb.getManager().getObjects(Flow.class);
	// for (Flow flow : flowcharts) {
	// allNodes.addAll(flow.getNodes());
	// }
	// init(status, allNodes);
	// for (Flow flow : flowcharts) {
	// if (flow.isAutostart()) {
	// for (Node node : flow.getStartNodes()) {
	// error.append(visit(node, status, unusedValues));
	// }
	// }
	// }
	// String errormsg = error.toString();
	// if (!errormsg.isEmpty()) {
	// errormsg = "Inconsistent Value:" + errormsg;
	// Strings.maskHTML(errormsg);
	// res = new CITestResult(Type.FAILED, errormsg, config);
	// }
	//
	// }
	//
	// return res;
	// }

	/**
	 * 
	 * @created 08.05.2012
	 * @param status
	 * @param allNodes
	 */
	private void init(HashMap<Node, Status> status, List<Node> allNodes) {
		for (Node node : allNodes) {
			status.put(node, Status.UNVISITED);
		}
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param node
	 * @param status
	 * @param unusedValues
	 * @return
	 */
	private String visit(Node node, HashMap<Node, Status> status, List<String> unusedValues) {
		String result = "";
		if (node instanceof EndNode) {
			status.put(node, Status.PROCESSING);
			for (ComposedNode cNode : getComposedfromExit((EndNode) node)) {
				result += visit(cNode, status, unusedValues);
			}
		}
		List<String> unused = new LinkedList<String>();
		status.put(node, Status.PROCESSING);
		List<String> askedList = new LinkedList<String>();
		if (node.getClass().equals(ActionNode.class)) {
			PSAction action = ((ActionNode) node).getAction();
			if (action instanceof ActionNextQASet) {
				askedList = getAskedValues((ActionNextQASet) action);
				unusedValues.addAll(askedList);
			}
			unused.addAll(unusedValues);
			if (action instanceof ActionSetValue) {
				ActionSetValue actionSet = (ActionSetValue) action;
				String actionUsedValue = actionSet.getQuestion().getName();
				if (unusedValues.contains(actionUsedValue)) {
					result += actionUsedValue + " in " + node;
					AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
					anomalyManager.addAnomaly(node.getFlow(), node, "Inconsistent Value");
				}
				unused.remove(actionSet.getValue().toString());
			}
		}
		for (Edge edge : node.getOutgoingEdges()) {

			unused.removeAll(getUsedValues(edge));

			Node nextNode = edge.getEndNode();
			if (Status.UNVISITED == status.get(nextNode)) {
				if (nextNode instanceof ComposedNode) {
					StartNode sNode = getStartFromComposed((ComposedNode) nextNode);
					if (sNode != null) {
						nextNode = sNode;
					}
				}
				result += visit(nextNode, status, unused);
			}
		}
		return result;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param action
	 * @return
	 */
	private List<String> getAskedValues(ActionNextQASet action) {
		List<String> result = new LinkedList<String>();

		for (QASet set : action.getQASets()) {
			if (set instanceof QContainer) {
				QContainer qc = (QContainer) set;
				List<Question> questions = KnowledgeBaseUtils.getSuccessors(qc, Question.class);
				for (Question q : questions) {
					result.add(q.getName());
				}
			}
			else {
				result.add(set.getName());
			}
		}
		return result;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param edge
	 * @return
	 */
	private List<String> getUsedValues(Edge edge) {
		List<String> result = new LinkedList<String>();
		for (TerminologyObject ob : edge.getCondition().getTerminalObjects()) {
			result.add(ob.getName());
		}
		return result;
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
		HashMap<Node, Status> status = new HashMap<Node, Status>();
		List<Node> allNodes = new LinkedList<Node>();
		List<String> unusedValues = new LinkedList<String>();
		StringBuffer error = new StringBuffer();
		if (null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);
			for (Flow flow : flowcharts) {
				allNodes.addAll(flow.getNodes());
			}
			init(status, allNodes);
			for (Flow flow : flowcharts) {
				if (flow.isAutostart()) {
					for (Node node : flow.getStartNodes()) {
						error.append(visit(node, status, unusedValues));
					}
				}
			}
		}
		return error.toString();
	}

}
