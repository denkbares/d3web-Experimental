package de.d3web.we.diaflux.anomalies;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.utils.KnowWEUtils;


public class LoopSnapshotBeta extends AbstractCITest {
	
	private HashMap<Node, Status> color = new HashMap<Node, Status>();
	private List<Node> allNodes = new LinkedList<Node>();

//	private boolean hasSnapshot = false;
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
				D3webUtils.getKnowledgeBase(
						Environment.DEFAULT_WEB, articleName);
		
		CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);
		
		if(null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);
			for(Flow flow : flowcharts) {
				allNodes.addAll(flow.getNodes());
			}
			init();
			if(!errormsg.isEmpty()) {
				KnowWEUtils.maskHTML(errormsg);
				res = new CITestResult(Type.FAILED, errormsg, config);
			}
		}
		
		return res;
	}
	
	private void init() {
		for(Node node : allNodes) {
			color.put(node, Status.UNVISITED);
		}
		for(Node node : allNodes) {
			if(Status.UNVISITED == color.get(node)) {
				visit(node);
			}
		}
		
	}
	
	private void visit(Node node) {
		
		if(node.getClass().equals(SnapshotNode.class)) {
			for(Node saveNode : allNodes) {
				if(color.get(saveNode).equals(Status.PROCESSING)) {
					color.put(saveNode, Status.SAVED);
				}
			}
		}
		if(color.get(node).equals(Status.PROCESSING)){
			errormsg = "Flowchart " + node.getFlow().getName() + " has Loop before " + node.getName() + " without Snapshot";
//			errormsg = node.getName();
		}
		color.put(node, Status.PROCESSING);
		if(errormsg.isEmpty()){
			for(Edge edge : node.getOutgoingEdges()) {
				Node nextnode = edge.getEndNode();
				if(color.get(nextnode).equals(Status.UNVISITED) || color.get(nextnode).equals(Status.PROCESSING)) {
					visit(nextnode);
				}
			}
		}
		color.put(node, Status.VISITED);
	}
}
