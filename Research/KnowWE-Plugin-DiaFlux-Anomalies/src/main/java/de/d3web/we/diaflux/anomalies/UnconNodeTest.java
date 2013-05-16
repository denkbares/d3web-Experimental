/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.we.diaflux.anomalies;

import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.CommentNode;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.we.diaflux.pathcoloring.AnomalyManager;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class UnconNodeTest extends AbstractAnomalyTest {

	/**
	 *
	 */
	// @Override
	// public CITestResult call() throws Exception {
	// String articleName = getParameter(0);
	// String config = "knowledge base article: " + articleName;
	//
	// KnowledgeBase kb =
	// D3webUtils.getKnowledgeBase(
	// Environment.DEFAULT_WEB, articleName);
	//
	// CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);
	//
	// String resStr = "";
	//
	// if (null != kb) {
	// List<Flow> flowcharts =
	// kb.getManager().getObjects(Flow.class);
	//
	// AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
	// for (Flow flow : flowcharts) {
	// List<Node> nodes = flow.getNodes();
	// for (Node node : nodes) {
	// if (!node.getClass().equals(CommentNode.class)) {
	// if (!node.getClass().equals(StartNode.class)) {
	// if (node.getIncomingEdges().size() < 1) {
	// resStr += node.getFlow().getName() + ": " + node.getName()
	// + " no incoming Edge <br>";
	// anomalyManager.addAnomaly(node.getFlow(), node, "Unconnected Node");
	// }
	// }
	// if (!node.getClass().equals(EndNode.class)) {
	// if (node.getOutgoingEdges().size() < 1) {
	// resStr += node.getFlow().getName() + ": " + node.getName()
	// + " no outgoing Edge <br>";
	// anomalyManager.addAnomaly(node.getFlow(), node, "Unconnected Node");
	// }
	// }
	// }
	//
	// }
	//
	// }
	// if (!resStr.isEmpty()) {
	// Strings.maskHTML(resStr);
	// res = new CITestResult(Type.FAILED, resStr, config);
	// }
	// }
	// return res;
	// }

	@Override
	protected String test(KnowledgeBase kb) {
		String resStr = "";

		if (null != kb) {
			FlowSet flowSet = DiaFluxUtils.getFlowSet(kb);

			AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
			for (Flow flow : flowSet) {
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
		}
		return resStr;
	}

}
