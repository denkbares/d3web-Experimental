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

import de.d3web.core.inference.PSAction;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.diaFlux.flow.ActionNode;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.NodeList;
import de.d3web.diaFlux.inference.FluxSolver;


/**
 * 
 * @author Reinhard Hatko
 * @created 12.11.2012
 */
class DiaFluxNodeExtractor implements DependencyExtractor {

	@Override
	public Collection<Dependency> getDependencies(KnowledgeBase kb) {
		Collection<NodeList> slices = kb.getAllKnowledgeSlicesFor(FluxSolver.DEPENDANT_NODES);
		Set<Dependency> result = new HashSet<Dependency>();

		for (NodeList nodes : slices) {

			if (nodes == null || nodes.getNodes().isEmpty()) continue;


			for (Node node : nodes.getNodes()) {
				if (node instanceof ActionNode) {
					PSAction action = ((ActionNode) node).getAction();

					List<TerminologyObject> objects = new LinkedList<TerminologyObject>();

					for (TerminologyObject terminologyObject : action.getForwardObjects()) {
						objects.add(terminologyObject);
					}


					result.add(new Dependency(action.getBackwardObjects().get(0),
							objects, FluxSolver.class, node.getFlow().getName() + "" + node.getID()));
				}
			}
		}


		return result;

	}

}
