/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.rdfs.vis;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Contains a sub-set of a graph.
 * 
 * @author jochenreutelshofer
 * @created 27.05.2013
 */
public class SubGraphData {

	private final Set<ConceptNode> concepts;
	private final Set<Edge> edges;

	/**
 * 
 */
	public SubGraphData() {
		concepts = new LinkedHashSet<ConceptNode>();
		edges = new LinkedHashSet<Edge>();
	}

	public Set<ConceptNode> getConceptDeclaration() {
		return concepts;
	}

	public Set<Edge> getEdges() {
		return edges;
	}

	public void addEdge(Edge e) {
		edges.add(e);
	}

	public void addConcept(ConceptNode n) {
		if (concepts.contains(n)) {
			// due to equals not regarding outer-flag
			concepts.remove(n);
			concepts.add(n);
		}
	}

}
