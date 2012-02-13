package de.d3web.we.diaflux.anomalies;

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;

public class RedundantStartNodeTest extends AbstractCITest {

	@Override
	public CITestResult call() throws Exception {

		String articleName = getParameter(0);
		String config = "knowledge base article: " + articleName;

		KnowledgeBase kb =
				D3webUtils.getKnowledgeBase(
						KnowWEEnvironment.DEFAULT_WEB, articleName);

		CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);

		if (null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);

			List<StartNode> allStartNodes = new LinkedList<StartNode>();
			List<ComposedNode> cNodes = new LinkedList<ComposedNode>();

			// List of all StartNodes except for autostarting Flowchart
			for (Flow flow : flowcharts) {
				if (!flow.isAutostart()) {
					List<StartNode> startNodes = flow.getStartNodes();
					for (StartNode start : startNodes) {
						allStartNodes.add(start);
					}
				}
			}

			// List of all ComposedNodes
			for (Flow flow : flowcharts) {
				List<Node> nodes = flow.getNodes();
				for (Node node : nodes) {
					if (node.getClass().equals(ComposedNode.class)) {
						ComposedNode cNode = (ComposedNode) node;
						cNodes.add(cNode);
					}
				}
			}

			// Comparing Flowchartcallings with Startnodenames
			for (ComposedNode cNode : cNodes) {
				String flowName = cNode.getCalledFlowName();
				String startName = cNode.getCalledStartNodeName();

				for (StartNode sNode : allStartNodes) {
					if (sNode.getFlow().getName().equals(flowName)) {
						if (sNode.getName().equals(startName)) {
							allStartNodes.remove(sNode);
						}
					}
				}
			}
			if (!allStartNodes.isEmpty()) {
				String redStarts = "";
				for (StartNode node : allStartNodes) {
					redStarts += "in " + node.getFlow().getName() + " node " + node.getName();
				}
				res = new CITestResult(Type.FAILED, redStarts + " is redundant", config);
			}
		}

		return res;
	}

}
