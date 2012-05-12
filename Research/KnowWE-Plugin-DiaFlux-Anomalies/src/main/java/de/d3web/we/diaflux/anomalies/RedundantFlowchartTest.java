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

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class RedundantFlowchartTest extends AbstractAnomalyTest {

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
	// List<Flow> unusedflows = new LinkedList<Flow>();
	//
	// if (null != kb) {
	// List<Flow> flowcharts =
	// kb.getManager().getObjects(Flow.class);
	// List<ComposedNode> cNodes = new LinkedList<ComposedNode>();
	//
	// // List of all ComposedNodes
	// for (Flow flow : flowcharts) {
	// List<Node> nodes = flow.getNodes();
	// for (Node node : nodes) {
	// if (node.getClass().equals(ComposedNode.class)) {
	// cNodes.add((ComposedNode) node);
	// }
	// }
	// }
	//
	// // List of all non-autostarting Flowcharts
	// for (Flow flow : flowcharts) {
	// if (!flow.isAutostart()) {
	// unusedflows.add(flow);
	// }
	// }
	//
	// // Comparing Flowcharts if called
	// for (ComposedNode cNode : cNodes) {
	// for (int i = 0; i < unusedflows.size(); i++) {
	// Flow flow = unusedflows.get(i);
	// if (flow.getName().equals(cNode.getCalledFlowName())) {
	// unusedflows.remove(flow);
	// }
	// }
	// }
	//
	// if (!unusedflows.isEmpty()) {
	// String redFlows = "";
	// for (Flow flow : unusedflows) {
	// redFlows += flow.getName() + " ";
	// }
	// res = new CITestResult(Type.FAILED, "Redundant Flowcharts: " + redFlows,
	// config);
	// }
	// }
	//
	// return res;
	// }

	@Override
	protected String test(KnowledgeBase kb) {
		List<Flow> unusedflows = new LinkedList<Flow>();
		String errormsg = "";

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
				for (int i = 0; i < unusedflows.size(); i++) {
					Flow flow = unusedflows.get(i);
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
				errormsg = "Redundant Flowcharts: " + redFlows;
			}
		}
		return errormsg;
	}

}
