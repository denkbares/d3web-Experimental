package de.d3web.we.diaflux.anomalies;

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;

public class RedundantFlowchartTest extends AbstractCITest {

	@Override
	public CITestResult call() throws Exception {

		String articleName = getParameter(0);
		String config = "knowledge base article: " + articleName;

		KnowledgeBase kb =
				D3webUtils.getKnowledgeBase(
						Environment.DEFAULT_WEB, articleName);

		CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);

		List<Flow> unusedflows = new LinkedList<Flow>();

		if (null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);
			List<ComposedNode> cNodes = new LinkedList<ComposedNode>();

			// List of all ComposedNodes
			for (Flow flow : flowcharts) {
				List<Node> nodes = flow.getNodes();
				for (Node node : nodes) {
					if (node.getClass().equals(ComposedNode.class)) {
						cNodes.add((ComposedNode) node);
					}
				}
			}

			// List of all non-autostarting Flowcharts
			for (Flow flow : flowcharts) {
				if (!flow.isAutostart()) {
					unusedflows.add(flow);
				}
			}

			// Comparing Flowcharts if called
			for (ComposedNode cNode : cNodes) {
				for (Flow flow : unusedflows) {
					if (flow.getName().equals(cNode.getCalledFlowName())) {
						unusedflows.remove(flow);
					}
				}
			}

			if (!unusedflows.isEmpty()) {
				String redFlows = "";
				for (Flow flow : unusedflows) {
					redFlows += flow.getName() + " ";
				}
				res = new CITestResult(Type.FAILED, "Redundant Flowcharts: " + redFlows, config);
			}
		}

		return res;
	}

}
