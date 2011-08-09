/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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

import java.util.HashMap;
import java.util.Map;

import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Node;


/**
 * This class stores the coverage fo DiaFlux models for a specific session.
 * 
 * @author Reinhard Hatko
 * @created 09.08.2011
 */
public class CoverageSessionObject implements SessionObject {

	private final Map<Edge, Integer> edges;
	private final Map<Node, Integer> nodes;

	public CoverageSessionObject() {
		edges = new HashMap<Edge, Integer>();
		nodes = new HashMap<Node, Integer>();
	}

	public void addTracedNode(Node node) {
		if (!nodes.containsKey(node)) {
			nodes.put(node, 0);
		}

		nodes.put(node, nodes.get(node) + 1);
	}

	public void addTracedEdge(Edge edge) {
		if (!edges.containsKey(edge)) {
			edges.put(edge, 0);
		}

		edges.put(edge, edges.get(edge) + 1);
	}

	public int getTraceCount(Node node) {
		Integer count = nodes.get(node);
		if (count == null) {
			return 0;
		}
		else return count;

	}

	public int getTraceCount(Edge edge) {
		Integer count = edges.get(edge);
		if (count == null) {
			return 0;
		}
		else return count;

	}

}
