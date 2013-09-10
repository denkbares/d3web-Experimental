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
package de.d3web.dependency;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.ActionAddValueFact;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.dependency.Dependency.Type;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * 
 * @author Reinhard Hatko
 * @created 07.06.2013
 */
public class DependencyFinder {

	private static final Collection<DependencyExtractor> finders;

	static {
		finders = new LinkedList<DependencyExtractor>();
		finders.add(new RuleExtractor(PSMethodAbstraction.class));
		finders.add(new RuleExtractor(PSMethodHeuristic.class));
		finders.add(new DiaFluxExtractor());
	}

	public static Collection<Dependency> getDependencies(KnowledgeBase kb) {
		Collection<Dependency> dependencies = new LinkedList<Dependency>();

		for (DependencyExtractor finder : finders) {
			dependencies.addAll(finder.getDependencies(kb));
		}

		return dependencies;
	}

	public static Dependency createDependency(Condition condition, Class<? extends PSMethod> psm) {
		return createDependency(condition, null, psm);

	}

	public static Dependency createDependency(Condition condition, TerminologyObject depObject, Class<? extends PSMethod> psm) {
		return new Dependency(depObject, condition.getTerminalObjects(), Type.Precondition, psm, "");
	}

	public static Dependency createDependency(ActionAddValueFact action, Class<? extends PSMethod> psm) {
		return new Dependency(action.getBackwardObjects().get(0), action.getForwardObjects(),
				Type.Derivation, psm, "");
	}

	/**
	 * 
	 * @created 07.06.2013
	 * @param dependencies
	 * @return
	 */
	public static Map<TerminologyObject, Collection<Dependency>> getForwardDependencies(KnowledgeBase kb) {
		Collection<Dependency> dependencies = getDependencies(kb);
		Map<TerminologyObject, Collection<Dependency>> result = new HashMap<TerminologyObject, Collection<Dependency>>();
		
		for (Dependency dependency : dependencies) {
			Collection<Dependency> deps = result.get(dependency.getObject());
			if (deps == null) {
				deps = new LinkedList<Dependency>();
				result.put(dependency.getObject(), deps);
			}
	
			deps.add(dependency);
	
		}
		
		return result;
	}

	/**
	 * 
	 * @created 07.06.2013
	 * @param dependencies
	 * @return
	 */
	public static Map<TerminologyObject, Collection<Dependency>> getBackwardDependencies(KnowledgeBase kb) {
		Collection<Dependency> dependencies = getDependencies(kb);
		Map<TerminologyObject, Collection<Dependency>> result = new HashMap<TerminologyObject, Collection<Dependency>>();

		for (Dependency dependency : dependencies) {
			for (TerminologyObject to : dependency) {
				Collection<Dependency> deps = result.get(to);
				if (deps == null) {
					deps = new LinkedList<Dependency>();
					result.put(to, deps);
				}

				deps.add(dependency);

			}

		}

		return result;
	}

}
