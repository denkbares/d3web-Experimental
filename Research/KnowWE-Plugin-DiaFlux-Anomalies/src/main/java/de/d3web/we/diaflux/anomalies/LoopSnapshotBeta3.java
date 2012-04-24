package de.d3web.we.diaflux.anomalies;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.utils.KnowWEUtils;


public class LoopSnapshotBeta3 extends AbstractCITest {

	private enum Status {
		UNVISITED,
		VISITED,
		PROCESSING,
		SAVED
	}

	@Override
	public CITestResult call() throws Exception {
		HashMap<Node, Status> color = new HashMap<Node, Status>();
		List<Node> allNodes = new LinkedList<Node>();
		Stack<ComposedNode> superNodes = new Stack<ComposedNode>();
		Stack<Node> path = new Stack<Node>(); //implement as List and delete upper Stack
		Integer passSnaps = 0;
		StringBuffer errormsg = new StringBuffer();

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
			init(color, allNodes);

			for (Flow f : flowcharts) {
				if (f.isAutostart()) {
					for (Node node : f.getNodes()) {
						if (node instanceof StartNode) {
							visit(node, color, allNodes, superNodes, path, errormsg);
						}
					}
				}
			}

			String error = errormsg.toString();
			if (!error.isEmpty()) {
				KnowWEUtils.maskHTML(error);
				res = new CITestResult(Type.FAILED, error, config);
			}

		}
		return res;
	}

	private void init(HashMap<Node, Status> color, List<Node> allNodes) {
		for (Node node : allNodes) {
			color.put(node, Status.UNVISITED);
		}

	}

	private void visit(Node node, HashMap<Node, Status> color, List<Node> allNodes, Stack<ComposedNode> superNodes, Stack<Node> path, StringBuffer errormsg) {
		System.out.println("visiting " + node.getName() + " from " + node.getFlow().getName());
		path.push(node);
		if (node instanceof SnapshotNode) {
			for (Node n : allNodes) {
				if (color.get(n) == Status.PROCESSING) {
					System.out.println("Node " + n.getName() + " saved");
					color.put(node, Status.SAVED);
				}
			}
		}

		if (errormsg.length()< 1) {
			if (color.get(node) == Status.PROCESSING) {
				errormsg.append("Flowchart " + node.getFlow().getName() + " has Loop before "
						+ node.getName() + " without Snapshot");
			}
			else {
				color.put(node, Status.PROCESSING);
				System.out.println("Node " + node.getName() + " processing");
			}
			if (node instanceof ComposedNode) {
				ComposedNode cNode = (ComposedNode) node;
				superNodes.push(cNode);
				String nFlow = cNode.getCalledFlowName();
				String nStart = cNode.getCalledStartNodeName();
				List<Flow> flowcharts = node.getFlow().getKnowledgeBase().getManager().getObjects(
						Flow.class);
				for (Flow flow : flowcharts) {
					if (flow.getName().equals(nFlow)) {
						for (Node n : flow.getStartNodes()) {
							if (n.getName().equals(nStart)) {
								System.out.println("new Flowchart " + nFlow + " Node " + nStart);
									visit(n, color, allNodes, superNodes, path, errormsg); //No test if node already visited, because sub-flowcharts can be called numerous times
							}
						}
					}
				}
			}
			else if (node instanceof EndNode) {
				if (!superNodes.isEmpty()) {//superNodes should only be empty if the node is in the Autostart-Flowchart
					ComposedNode compNode = superNodes.pop();
					for (Edge e : compNode.getOutgoingEdges()) {
						if (color.get(e.getEndNode()) == Status.UNVISITED || color.get(e.getEndNode()) == Status.PROCESSING) {
							visit(e.getEndNode(), color, allNodes, superNodes, path, errormsg);
						}
					}
				}
			}
			else {
				for (Edge e : node.getOutgoingEdges()) {
					if (color.get(e.getEndNode()) == Status.UNVISITED || color.get(e.getEndNode()) == Status.PROCESSING) {
						visit(e.getEndNode(), color, allNodes, superNodes, path, errormsg);
					}
				}
			}
		}
		color.put(node, Status.VISITED);
		if (node instanceof SnapshotNode) {
			path.pop();
			while (!path.isEmpty()) {
				
				color.put(path.pop(), Status.PROCESSING);
			}
		}
	}

}
