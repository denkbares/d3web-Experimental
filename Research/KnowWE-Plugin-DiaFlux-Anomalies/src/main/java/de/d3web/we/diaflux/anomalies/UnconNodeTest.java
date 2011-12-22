package de.d3web.we.diaflux.anomalies;

import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.knowwe.core.KnowWEEnvironment;

public class UnconNodeTest extends AbstractCITest {

	@Override
	public CITestResult call() throws Exception {
		String articleName = getParameter(0);
		String config = "knowledge base article: " + articleName;

		KnowledgeBase kb =
				D3webModule.getKnowledgeBase(
						KnowWEEnvironment.DEFAULT_WEB, articleName);
		
		CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);
		
		String resStr = "";
				
		if(null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);
			
			for(Flow flow : flowcharts) {
				List<Node> nodes = flow.getNodes();
				for(Node node : nodes) {
					if(!node.getClass().equals(StartNode.class)) {
						if(node.getIncomingEdges().size() < 1) {
							resStr += node.getFlow().getName() + ": " + node.getName() + " no incoming Edge \n";
						}
					}
					if(!node.getClass().equals(EndNode.class)) {
						if(node.getOutgoingEdges().size() < 1) {
							resStr += node.getFlow().getName() + ": " + node.getName() + " no outgoing Edge \n";
						}
					}
					
				}
			}
			if(!resStr.isEmpty()) {
				res = new CITestResult(Type.FAILED, resStr, config);
			}
			
		}
		return res;
	}

}
