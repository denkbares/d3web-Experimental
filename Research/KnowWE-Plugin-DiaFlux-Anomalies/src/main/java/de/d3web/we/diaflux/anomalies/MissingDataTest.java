package de.d3web.we.diaflux.anomalies;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.abstraction.formula.QNumWrapper;
import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.KnowledgeBase;
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
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.evaluators.Evaluator;
import de.d3web.we.diaflux.evaluators.EvaluatorManager;
import de.d3web.we.diaflux.pathcoloring.AnomalyManager;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.utils.KnowWEUtils;

public class MissingDataTest extends AbstractCITest {

	private enum Status {
		UNVISITED,
		VISITED,
		PROCESSING,
	}

	private EvaluatorManager evalManager = EvaluatorManager.getEvalManager();

	@Override
	public CITestResult call() throws Exception {
		HashMap<Node, Status> status = new HashMap<Node, Status>();
		List<Node> allNodes = new LinkedList<Node>();
		List<String> askedNodes = new LinkedList<String>();
		StringBuffer error = new StringBuffer();

		String articleName = getParameter(0);
		String config = "knowledge base article: " + articleName;

		KnowledgeBase kb =
				D3webUtils.getKnowledgeBase(
						Environment.DEFAULT_WEB, articleName);

		CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);

		if (null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);
			AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();

			for (Flow flow : flowcharts) {
				allNodes.addAll(flow.getNodes());
			}
			init(status, allNodes);
			
			for (Flow flow : flowcharts) {
				if (flow.isAutostart()) {
					for (Node node : flow.getStartNodes()) {
						error.append(visit(node, status, askedNodes));
					}
				}
			}
			String errormsg = error.toString();
			if (!errormsg.isEmpty()) {
				errormsg = "Missing Data:" + errormsg;
				KnowWEUtils.maskHTML(errormsg);
				res = new CITestResult(Type.FAILED, errormsg, config);
			}
		}
		return res;
	}

	private void init(HashMap<Node, Status> status, List<Node> allNodes) {
		for (Node node : allNodes) {
			status.put(node, Status.UNVISITED);
		}
	}

	private String visit(Node node, HashMap<Node, Status> status, List<String> askedNodes) {
		AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
		String result = "";
		if (node instanceof EndNode) {
			status.put(node, Status.PROCESSING);
			for (ComposedNode cNode : getComposedfromExit((EndNode) node)) {
//				for (Node fNode : getFollowingNodes(cNode)) {
					result += visit(cNode, status, askedNodes);
//				}
			}
		}
		status.put(node, Status.PROCESSING);
		List<String> askedList = new LinkedList<String>();
		if (node.getClass().equals(ActionNode.class)) {
			PSAction action = ((ActionNode) node).getAction();
			
			if (action instanceof ActionNextQASet) {
				askedList = getAskedValues((ActionNextQASet) action);
				askedNodes.addAll(askedList);
			}
			
			if (action instanceof ActionSetValue) {
				ActionSetValue actionSet = (ActionSetValue) action;
				
				if (actionSet.getValue() instanceof QNumWrapper) {
					QNumWrapper wrapper = (QNumWrapper) actionSet.getValue();
					String key = wrapper.getQuestion().getName();
					
					if (!askedNodes.contains(key)) {
						anomalyManager.addAnomaly(node.getFlow(), node, "Intersecting Postcondition");
						result += "<br>" + key + " on Node " + node;
					}
				}
			}
		}

		for (Edge edge : node.getOutgoingEdges()) {
			result += testMissingVar(edge, askedNodes);
			Node nextNode = edge.getEndNode();
			if (Status.UNVISITED == status.get(nextNode)) {
				if(nextNode instanceof ComposedNode) {
					StartNode sNode = getStartFromComposed((ComposedNode) nextNode);
					if (sNode != null) {
						nextNode = sNode;
					}
				}
				result += visit(nextNode, status, askedNodes);
			}
		}
		status.put(node, Status.VISITED);
		for (String str : askedList) {
			askedNodes.remove(str);
		}
		return result;
	}

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

	private String testMissingVar(Edge edge, List<String> askedNodes) {
		AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
		String result = "";
		for (Evaluator eval : evalManager.getEvaluator()) {
			if (eval.canEvaluate(edge.getCondition())) {
				EvalResult eRes = eval.evaluate(edge.getCondition());
				for (String key : eRes.getVariables()) {
					if (!askedNodes.contains(key)) {
						anomalyManager.addAnomaly(edge.getEndNode().getFlow(), edge, "Intersecting Postcondition");
						result += "<br>" + key + " on Edge " + edge;
					}
				}
			}
		}
		return result;
	}
	
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

	private List<Node> getFollowingNodes(Node node) {
		List<Node> result = new LinkedList<Node>();
		for (Edge e : node.getOutgoingEdges()) {
			result.add(e.getEndNode());
		}
		return result;
	}
}
