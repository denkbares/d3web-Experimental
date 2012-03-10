package de.d3web.we.diaflux.anomalies;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.indication.ActionIndication;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;

public class MissingDataTest extends AbstractCITest {

	private final HashMap<Node, Status> status = new HashMap<Node, Status>();
	private final List<Node> allNodes = new LinkedList<Node>();
	private final TreeSet<String> knownNodes = new TreeSet<String>();

	private final String errormsg = "";

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

			if (!errormsg.isEmpty()) {
				res = new CITestResult(Type.FAILED, errormsg, config);
			}
		}

		return res;
	}

	private void init() {
		for (Node node : allNodes) {
			status.put(node, Status.UNVISITED);
		}
		for (Node node : allNodes) {
			if (Status.UNVISITED == status.get(node)) {
				// System.out.println("Node " + node.getName() + " visiting");
				visit(node);
			}
		}

	}

	private void visit(Node node) {
		status.put(node, Status.PROCESSING);
		if (node.getClass().equals(ActionNode.class)) {
			ActionNode aNode = (ActionNode) node;
			System.out.println("=========== ");
			System.out.println(aNode.getAction().getClass());
			/*
			 * indicate: ActionIndication.class set: ActionSetValue.class
			 */
			ActionIndication a;
			if (aNode.getAction().toString().equals("NOOP")) {
				System.out.println("-------");
				System.out.println("NOOP-Node");
				System.out.println("-------");
			}
			// knownNodes.add(node.getName());
		}

		for (Edge edge : node.getOutgoingEdges()) {
			Node nextnode = edge.getEndNode();
			if (Status.UNVISITED == status.get(node)) {
				visit(nextnode);
			}
		}
		status.put(node, Status.VISITED);
	}
}
