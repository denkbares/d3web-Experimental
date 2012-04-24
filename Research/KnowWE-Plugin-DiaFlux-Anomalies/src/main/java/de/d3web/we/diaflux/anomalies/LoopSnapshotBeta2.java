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
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.utils.KnowWEUtils;


public class LoopSnapshotBeta2 extends AbstractCITest{
	private HashMap<Node, Status> color = new HashMap<Node, Status>();
	private List<Node> allNodes = new LinkedList<Node>();

	// private boolean hasSnapshot = false;
	private String errormsg = "";

	private enum Status {
		UNVISITED,
		VISITED,
		PROCESSING,
		SAVED
	}

	@Override
	public CITestResult call() throws Exception {
		 System.out.println("--------------------");
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
			init();
//			for (Flow flow : flowcharts) {
				for(Node node : allNodes){
//				if (flow.isAutostart()) {
//					for (Node node : flow.getStartNodes()) {
						if (node instanceof StartNode || node instanceof SnapshotNode) {
							for (Node saveNode : allNodes) {
								if (color.get(saveNode).equals(Status.PROCESSING)) {
									// System.out.println("Node " + node.getName() +
									// " saving");
									color.put(saveNode, Status.VISITED);
								}
							}
							if (!(Status.VISITED == color.get(node))) {
								System.out.println("Node " + node.getName() +
										" visiting from " + node.getFlow().getName());
								visit(node);
							}
						}
				}
//					}
//				}
//			}
			if (!errormsg.isEmpty()) {
				KnowWEUtils.maskHTML(errormsg);
				res = new CITestResult(Type.FAILED, errormsg, config);
			}

		}
		return res;
	}

	private void init() {
		for (Node node : allNodes) {
			color.put(node, Status.UNVISITED);
		}

	}

	private void visit(Node node) {
		if (color.get(node).equals(Status.PROCESSING)) {
			errormsg = "Flowchart " + node.getFlow().getName() + " has Loop before "
					+ node.getName() + " without Snapshot";
			System.out.println("errormsg " + errormsg);
			// errormsg = node.getName();
		} else {
		color.put(node, Status.PROCESSING);
		}
		 System.out.println("Node " + node.getName() + " processing from " + node.getFlow().getName());
		if (errormsg.isEmpty()) {
			if (node instanceof ComposedNode) {
				ComposedNode cNode = (ComposedNode) node;
				String nFlow = cNode.getCalledFlowName();
				String nStart = cNode.getCalledStartNodeName();
				List<Flow> flowcharts = node.getFlow().getKnowledgeBase().getManager().getObjects(
						Flow.class);
				for (Flow flow : flowcharts) {
					if (flow.getName().equals(nFlow)) {
						for (Node n : flow.getStartNodes()) {
							if (n.getName().equals(nStart)) {
								System.out.println("new Flowchart " + nFlow + " Node " + nStart);
								visit(n);
							}
						}
					}
				}
			} else if (node instanceof EndNode) {
				/*List<ComposedNode> composed = getComposedNodes(node.getFlow().getKnowledgeBase());
				for(ComposedNode compNode : composed) {
//					if
				}*/
				List<Node> nextNodes = getComposedNext(node);
				for(Node nNode : nextNodes) {
					System.out.println("exiting composed " + node.getFlow().getName() + " into " + nNode.getName());
					visit(nNode);
				}
			}
			/*
			 * if(node instanceof EndNode && !node.getFlow().isAutostart()) {
			 * node = superNode; }
			 */
			for (Edge edge : node.getOutgoingEdges()) {
				Node nextnode = edge.getEndNode();
				// System.out.println("Node " + nextnode.getName() +
				// " is nextNodeTest");
				if (nextnode.getClass().equals(SnapshotNode.class)) {
					/*for (Node saveNode : allNodes) {
						if (color.get(saveNode).equals(Status.PROCESSING)) {
							// System.out.println("Node " + node.getName() +
							// " saving");
							color.put(saveNode, Status.SAVED);
						}
					}*/
				}
				else if (color.get(nextnode).equals(Status.UNVISITED)
						|| color.get(nextnode).equals(Status.PROCESSING)) {
					// System.out.println("Node " + node.getName() +
					// " real next");
					System.out.println("Next Node " + nextnode.getName());
					visit(nextnode);
				}
			}
		}
		color.put(node, Status.VISITED);
	}

	private List<ComposedNode> getComposedNodes(KnowledgeBase kb) {
		List<ComposedNode> composedNodes = new LinkedList<ComposedNode>();
		List<Flow> flows = kb.getManager().getObjects(Flow.class);
		for(Flow flow : flows) {
			for(Node node : flow.getNodes()) {
				if(node instanceof ComposedNode) {
					composedNodes.add((ComposedNode)node);
				}
			}
		}
		return composedNodes;
	}
	
	private List<Node> getComposedNext(Node node) {
		List<Node> nextnodes = new LinkedList<Node>();
		
		List<ComposedNode> compNodes = getComposedNodes(node.getFlow().getKnowledgeBase());
		for(ComposedNode cNode : compNodes) {
			if(cNode.getCalledFlowName().equals(node.getFlow().getName())) {
			System.out.println("cNode is " + cNode.getCalledFlowName() + " and wanted flow is " + node.getFlow().getName());
			for(Edge edge : cNode.getOutgoingEdges()) {
				nextnodes.add(edge.getEndNode());
			}
			}
		}
		return nextnodes;
	}
}
