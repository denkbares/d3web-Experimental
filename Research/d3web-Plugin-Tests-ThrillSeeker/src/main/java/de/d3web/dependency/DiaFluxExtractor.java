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
import java.util.Set;

import de.d3web.core.inference.ActionAddValueFact;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;


/**
 * 
 * @author Reinhard Hatko
 * @created 12.11.2012
 */
public class DiaFluxExtractor implements DependencyExtractor {

	@Override
	public Collection<Dependency> getDependencies(KnowledgeBase kb) {
		if (!DiaFluxUtils.hasFlows(kb)) return Collections.emptyList();

		Set<Dependency> result = new HashSet<Dependency>();

		for (Flow flow : DiaFluxUtils.getFlowSet(kb)) {

			for (Node node : flow.getNodes()) {
				if (node instanceof ActionNode) {
					PSAction action = ((ActionNode) node).getAction();

					if (action instanceof ActionAddValueFact) {
						String verbalization = node.getFlow().getName() + "" + node.getID();
						result.add(DependencyFinder.createDependency((ActionAddValueFact) action,
								FluxSolver.class));
					}

				}
			}

			for (Edge edge : flow.getEdges()) {
				Condition condition = edge.getCondition();
				if (condition instanceof ConditionTrue) continue;

				result.add(DependencyFinder.createDependency(condition, FluxSolver.class));

			}

		}

		return result;

	}

}