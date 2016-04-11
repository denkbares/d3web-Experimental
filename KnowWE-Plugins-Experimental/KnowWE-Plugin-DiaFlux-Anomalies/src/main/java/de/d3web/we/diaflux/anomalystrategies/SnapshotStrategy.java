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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaflux.coverage.AllPathsStrategy;
import de.d3web.diaflux.coverage.DFSStrategy;
import de.d3web.diaflux.coverage.Path;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 20.08.2012
 */
public class SnapshotStrategy implements DFSStrategy {

	private final DFSStrategy delegate;
	private final Collection<Path> anomalies;

	public SnapshotStrategy(KnowledgeBase kb) {
		this.delegate = new AllPathsStrategy(true, kb);
		this.anomalies = new LinkedList<Path>();
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

	public void found(Path path) {
		delegate.found(path);
		// Circular path without Snapshot
		if (path.getHead() == path.getTail()) {
			if (!(path.getHead() instanceof SnapshotNode)) {
				anomalies.add(path);
			}
		}
	}

	public Path createStartPath(Path path) {
		return delegate.createStartPath(path);
	}

	public boolean enterSubflow(ComposedNode node, Path path) {
		return delegate.enterSubflow(node, path);
	}

	public void finished(Path path) {
		delegate.finished(path);
	}

	public Collection<Path> getAnomalies() {
		return anomalies;
	}

}
