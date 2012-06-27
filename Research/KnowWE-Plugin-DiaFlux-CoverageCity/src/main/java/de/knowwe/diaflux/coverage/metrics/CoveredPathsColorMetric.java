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

import de.d3web.diaFlux.flow.Node;
import de.d3web.diaflux.coverage.CoverageResult;
import de.d3web.diaflux.coverage.PathIndex;


/**
 * 
 * @author Reinhard Hatko
 * @created 20.05.2012
 */
public class CoveredPathsColorMetric extends AbstractColorMetric<Node> {
	
	private final CoverageResult result;
	
	public CoveredPathsColorMetric(CoverageResult result) {
		this.result = result;
	}
	
	@Override
	protected float getColorValue(Node object) {
		PathIndex index = result.getPathIndex();
		double allPathsCount = index.getAllPaths(object).size();
		double coveredPathsCount = index.getCoveredPaths(object).size();

		float f = (float) (coveredPathsCount / allPathsCount);
		return Math.min(f, 1);
	}



}
