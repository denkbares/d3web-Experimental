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
package de.knowwe.diaflux.coverage.metrics;

import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaflux.coverage.CoverageResult;


/**
 * 
 * @author Reinhard Hatko
 * @created 08.02.2012
 */
public class CoveredOutgoingEdgesMetric implements Metric<Node, Double> {

	private final CoverageResult result;

	public CoveredOutgoingEdgesMetric(CoverageResult result) {
		this.result = result;
	}

	public Double getValue(Node object) {

		if (object instanceof EndNode) return 2 * 1d;

		double count = 0;
		for (Edge edge : object.getOutgoingEdges()) {
			if (result.getTraceCount(edge) > 0) count++;
		}

		return Math.max(2 * count/* * count */, 0.5);
	}

}
