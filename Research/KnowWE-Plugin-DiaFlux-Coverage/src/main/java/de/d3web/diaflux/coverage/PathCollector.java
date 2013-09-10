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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;

/**
 * 
 * @author Reinhard Hatko
 * @created 06.08.2012
 */
public class PathCollector implements DFSStrategy {

	private final DFSStrategy delegate;
	private final Collection<Path> paths;

	public PathCollector(DFSStrategy delegate) {
		this.delegate = delegate;
		this.paths = new HashSet<Path>();
	}

	@Override
	public void found(Path path) {
		paths.add(path);
		delegate.found(path);
	}

	public Collection<Path> getPaths() {
		return paths;
	}

	public List<Path> getInitialStartPaths() {
		return delegate.getInitialStartPaths();
	}

	public boolean followEdge(Edge edge, Path path) {
		return delegate.followEdge(edge, path);
	}

	public boolean offer(DiaFluxElement el, Path path) {
		return delegate.offer(el, path);
	}

	public Path createStartPath(Path path) {
		return delegate.createStartPath(path);
	}

	public boolean enterSubflow(ComposedNode node, Path path) {
		return delegate.enterSubflow(node, path);
	}

	@Override
	public void finished(Path path) {
		delegate.finished(path);
	}

}
