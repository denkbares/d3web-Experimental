package de.d3web.we.diaflux.anomalies;

import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.CommentNode;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.diaflux.pathcoloring.AnomalyManager;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.utils.KnowWEUtils;

public class UnconNodeTest extends AbstractCITest {

	@Override
	public CITestResult call() throws Exception {
		String articleName = getParameter(0);
		String config = "knowledge base article: " + articleName;

		KnowledgeBase kb =
				D3webUtils.getKnowledgeBase(
						Environment.DEFAULT_WEB, articleName);

		CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);

		String resStr = "";

		if (null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);

			AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
			for (Flow flow : flowcharts) {
				List<Node> nodes = flow.getNodes();
				for (Node node : nodes) {
					if (!node.getClass().equals(CommentNode.class)) {
						if (!node.getClass().equals(StartNode.class)) {
							if (node.getIncomingEdges().size() < 1) {
								resStr += node.getFlow().getName() + ": " + node.getName()
										+ " no incoming Edge <br>";
								anomalyManager.addAnomaly(node.getFlow(), node, "Unconnected Node");
							}
						}
						if (!node.getClass().equals(EndNode.class)) {
							if (node.getOutgoingEdges().size() < 1) {
								resStr += node.getFlow().getName() + ": " + node.getName()
										+ " no outgoing Edge <br>";
								anomalyManager.addAnomaly(node.getFlow(), node, "Unconnected Node");
							}
						}
					}

				}
			}
			if (!resStr.isEmpty()) {
				KnowWEUtils.maskHTML(resStr);
				res = new CITestResult(Type.FAILED, resStr, config);
			}

		}
		return res;
	}

}
