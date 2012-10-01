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
package de.knowwe.diaflux.coverage;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaflux.coverage.AllPathsShallowStrategy;
import de.d3web.diaflux.coverage.DFSStrategy;
import de.d3web.diaflux.coverage.Path;


/**
 * 
 * @author Reinhard Hatko
 * @created 22.08.2012
 */
public class PathsFromNodeStrategy implements DFSStrategy {

	private final DFSStrategy delegate;
	private final Node node;
	private final Collection<Path> paths;

	public PathsFromNodeStrategy(Node node) {
		delegate = new AllPathsShallowStrategy(false, node.getFlow().getKnowledgeBase());
		this.node = node;
		this.paths = new LinkedList<Path>();
	}

	public Node getNode() {
		return node;
	}

	public List<Path> getInitialStartPaths() {
		return Arrays.asList(new Path(this.node));
	}

	public Collection<Path> getPaths() {
		return paths;
	}

	public void found(Path path) {
		delegate.found(path);
		paths.add(path);
	}

	@Override
	public Path createStartPath(Path path) {
		return delegate.createStartPath(path);
	}

	public boolean followEdge(Edge edge, Path path) {
		return delegate.followEdge(edge, path);
	}

	public boolean offer(DiaFluxElement el, Path path) {
		return delegate.offer(el, path);
	}


	public boolean enterSubflow(ComposedNode node, Path path) {
		return delegate.enterSubflow(node, path);
	}

	public void finished(Path path) {
		delegate.finished(path);
	}

}
