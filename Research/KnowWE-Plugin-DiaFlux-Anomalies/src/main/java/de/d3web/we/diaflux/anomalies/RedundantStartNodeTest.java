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

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.we.diaflux.pathcoloring.AnomalyManager;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class RedundantStartNodeTest extends AbstractAnomalyTest {

	/**
	 *
	 */
	// @Override
	// public CITestResult call() throws Exception {
	//
	// String articleName = getParameter(0);
	// String config = "knowledge base article: " + articleName;
	//
	// KnowledgeBase kb =
	// D3webUtils.getKnowledgeBase(
	// Environment.DEFAULT_WEB, articleName);
	//
	// CITestResult res = new CITestResult(Type.SUCCESSFUL, null, config);
	//
	// if (null != kb) {
	// List<Flow> flowcharts =
	// kb.getManager().getObjects(Flow.class);
	//
	// List<StartNode> allStartNodes = new LinkedList<StartNode>();
	// List<ComposedNode> cNodes = new LinkedList<ComposedNode>();
	//
	// // List of all StartNodes except for autostarting Flowchart
	// for (Flow flow : flowcharts) {
	// if (!flow.isAutostart()) {
	// List<StartNode> startNodes = flow.getStartNodes();
	// for (StartNode start : startNodes) {
	// allStartNodes.add(start);
	// }
	// }
	// }
	//
	// // List of all ComposedNodes
	// for (Flow flow : flowcharts) {
	// List<Node> nodes = flow.getNodes();
	// for (Node node : nodes) {
	// if (node.getClass().equals(ComposedNode.class)) {
	// ComposedNode cNode = (ComposedNode) node;
	// cNodes.add(cNode);
	// }
	// }
	// }
	//
	// // Comparing Flowchartcallings with Startnodenames
	// for (ComposedNode cNode : cNodes) {
	// String flowName = cNode.getCalledFlowName();
	// String startName = cNode.getCalledStartNodeName();
	//
	// for (int i = 0; i < allStartNodes.size(); i++) {
	// StartNode sNode = allStartNodes.get(i);
	// if (sNode.getFlow().getName().equals(flowName)) {
	// if (sNode.getName().equals(startName)) {
	// allStartNodes.remove(sNode);
	// }
	// }
	// }
	// }
	// if (!allStartNodes.isEmpty()) {
	// AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
	// String redStarts = "";
	// for (StartNode node : allStartNodes) {
	// redStarts += "in " + node.getFlow().getName() + " node " +
	// node.getName();
	// anomalyManager.addAnomaly(node.getFlow(), node, "Unconnected Node");
	// }
	// res = new CITestResult(Type.FAILED, redStarts + " is redundant", config);
	// }
	// }
	//
	// return res;
	// }

	@Override
	protected String test(KnowledgeBase kb) {
		String errormsg = "";
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

				for (int i = 0; i < allStartNodes.size(); i++) {
					StartNode sNode = allStartNodes.get(i);
					if (sNode.getFlow().getName().equals(flowName)) {
						if (sNode.getName().equals(startName)) {
							allStartNodes.remove(sNode);
						}
					}
				}
			}
			if (!allStartNodes.isEmpty()) {
				AnomalyManager anomalyManager = AnomalyManager.getAnomalyManager();
				String redStarts = "";
				for (StartNode node : allStartNodes) {
					redStarts += "in " + node.getFlow().getName() + " node " + node.getName();
					anomalyManager.addAnomaly(node.getFlow(), node, "Unconnected Node");
				}
				errormsg = redStarts + " is redundant";
			}
		}
		return errormsg;
	}

}
