/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.d3webviz.diafluxCoupling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.ActionAddValueFact;
import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.d3webviz.AbstractD3webVizAction;


/**
 * 
 * @author Reinhard Hatko
 * @created 17.05.2013
 */
public class DiaFluxCouplingMatrixAction extends AbstractD3webVizAction {

	public enum Relation {
		child, parent, sibling
	}

	@Override
	protected String createOutput(KnowledgeBase kb, Section<?> section, UserActionContext context) {
		if (!DiaFluxUtils.hasFlows(kb)) return "{}";

		Map<Flow, Collection<TerminologyObject>> objects = new HashMap<Flow, Collection<TerminologyObject>>();

		FlowSet flowSet = DiaFluxUtils.getFlowSet(kb);

		for (Flow flow : flowSet) {
			objects.put(flow, getObjects(flow, kb));
		}

		StringBuilder bob = new StringBuilder();
		bob.append("{");
		List<Flow> flowIndex = new ArrayList<Flow>(objects.keySet());
		createNodes(flowIndex, bob);
		bob.append(",");
		createLinks(flowIndex, objects, bob);

		bob.append("}");
		return bob.toString();
	}

	/**
	 * 
	 * @created 17.05.2013
	 * @param flowIndex
	 * @param objects
	 * @param bob
	 */
	private void createLinks(List<Flow> flowIndex, Map<Flow, Collection<TerminologyObject>> objects, StringBuilder bob) {
		bob.append("\"links\":[");

		int size = flowIndex.size();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Collection<TerminologyObject> first = objects.get(flowIndex.get(i));
				Collection<TerminologyObject> second = objects.get(flowIndex.get(j));
				List<TerminologyObject> intersection = getIntersection(first, second);

				bob.append("{");
				bob.append("\"source\":");
				bob.append(i);
				bob.append(",");
				bob.append("\"target\":");
				bob.append(j);
				bob.append(",");
				bob.append("\"value\":");
				if (first.size() == 0) {
					bob.append(0);
				}
				else {
					bob.append(intersection.size() / (double) first.size());
				}

				bob.append(",");
				bob.append("\"objects\":");
				bob.append("[");
				for (int k = 0; k < intersection.size(); k++) {
					bob.append("\"" + intersection.get(k) + "\"");

					if (k < intersection.size() - 1) bob.append(",");

				}
				bob.append(",");
				bob.append("\"type\":");
				bob.append(""); // TODO

				bob.append("]");

				bob.append("}");
				if (j < size - 1) bob.append(",");

			}

			if (i < size - 1) bob.append(",");
		}

		bob.append("]");
	}

	/**
	 * Returns the TermObjects that are contained both collection
	 * 
	 * @created 17.05.2013
	 * @param collection
	 * @param collection2
	 * @return
	 */
	private static List<TerminologyObject> getIntersection(Collection<TerminologyObject> first, Collection<TerminologyObject> second) {
		List<TerminologyObject> temp = new ArrayList<TerminologyObject>(first);
		temp.retainAll(second);

		return temp;
	}

	private static void createNodes(List<Flow> flowIndex, StringBuilder bob) {
		bob.append("\"nodes\":[");

		int size = flowIndex.size();
		for (int i = 0; i < size; i++) {

			Flow flow = flowIndex.get(i);
			bob.append("{");
			bob.append("\"name\":");
			bob.append("\"" + flow.getName() + "\",");
			bob.append("\"group\":");
			bob.append(0);
			bob.append("}");

			if (i < size - 1) {
				bob.append(",");
			}

		}

		bob.append("]");
	}

	private static Collection<TerminologyObject> getObjects(Flow flow, KnowledgeBase kb) {
		Collection<TerminologyObject> result = new HashSet<TerminologyObject>();

		for (Node node : flow.getNodes()) {
			result.addAll(node.getHookedObjects());
			if (node instanceof ActionNode) {
				PSAction action = ((ActionNode) node).getAction();
				if (action instanceof ActionAddValueFact) {
					result.addAll(action.getBackwardObjects());
				}
			}
		}

		// ignore now and start
		result.remove(kb.getManager().searchQuestion("now"));
		result.remove(kb.getManager().searchQuestion("start"));

		return result;
	}
	
	/**
	 * Returns the relation of the first flow to the second flow:
	 * 
	 * child, if the first flow is called by the second flow, or one of its
	 * successors parent, if the first flow is called by the second one, of one
	 * of its successors sibling, if they are not in a child or parent
	 * relationship.
	 * 
	 * @created 22.05.2013
	 * @param first
	 * @param second
	 * @return s the found relations. If either of the
	 */
	public static Collection<Relation> getRelations(Flow first, Flow second) {
		Collection<Relation> relations = new HashSet<Relation>();

		if (isCalledRecursive(first, second)) relations.add(Relation.child);

		if (isCalledRecursive(second, first)) relations.add(Relation.parent);

		return relations;
	}

	/**
	 * Returns a list of call hierarchies, ie. a list of ComposedNodes, that
	 * lead to the execution of the supplied flow.
	 * 
	 * @created 22.05.2013
	 * @param flow
	 * @return
	 */
	public static Collection<Collection<ComposedNode>> getCallHierarchies(Flow flow) {
		Collection<Collection<ComposedNode>> result = new HashSet<Collection<ComposedNode>>();

		Collection<ComposedNode> currentHierarchy = new LinkedList<ComposedNode>();
		getCallHierarchies(flow, currentHierarchy, result);

		return result;
	}

	/**
	 * 
	 * @created 22.05.2013
	 * @param flow
	 * @param currentHierarchy
	 * @param result
	 */
	private static void getCallHierarchies(Flow flow, Collection<ComposedNode> currentHierarchy, Collection<Collection<ComposedNode>> result) {
		List<ComposedNode> callingNodes = DiaFluxUtils.getCallingNodes(flow.getKnowledgeBase(),
				flow);
		if (callingNodes.isEmpty()) {
			result.add(currentHierarchy);
		}
		else {

			for (ComposedNode compNode : callingNodes) {
				// no endless looping
				if (currentHierarchy.contains(compNode)) {
					result.add(currentHierarchy);
					continue;
				}

				ArrayList<ComposedNode> copy = new ArrayList<ComposedNode>(currentHierarchy);
				copy.add(compNode);
				getCallHierarchies(compNode.getFlow(), copy, result);
			}
		}

	}

	private static boolean isCalledRecursive(Flow callee, Flow caller) {
		if (isCalled(callee, caller)) return true;

		Collection<ComposedNode> calls = caller.getNodesOfClass(ComposedNode.class);

		for (ComposedNode composedNode : calls) {
			return isCalledRecursive(callee, composedNode.getFlow());
		}
		return false;

	}

	private static boolean isCalled(Flow callee, Flow caller) {
		Collection<ComposedNode> calls = caller.getNodesOfClass(ComposedNode.class);

		for (ComposedNode composedNode : calls) {
			if (DiaFluxUtils.getCalledFlow(composedNode.getFlow().getKnowledgeBase(), composedNode) == callee) return true;
		}
		return false;

	}



}
