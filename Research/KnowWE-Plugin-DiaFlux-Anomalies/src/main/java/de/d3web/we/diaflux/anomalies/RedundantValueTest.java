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

import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaflux.coverage.Path;
import de.d3web.diaflux.coverage.PathGenerator;
import de.d3web.we.diaflux.anomalystrategies.RedundantValueStrategy;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class RedundantValueTest extends AbstractAnomalyTest {

	// private enum Status {
	// UNUSED,
	// USING,
	// }

	/**
	 *
	 */
	// @Override
	// public CITestResult call() throws Exception {
	// HashMap<Node, Status> status = new HashMap<Node, Status>();
	// List<Node> allNodes = new LinkedList<Node>();
	//
	// TreeSet<String> everUsed = new TreeSet<String>();
	// TreeMap<String, Node> everAsked = new TreeMap<String, Node>();
	// TreeSet<String> weakRemain = new TreeSet<String>();
	//
	// StringBuffer error = new StringBuffer();
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
	// for (Flow flow : flowcharts) {
	// allNodes.addAll(flow.getNodes());
	// }
	//
	// init(allNodes, status);
	//
	// WholePathStrategy strategy = new WholePathStrategy(kb);
	// PathGenerator generator = new PathGenerator(kb, strategy);
	// generator.createPaths();
	//
	// // for (Flow flow : flowcharts) {
	// // if (flow.isAutostart()) {
	// // for (StartNode sNode : flow.getStartNodes()) {
	// // visit(sNode, status, weakRemain, strongRemain, weakRedund);
	// // }
	// // }
	// // }
	// for (Path path : generator.getFinalPaths()) {
	// Iterator<DiaFluxElement> it = path.iterator();
	//
	// TreeMap<String, Node> actualAsked = new TreeMap<String, Node>();
	// TreeSet<String> actualUsed = new TreeSet<String>();
	//
	// while (it.hasNext()) {
	// DiaFluxElement el = it.next();
	// if (el instanceof ActionNode) {
	// PSAction action = ((ActionNode) el).getAction();
	// if (action instanceof ActionNextQASet) {
	// List<String> var = getAskedValues((ActionNextQASet) action);
	// // weakRemain.addAll(var);
	// // strongRemain.addAll(var);
	// for (String st : var) {
	// actualAsked.put(st, (Node) el);
	// }
	// }
	// else if (action instanceof ActionSetValue) {
	//
	// String actionUsedValue = ((ActionSetValue) action).getValue().toString();
	// // weakRemain.remove(actionUsedValue);
	// // strongRemain.remove(actionUsedValue);
	// actualUsed.add(actionUsedValue);
	// // TODO value which is set to actualAsked?
	// }
	// }
	// else if (el instanceof Edge) {
	//
	// Edge edge = (Edge) el;
	// List<String> usedVars = getConditionVar(edge);
	// // strongRemain.removeAll(usedVars);
	// actualUsed.addAll(usedVars);
	// // TreeSet<String> weak = new TreeSet<String>();
	// // weak.addAll(weakRemain);
	// // weak.removeAll(usedVars);
	// }
	// }
	// everAsked.putAll(actualAsked);
	//
	// everUsed.addAll(actualUsed);
	// actualAsked.keySet().removeAll(actualUsed);
	// weakRemain.addAll(actualAsked.keySet());
	//
	// }
	//
	// Set<String> strongRemain = new TreeSet<String>();
	// strongRemain.addAll(everAsked.keySet());
	//
	// strongRemain.removeAll(everUsed);
	//
	// weakRemain.removeAll(strongRemain);
	//
	// String weak = getRedundValues(weakRemain);
	// if (!weak.isEmpty()) {
	// error.append("weak Redundancy : <br>" + weak + "<br>");
	// }
	// String strong = getRedundValues(strongRemain);
	// if (!strong.isEmpty()) {
	// error.append("strong Redundancy : <br>" + strong);
	// }
	//
	// AnomalyManager manager = AnomalyManager.getAnomalyManager();
	//
	// for (String st : strongRemain) {
	// Node node = everAsked.get(st);
	// manager.addAnomaly(node.getFlow(), node, "Strong Redundacy");
	// }
	//
	// for (String st : weakRemain) {
	// Node node = everAsked.get(st);
	// manager.addAnomaly(node.getFlow(), node, "Weak Redundacy");
	// }
	//
	// String errormsg = error.toString();
	// if (!errormsg.isEmpty()) {
	// Strings.maskHTML(errormsg);
	// res = new CITestResult(Type.FAILED, errormsg, config);
	// }
	// }
	//
	// return res;
	// }

	/**
	 * 
	 * @created 08.05.2012
	 * @param allNodes
	 * @param status
	 */
	// private void init(List<Node> allNodes, HashMap<Node, Status> status) {
	// for (Node node : allNodes) {
	// status.put(node, Status.UNUSED);
	// }
	// }

	/**
	 * 
	 * @created 08.05.2012
	 * @param node
	 * @param status
	 * @param weakRemain
	 * @param strongRemain
	 * @param weakRedund
	 */
	// private void visit(Node node, HashMap<Node, Status> status,
	// TreeSet<String> weakRemain, TreeSet<String> strongRemain, TreeSet<String>
	// weakRedund) {
	// String actionUsedValue = null;
	// List<String> var = new LinkedList<String>();
	// status.put(node, Status.USING);
	// if (node.getClass().equals(EndNode.class)) {
	// weakRedund.addAll(weakRemain);
	// }
	// else if (node.getClass().equals(ActionNode.class)) {
	// PSAction action = ((ActionNode) node).getAction();
	// if (action instanceof ActionNextQASet) {
	// var = getAskedValues((ActionNextQASet) action);
	// weakRemain.addAll(var);
	// strongRemain.addAll(var);
	// }
	// else if (action instanceof ActionSetValue) {
	// actionUsedValue = ((ActionSetValue) action).getValue().toString();
	// weakRemain.remove(actionUsedValue);
	// strongRemain.remove(actionUsedValue);
	// }
	// }
	//
	// for (Edge edge : node.getOutgoingEdges()) {
	// List<String> usedVars = getConditionVar(edge);
	// strongRemain.removeAll(usedVars);
	//
	// TreeSet<String> weak = new TreeSet<String>();
	// weak.addAll(weakRemain);
	// weak.removeAll(usedVars);
	// Node nextnode = edge.getEndNode();
	// if (Status.UNUSED == status.get(nextnode)) {
	// visit(nextnode, status, weak, strongRemain, weakRedund);
	// }
	// }
	//
	// status.put(node, Status.UNUSED);
	// }

	/**
	 * 
	 * @created 08.05.2012
	 * @param remains
	 * @return
	 */
	// private String getRedundValues(Set<String> remains) {
	// String result = "";
	// for (String remain : remains) {
	// result += remain + " is redundant <br>";
	// }
	// return result;
	// }

	/**
	 * 
	 * @created 08.05.2012
	 * @param edge
	 * @return
	 */
	// private List<String> getConditionVar(Edge edge) {
	// List<String> result = new LinkedList<String>();
	// for (TerminologyObject ob : edge.getCondition().getTerminalObjects()) {
	// result.add(ob.getName());
	// }
	// return result;
	// }

	/**
	 * 
	 * @created 08.05.2012
	 * @param action
	 * @return
	 */
	// private List<String> getAskedValues(ActionNextQASet action) {
	// List<String> result = new LinkedList<String>();
	//
	// for (QASet set : action.getQASets()) {
	// if (set instanceof QContainer) {
	// QContainer qc = (QContainer) set;
	// List<Question> questions = KnowledgeBaseUtils.getSuccessors(qc,
	// Question.class);
	// for (Question q : questions) {
	// result.add(q.getName());
	// }
	// }
	// else {
	// result.add(set.getName());
	// }
	// }
	// return result;
	// }

	@Override
	protected String test(KnowledgeBase kb) {
		StringBuffer error = new StringBuffer();
		if (kb != null) {
			RedundantValueStrategy strategy = new RedundantValueStrategy(kb);
			PathGenerator generator = new PathGenerator(kb, strategy);
			generator.createPaths();
			HashMap<Node, Path> anomalies = strategy.getAnomalies();
			if (anomalies != null) {
				for (Node node : anomalies.keySet())
					error.append("Weak redundancy at " + node + "<br>");
			}
			TreeMap<String, Node> allAsked = strategy.getAllAsked();
			TreeSet<String> allUsed = strategy.getAllUsed();
			allAsked.keySet().removeAll(allUsed);
			for (String st : allAsked.keySet()) {
				error.append("Strong redundancy at " + allAsked.get(st) + "<br>");
			}
		}
		// HashMap<Node, Status> status = new HashMap<Node, Status>();
		// List<Node> allNodes = new LinkedList<Node>();
		//
		// TreeSet<String> everUsed = new TreeSet<String>();
		// TreeMap<String, Node> everAsked = new TreeMap<String, Node>();
		// TreeSet<String> weakRemain = new TreeSet<String>();
		//
		// StringBuffer error = new StringBuffer();
		// if (null != kb) {
		// List<Flow> flowcharts =
		// kb.getManager().getObjects(Flow.class);
		//
		// for (Flow flow : flowcharts) {
		// allNodes.addAll(flow.getNodes());
		// }
		//
		// init(allNodes, status);
		//
		// WholePathStrategy strategy = new WholePathStrategy(kb);
		// PathGenerator generator = new PathGenerator(kb, strategy);
		// generator.createPaths();
		//
		// // for (Flow flow : flowcharts) {
		// // if (flow.isAutostart()) {
		// // for (StartNode sNode : flow.getStartNodes()) {
		// // visit(sNode, status, weakRemain, strongRemain, weakRedund);
		// // }
		// // }
		// // }
		// for (Path path : generator.getFinalPaths()) {
		// Iterator<DiaFluxElement> it = path.iterator();
		//
		// TreeMap<String, Node> actualAsked = new TreeMap<String, Node>();
		// TreeSet<String> actualUsed = new TreeSet<String>();
		//
		// while (it.hasNext()) {
		// DiaFluxElement el = it.next();
		// if (el instanceof ActionNode) {
		// PSAction action = ((ActionNode) el).getAction();
		// if (action instanceof ActionNextQASet) {
		// List<String> var = getAskedValues((ActionNextQASet) action);
		// // weakRemain.addAll(var);
		// // strongRemain.addAll(var);
		// for (String st : var) {
		// actualAsked.put(st, (Node) el);
		// }
		// }
		// else if (action instanceof ActionSetValue) {
		//
		// String actionUsedValue = ((ActionSetValue)
		// action).getValue().toString();
		// // weakRemain.remove(actionUsedValue);
		// // strongRemain.remove(actionUsedValue);
		// actualUsed.add(actionUsedValue);
		// // TODO value which is set to actualAsked?
		// }
		// }
		// else if (el instanceof Edge) {
		//
		// Edge edge = (Edge) el;
		// List<String> usedVars = getConditionVar(edge);
		// // strongRemain.removeAll(usedVars);
		// actualUsed.addAll(usedVars);
		// // TreeSet<String> weak = new TreeSet<String>();
		// // weak.addAll(weakRemain);
		// // weak.removeAll(usedVars);
		// }
		// }
		// everAsked.putAll(actualAsked);
		//
		// everUsed.addAll(actualUsed);
		// actualAsked.keySet().removeAll(actualUsed);
		// weakRemain.addAll(actualAsked.keySet());
		//
		// }
		//
		// Set<String> strongRemain = new TreeSet<String>();
		// strongRemain.addAll(everAsked.keySet());
		//
		// strongRemain.removeAll(everUsed);
		//
		// weakRemain.removeAll(strongRemain);
		//
		// String weak = getRedundValues(weakRemain);
		// if (!weak.isEmpty()) {
		// error.append("weak Redundancy : <br>" + weak + "<br>");
		// }
		// String strong = getRedundValues(strongRemain);
		// if (!strong.isEmpty()) {
		// error.append("strong Redundancy : <br>" + strong);
		// }
		//
		// AnomalyManager manager = AnomalyManager.getAnomalyManager();
		//
		// for (String st : strongRemain) {
		// Node node = everAsked.get(st);
		// manager.addAnomaly(node.getFlow(), node, "Strong Redundacy");
		// }
		//
		// for (String st : weakRemain) {
		// Node node = everAsked.get(st);
		// manager.addAnomaly(node.getFlow(), node, "Weak Redundacy");
		// }
		// }
		return error.toString();
	}
}
