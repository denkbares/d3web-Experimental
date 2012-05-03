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
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.diaflux.pathcoloring.AnomalyManager;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.utils.KnowWEUtils;

public class InconsistentValueTest extends AbstractCITest {

	private enum Status {
		UNVISITED,
		VISITED,
		PROCESSING,
	}

	@Override
	public CITestResult call() throws Exception {
		HashMap<Node, Status> status = new HashMap<Node, Status>();
		List<Node> allNodes = new LinkedList<Node>();
		List<String> unusedValues = new LinkedList<String>();
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
			String errormsg = error.toString();
			if (!errormsg.isEmpty()) {
				errormsg = "Inconsistent Value:" + errormsg;
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

	private String visit(Node node, HashMap<Node, Status> status, List<String> unusedValues) {
		String result = "";
		if (node instanceof EndNode) {
			status.put(node, Status.PROCESSING);
			for (ComposedNode cNode : getComposedfromExit((EndNode) node)) {
				// for (Node fNode : getFollowingNodes(cNode)) {
				result += visit(cNode, status, unusedValues);
				// }
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

	private List<String> getUsedValues(Edge edge) {
		List<String> result = new LinkedList<String>();
		for (TerminologyObject ob : edge.getCondition().getTerminalObjects()) {
			result.add(ob.getName());
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
}
