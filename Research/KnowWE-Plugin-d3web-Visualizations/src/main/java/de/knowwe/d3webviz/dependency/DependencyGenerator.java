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
package de.knowwe.d3webviz.dependency;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.utilities.Pair;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 20.07.2012
 */
public class DependencyGenerator {

	private final KnowledgeBase kb;
	private final Collection<Dependency> dependencies;
	private final boolean showType;
	private static final Collection<DependencyExtractor> finders;
	private final String[] ignores = new String[] {
			"start", "now" };

	static {
		finders = new LinkedList<DependencyExtractor>();
		finders.add(new RuleExtractor());
		finders.add(new DiaFluxNodeExtractor());
	}

	public DependencyGenerator(KnowledgeBase kb, boolean showType) {
		this.kb = kb;
		this.showType = showType;
		this.dependencies = new LinkedList<Dependency>();
	}

	public void createDependencyGraph(StringBuilder bob) {
		addDependencies();

		createOutput(bob);

	}

	private void createOutput(StringBuilder bob) {
		// bob.append("{\"nodes\": [");
		// bob.replace(bob.length() - 1, bob.length(), "");
		// bob.append("],\n");
		bob.append("{");

		if (showType) {
			createLinksWithDependency(bob);
		}
		else {
			createLinksOnly(bob);
		}

		bob.replace(bob.length() - 1, bob.length(), "");
		bob.append("}");

	}

	/**
	 * 
	 * @created 12.11.2012
	 * @param bob
	 */
	private void createLinksWithDependency(StringBuilder bob) {
		bob.append("\"links\": [");
		List<Pair<?, ?>> edges = new LinkedList<Pair<?, ?>>();
		List<Object> indizes = new LinkedList<Object>();
		next:
		for (Dependency dep : dependencies) {

			for (String ignore : ignores) {
				if (dep.getObject().getName().equals(ignore)) {
					continue next;
				}
			}
			int depNode = getIndex(dep, indizes);
			for (TerminologyObject to : dep) {
				Pair<?, ?> edge = new Pair<Object, Object>(to, dep);
				if (!edges.contains(edge)) {
					appendEdge(getIndex(to, indizes), depNode, "", bob);
					edges.add(edge);
				}

			}

			appendEdge(depNode, getIndex(dep.getObject(), indizes), "", bob);
		}

		bob.replace(bob.length() - 1, bob.length(), "");
		bob.append("],\n");
		bob.append("\"nodes\": [");

		for (int i = 0; i < indizes.size(); i++) {
			Object object = indizes.get(i);
			appendNode(object, bob);
			// objects.remove(object);
		}

		bob.replace(bob.length() - 1, bob.length(), "");

		bob.append("]\n");

	}

	/**
	 * 
	 * @created 25.01.2013
	 * @param object
	 * @param indizes
	 * @return
	 */
	private <T extends Object> int getIndex(T object, List<T> indizes) {
		if (!indizes.contains(object)) {
			indizes.add(object);
		}
		return indizes.indexOf(object);
	}

	/**
	 * Creates only the links between dependent TOs, without the type of
	 * Dependency.
	 * 
	 * @param bob
	 */
	protected void createLinksOnly(StringBuilder bob) {
		bob.append("\"links\": [");
		List<TerminologyObject> indizes = new LinkedList<TerminologyObject>();
		List<Pair<?, ?>> edges = new LinkedList<Pair<?, ?>>();
		for (Dependency dep : dependencies) {
			for (TerminologyObject to : dep) {
				Pair<?, ?> edge = new Pair<Object, Object>(dep.getObject(), to);
				if (!edges.contains(edge)) {
					appendEdge(getIndex(to, indizes), getIndex(dep.getObject(), indizes), "", bob);
					edges.add(edge);
				}
			}
		}

		bob.replace(bob.length() - 1, bob.length(), "");
		bob.append("],\n");
		// createNodes()

		bob.append("\"nodes\": [");

		// List<TerminologyObject> objects = new
		// LinkedList<TerminologyObject>();
		// objects.addAll(kb.getManager().getQuestions());
		// objects.addAll(kb.getManager().getSolutions());

		for (int i = 0; i < indizes.size(); i++) {
			Object object = indizes.get(i);
			appendNode(object, bob);
			// objects.remove(object);

		}

		// for (TerminologyObject terminologyObject : objects) {
		// appendNode(terminologyObject, bob);
		// }

		bob.replace(bob.length() - 1, bob.length(), "");

		bob.append("]\n");

	}

	private void appendNode(Object o, StringBuilder bob) {
		bob.append("{\"id\":");
		bob.append("\"");
		String name = null;
		if (o instanceof TerminologyObject) {
			name = ((TerminologyObject) o).getName();
		}
		else if (o instanceof Dependency) {
			name = ((Dependency) o).getVerbalization();
		}
		bob.append(maskJSON(name));
		bob.append("\"");
		bob.append("},");
	}

	private void appendEdge(int from, int to, String options, StringBuilder bob) {

		bob.append("{\"source\":\"");
		bob.append(from);
		bob.append("\", \"target\":\"");
		bob.append(to);
		bob.append("\"");
		if (!options.isEmpty()) {
			bob.append(",");
			bob.append(options);
		}
		bob.append("},");
	}

	private String maskJSON(String s) {
		return s.replaceAll("\"", "\\\"");
	}

	private void addDependencies() {

		for (DependencyExtractor finder : finders) {
			this.dependencies.addAll(finder.getDependencies(kb));
			
		}

	}

}