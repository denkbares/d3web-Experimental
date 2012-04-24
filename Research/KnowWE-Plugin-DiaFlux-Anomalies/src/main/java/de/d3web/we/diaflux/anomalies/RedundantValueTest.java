package de.d3web.we.diaflux.anomalies;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.indication.ActionNextQASet;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.utils.KnowWEUtils;

public class RedundantValueTest extends AbstractCITest {

	private enum Status {
		UNUSED,
		USING,
	}

	@Override
	public CITestResult call() throws Exception {
		HashMap<Node, Status> status = new HashMap<Node, Status>();
		List<Node> allNodes = new LinkedList<Node>();
		TreeSet<String> weakRemain = new TreeSet<String>();
		TreeSet<String> strongRemain = new TreeSet<String>();
		TreeSet<String> weakRedund = new TreeSet<String>();
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

			init(allNodes, status);

			for (Flow flow : flowcharts) {
				if (flow.isAutostart()) {
					for (StartNode sNode : flow.getStartNodes()) {
						visit(sNode, status, weakRemain, strongRemain, weakRedund);
					}
				}
			}
			weakRedund.removeAll(strongRemain);
			String weak = getRedundValues(weakRedund);
			if (!weak.isEmpty()) {
				error.append("weak Redundancy : <br>" + weak + "<br>");
			}
			String strong = getRedundValues(strongRemain);
			if (!strong.isEmpty()) {
				error.append("strong Redundancy : <br>" + strong);
			}

			String errormsg = error.toString();
			if (!errormsg.isEmpty()) {
				KnowWEUtils.maskHTML(errormsg);
				res = new CITestResult(Type.FAILED, errormsg, config);
			}
		}

		return res;
	}

	private void init(List<Node> allNodes, HashMap<Node, Status> status) {
		for (Node node : allNodes) {
			status.put(node, Status.UNUSED);
		}
	}

	private void visit(Node node, HashMap<Node, Status> status, TreeSet<String> weakRemain, TreeSet<String> strongRemain, TreeSet<String> weakRedund) {
		String actionUsedValue = null;
		List<String> var = new LinkedList<String>();
		status.put(node, Status.USING);
		if (node.getClass().equals(EndNode.class)) {
			weakRedund.addAll(weakRemain);
		}
		else if (node.getClass().equals(ActionNode.class)) {
			PSAction action = ((ActionNode) node).getAction();
			if (action instanceof ActionNextQASet) {
				var = getAskedValues((ActionNextQASet) action);
				weakRemain.addAll(var);
				strongRemain.addAll(var);
			}
			else if (action instanceof ActionSetValue) {
				actionUsedValue = ((ActionSetValue) action).getValue().toString();
				weakRemain.remove(actionUsedValue);
				strongRemain.remove(actionUsedValue);
			}
		}

		for (Edge edge : node.getOutgoingEdges()) {
			List<String> usedVars = getConditionVar(edge);
			strongRemain.removeAll(usedVars);

			TreeSet<String> weak = new TreeSet<String>();
			weak.addAll(weakRemain);
			weak.removeAll(usedVars);
			Node nextnode = edge.getEndNode();
			if (Status.UNUSED == status.get(nextnode)) {
				visit(nextnode, status, weak, strongRemain, weakRedund);
			}
		}

		status.put(node, Status.UNUSED);
	}

	private String getRedundValues(TreeSet<String> remains) {
		String result = "";
		for (String remain : remains) {
			result += remain + " is redundant <br>";
		}
		return result;
	}

	private List<String> getConditionVar(Edge edge) {
		List<String> result = new LinkedList<String>();
		for (TerminologyObject ob : edge.getCondition().getTerminalObjects()) {
			result.add(ob.getName());
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
}
