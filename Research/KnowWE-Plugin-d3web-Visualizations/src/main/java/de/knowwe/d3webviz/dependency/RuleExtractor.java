/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.d3webviz.dependency;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;

/**
 * 
 * @author Reinhard Hatko
 * @created 12.11.2012
 */
public class RuleExtractor implements DependencyExtractor {


	@Override
	public Collection<Dependency> getDependencies(KnowledgeBase kb) {

		Collection<RuleSet> slices = kb.getAllKnowledgeSlicesFor(PSMethodAbstraction.FORWARD);

		Set<Dependency> result = new HashSet<Dependency>();
		
		for (RuleSet rules : slices) {

			if (rules == null) continue;

			for (Rule rule : rules.getRules()) {
				Dependency dependency = createDependency(rule);
				if (dependency != null)
				result.add(dependency);
			}

		}


		return result;
	}

	/**
	 * 
	 * @created 23.01.2013
	 * @param rule
	 * @return
	 */
	public Dependency createDependency(Rule rule) {
		PSAction action = rule.getAction();
		List<TerminologyObject> objects = new LinkedList<TerminologyObject>();

		for (TerminologyObject terminologyObject : action.getForwardObjects()) {
			objects.add(terminologyObject);
		}


		Dependency dependency = new Dependency(action.getBackwardObjects().get(0), objects,
				PSMethodAbstraction.class, rule.getClass().getName() + rule.hashCode());
		return dependency;
	}

}
