package de.d3web.we.diaflux.anomalies;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.diaflux.pathcoloring.AnomalyManager;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.utils.Strings;

public class LoopWithoutSnapshotTest extends AbstractCITest {

	private enum Status {
		UNVISITED,
		VISITED,
		PROCESSING,
		SAVED
	}

	@Override
	public CITestResult call() throws Exception {
		String articleName = getParameter(0);
		String config = "knowledge base article: " + articleName;
		StringBuffer msg = new StringBuffer();
		HashMap<Node, Status> status = new HashMap<Node, Status>();
		List<Node> allNodes = new LinkedList<Node>();

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
			for (Node node : allNodes) {
				if (node instanceof StartNode || node instanceof SnapshotNode) {
					if (status.get(node) == Status.UNVISITED) {
						visit(node, msg, status);
					}
				}
			}

			String errormsg = msg.toString();
			if (!errormsg.isEmpty()) {
				Strings.maskHTML(errormsg);
				res = new CITestResult(Type.FAILED, errormsg, config);
			}
		}

		return res;
	}

	private void visit(Node node, StringBuffer errormsg, HashMap<Node, Status> status) {
		if (status.get(node) == Status.PROCESSING) {
			errormsg.append("missing Snapshot before " + node.getName() + " in Flowchart "
					+ node.getFlow().getName());
			AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
			anomalyManager.addAnomaly(node.getFlow(), node, "Loop without Snapshot");
		}
		if (errormsg.length() < 1) {
			if (node instanceof SnapshotNode) {
				status.put(node, Status.VISITED);
			}
			else if (node instanceof ComposedNode) {
				status.put(node, Status.PROCESSING);
				StartNode sNode = getStartFromComposed((ComposedNode) node);
				if (sNode != null) {
					visit(sNode, errormsg, status);
				}
			}
			else if (node instanceof EndNode) {
				status.put(node, Status.PROCESSING);
				for (ComposedNode cNode : getComposedfromExit((EndNode) node)) {
					for (Node fNode : getFollowingNodes(cNode)) {
						visit(fNode, errormsg, status);
					}
				}
			}
			else {
				status.put(node, Status.PROCESSING);
				for (Node fNode : getFollowingNodes(node)) {
					visit(fNode, errormsg, status);
				}
			}
		}
		status.put(node, Status.VISITED);
	}

	private void init(HashMap<Node, Status> status, List<Node> allNodes) {
		for (Node node : allNodes) {
			status.put(node, Status.UNVISITED);
		}
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
