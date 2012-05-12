/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaflux.anomalystrategies;

import java.util.List;

import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaflux.coverage.DFSStrategy;
import de.d3web.diaflux.coverage.Path;

/**
 * 
 * @author Roland
 * @created 09.05.2012
 */
public class FrameStackStrategy implements DFSStrategy {

	@Override
	public List<Path> getInitialStartPaths() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean followEdge(Edge edge, Path path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offer(DiaFluxElement el, Path path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void found(Path path) {
		// TODO Auto-generated method stub

	}

	@Override
	public Path createStartPath(Path path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean enterSubflow(ComposedNode node, Path path) {
		// TODO Auto-generated method stub
		return false;
	}

}
