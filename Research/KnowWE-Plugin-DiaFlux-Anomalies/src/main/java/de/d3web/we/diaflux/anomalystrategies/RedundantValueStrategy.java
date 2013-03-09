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
package de.d3web.we.diaflux.anomalystrategies;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import de.d3web.abstraction.ActionSetQuestion;
import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaflux.coverage.Path;
import de.d3web.indication.ActionNextQASet;

/**
 * 
 * @author Roland Jerg
 * @created 11.05.2012
 */
public class RedundantValueStrategy extends AbstractAnomalyStrategy {

	TreeSet<String> everUsed = new TreeSet<String>();
	TreeMap<String, Node> everAsked = new TreeMap<String, Node>();

	// TreeSet<String> weakRemain = new TreeSet<String>();

	// TreeMap<String, Node> actualAsked = new TreeMap<String, Node>();
	// HashMap<Path, TreeMap<String, Node>> actualAsked = new HashMap<Path,
	// TreeMap<String, Node>>();
	// TreeSet<String> actualUsed = new TreeSet<String>();
	// HashMap<Path, List<String>> actualUsed = new HashMap<Path,
	// List<String>>();

	/**
	 * @param kb
	 */
	public RedundantValueStrategy(KnowledgeBase kb) {
		super(kb);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Path> getInitialStartPaths() {
		List<Path> result = super.getInitialStartPaths();
		// for (Path path : result) {
		// actualAsked.put(path, new TreeMap<String, Node>());
		// actualUsed.put(path, new LinkedList<String>());
		// }
		return result;
	}

	@Override
	public boolean followEdge(Edge edge, Path path) {
		return true;
		// return !path.contains(edge.getEndNode());
	}

	@Override
	public boolean offer(DiaFluxElement el, Path path) {

		// if (actualAsked.get(path) == null) {
		// actualAsked.put(path, new TreeMap<String, Node>());
		// }
		// if (actualUsed.get(path) == null) {
		// actualUsed.put(path, new LinkedList<String>());
		// }
		boolean finished = false;
		if (path.contains(el)) {
			finished = true;
		}
		// TODO append private?
		super.offer(el, path);
		if (el instanceof ActionNode) {
			PSAction action = ((ActionNode) el).getAction();
			if (action instanceof ActionNextQASet) {
				// List<String> var = getAskedValues((ActionNextQASet) action);
				// for (String st : var) {
				// TreeMap<String, Node> nodes = actualAsked.get(path);
				// nodes.put(st, (Node) el);
				// }
			}
			else if (action instanceof ActionSetQuestion) {

				// String actionUsedValue = ((ActionSetValue)
				// action).getValue().toString();
				// weakRemain.remove(actionUsedValue);
				// strongRemain.remove(actionUsedValue);
				// List<String> pathUsed = actualUsed.get(path);
				// actualUsed.add(actionUsedValue);
				// pathUsed.add(actionUsedValue);
				// TODO value which is set to actualAsked?
			}

			else if (el instanceof Edge) {

				// Edge edge = (Edge) el;
				// List<String> usedVars = getConditionVar(edge);
				// strongRemain.removeAll(usedVars);
				// List<String> pathUsed = actualUsed.get(path);
				// pathUsed.addAll(usedVars);
				// actualUsed.addAll(usedVars);
				// TreeSet<String> weak = new TreeSet<String>();
				// weak.addAll(weakRemain);
				// weak.removeAll(usedVars);
			}
		}

		// TreeMap<String, Node> nodes = actualAsked.get(path);
		// System.out.println(nodes);
		// everAsked.putAll(nodes);
		// List<String> pathUsed = actualUsed.get(path);
		// everUsed.addAll(pathUsed);
		// nodes.keySet().removeAll(pathUsed);
		// weakRemain.addAll(actualAsked.keySet());
		return !finished;

	}

	@Override
	public void found(Path path) {
		// weakRemain.addAll(actualAsked.keySet());
		// TreeMap<String, Node> nodes = actualAsked.get(path);
		// for (String st : nodes.keySet()) {
		// anomalies.put(nodes.get(st), path);
		//
		// }
		counter++;
		if (counter % 10000 == 0) {
			System.out.println(counter);
		}
	}

	@SuppressWarnings("unused")
	private List<String> getConditionVar(Edge edge) {
		List<String> result = new LinkedList<String>();
		for (TerminologyObject ob : edge.getCondition().getTerminalObjects()) {
			result.add(ob.getName());
		}
		return result;
	}

	@SuppressWarnings("unused")
	private List<String> getAskedValues(ActionNextQASet action) {
		List<String> result = new LinkedList<String>();

		for (QASet set : action.getQASets()) {
			if (set instanceof QContainer) {
				QContainer qc = (QContainer) set;
				List<Question> questions = KnowledgeBaseUtils.getSuccessors(qc, Question.class);
				for (Question q : questions) {
					result.add(q.getName());
				}
			}
			else {
				result.add(set.getName());
			}
		}
		return result;
	}

	public TreeMap<String, Node> getAllAsked() {
		return everAsked;
	}

	public TreeSet<String> getAllUsed() {
		return everUsed;
	}

	@Override
	public Path createStartPath(Path path) {
		Path startPath = super.createStartPath(path);
		// if (startPath != null) {
		// actualAsked.put(startPath, new TreeMap<String, Node>());
		// actualUsed.put(startPath, new LinkedList<String>());
		// }

		return startPath;

		// // TODO consider callStack??
		// if (path.getHead() == path.getTail()) {
		// // circular path
		// return null;
		// }
		// Path startPath = path.newPath();
		// if (usedStartPaths.contains(startPath)) {
		// return null;
		// }
		// else {
		// usedStartPaths.add(startPath);
		// return startPath;
		// }
	}
}
