package de.d3web.we.diaflux.anomalies;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;

public class IntersectingPostconditionTest extends AbstractCITest {

	@Override
	public CITestResult call() throws Exception {

		//
		System.out.println("-----");
		//

		String articleName = getParameter(0);
		String config = "knowledge base article: " + articleName;

		String errormsg = "";

		KnowledgeBase kb =
				D3webUtils.getKnowledgeBase(
						Environment.DEFAULT_WEB, articleName);

		CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);

		if (null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);
			for (Flow flow : flowcharts) {
				for (Node node : flow.getNodes()) {
					System.out.println("Node " + node.getName());
					if (node.getClass().equals(ActionNode.class)) { // richtige
																	// Klasse
																	// raussuchen
						List<Condition> conditions = new LinkedList<Condition>();
						for (Edge edge : node.getOutgoingEdges()) {
							// System.out.println(edge.getCondition().toString());
							// //richtige Conditions raussuchen
							conditions.add(edge.getCondition());
						}
						Iterator<Condition> iter = conditions.iterator();
						while (iter.hasNext()) {
							Condition condition = iter.next();
							for (Condition comparCon : conditions) {
								if (!condition.equals(comparCon)) {
									Class cls = condition.getClass();
									// Method meth = cls.getm
									System.out.println("Class " + condition.getClass() + " string "
											+ condition.toString());
								}
							}
						}
					}
				}
			}
		}

		if (!errormsg.isEmpty()) {
			res = new CITestResult(Type.FAILED, "Postconditions are intersecting: " + errormsg,
					config);
		}
		return res;
	}

}
