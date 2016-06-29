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
package de.d3web.dependency;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import de.d3web.core.inference.ActionAddValueFact;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethodRulebased;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * 
 * @author Reinhard Hatko
 * @created 12.11.2012
 */
public class RuleExtractor implements DependencyExtractor {

	private final Class<? extends PSMethodRulebased> psm;

	public RuleExtractor(Class<? extends PSMethodRulebased> psm) {
		this.psm = psm;
	}

	@Override
	public Collection<Dependency> getDependencies(KnowledgeBase kb) {

		try {
			Set<Dependency> result = new HashSet<>();

			result.addAll(getDependencies(kb.getAllKnowledgeSlicesFor(PSMethodRulebased.getBackwardKind(psm))));
			result.addAll(getDependencies(kb.getAllKnowledgeSlicesFor(PSMethodRulebased.getForwardKind(psm))));
			return result;
		}
		catch (NoSuchElementException e) {
			return Collections.emptyList();
		}


	}

	/**
	 * 
	 * @created 07.06.2013
	 * @param slices
	 * @return
	 */
	private Set<Dependency> getDependencies(Collection<RuleSet> slices) {
		Set<Dependency> result = new HashSet<>();
		
		for (RuleSet rules : slices) {

			if (rules == null) continue;

			for (Rule rule : rules.getRules()) {
				PSAction action = rule.getAction();
				if (action instanceof ActionAddValueFact) {
					result.add(DependencyFinder.createDependency((ActionAddValueFact) action, psm));
				}
				result.add(DependencyFinder.createDependency(rule.getCondition(), psm));
			}

		}
		return result;
	}


}
