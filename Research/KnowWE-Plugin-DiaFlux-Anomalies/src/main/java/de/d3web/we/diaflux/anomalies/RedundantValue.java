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
import de.d3web.we.basic.D3webModule;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.knowwe.core.KnowWEEnvironment;


public class RedundantValue extends AbstractCITest {

	private HashMap<Node, Status> status = new HashMap<Node, Status>();
	private List<Node> allNodes = new LinkedList<Node>();
	private TreeSet<String> knownNodes = new TreeSet<String>();

	private String errormsg = "";
	
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
				D3webModule.getKnowledgeBase(
						KnowWEEnvironment.DEFAULT_WEB, articleName);
		
		CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);
		
		if(null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);
			
			for(Flow flow : flowcharts) {
				allNodes.addAll(flow.getNodes());
			}
			
			init();
			
			if(!errormsg.isEmpty()) {
				res = new CITestResult(Type.FAILED, errormsg, config);
			}
		}
		
		return res;
	}

	private void init() {
		for(Node node : allNodes) {
			status.put(node, Status.UNVISITED);
		}
		for(Node node : allNodes) {
			if(Status.UNVISITED == status.get(node)) {
//				System.out.println("Node " + node.getName() + " visiting");
				visit(node);
			}
		}
		
	}
	
	private void visit(Node node) {
		status.put(node, Status.PROCESSING);
		System.out.println("Nodeklasse " + node.getClass()); //richtige Nodes finden
		if(node.getClass().equals(ActionNode.class)) {
			knownNodes.add(node.getName());
		}
		
		for(Edge edge : node.getOutgoingEdges()) {
			Node nextnode = edge.getEndNode();
			if(Status.UNVISITED == status.get(node)) {
				visit(nextnode);
			}
		}
		status.put(node, Status.VISITED);
	}
}