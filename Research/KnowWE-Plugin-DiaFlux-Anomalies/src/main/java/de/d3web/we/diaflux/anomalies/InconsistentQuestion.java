package de.d3web.we.diaflux.anomalies;

import java.util.Hashtable;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;

public class InconsistentQuestion extends AbstractCITest {

	@Override
	public CITestResult call() throws Exception {
		String articleName = getParameter(0);
		String config = "knowledge base article: " + articleName;

		KnowledgeBase kb =
				D3webUtils.getKnowledgeBase(
						KnowWEEnvironment.DEFAULT_WEB, articleName);

		CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);

		Hashtable<String, Node> knownQuestions = new Hashtable<String, Node>();
		// String wenn Name der knoten gleich, sonst nachschauen

		if (null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);
			for (Flow flow : flowcharts) {
				List<Node> nodes = flow.getNodes();
				for (Node node : nodes) {
					if (node.getClass().equals(ActionNode.class)) {
						// richtige klasse raussuchen
						if (!knownQuestions.containsKey(node.getName())) {
							knownQuestions.put(node.getName(), node);
						}
						else {
							// hier vergleichen
						}
					}
				}
			}

		}

		return res;
	}

}
