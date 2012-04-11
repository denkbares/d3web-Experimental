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
package de.d3web.diaflux.coverage;

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;


/**
 * 
 * @author Reinhard Hatko
 * @created 26.03.2012
 */
public class AllPathsEvaluator implements EdgeEvaluator {

	private final KnowledgeBase kb;

	public AllPathsEvaluator(KnowledgeBase kb) {
		this.kb = kb;
	}

	@Override
	public boolean followEdge(Edge edge, Path path) {
		return !path.contains(edge.getEndNode());
	}

	@Override
	public List<Path> getStartPaths() {
		LinkedList<Path> paths = new LinkedList<Path>();

		for (Node node : DiaFluxUtils.getAutostartNodes(kb)) {
			paths.add(new Path(node));
		}
		return paths;
	}

	@Override
	public boolean stopPath(Path path) {
		return path.getTail() instanceof EndNode;
	}

}
