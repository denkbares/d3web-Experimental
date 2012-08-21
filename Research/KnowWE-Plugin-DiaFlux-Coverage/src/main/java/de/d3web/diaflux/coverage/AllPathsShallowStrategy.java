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
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;


/**
 * A strategy to create all paths of each DiaFlux model. Subflows are not
 * traversed.
 * 
 * @author Reinhard Hatko
 * @created 26.03.2012
 */
public class AllPathsShallowStrategy implements DFSStrategy {

	protected final KnowledgeBase kb;
	protected final Collection<Path> usedStartPaths;
	protected final boolean stopOnSnapshot;

	public AllPathsShallowStrategy(boolean stopOnSnapshot, KnowledgeBase kb) {
		this.kb = kb;
		this.stopOnSnapshot = stopOnSnapshot;
		this.usedStartPaths = new HashSet<Path>();
	}

	@Override
	public boolean followEdge(Edge edge, Path path) {
		return true;
	}

	@Override
	public List<Path> getInitialStartPaths() {
		LinkedList<Path> paths = new LinkedList<Path>();

		for (Flow flow : DiaFluxUtils.getFlowSet(kb)) {
			for (Node node : flow.getStartNodes()) {
				paths.add(new Path(node));
			}
		}
		usedStartPaths.addAll(paths);
		return paths;
	}


	@Override
	public boolean offer(DiaFluxElement el, Path path) {
		boolean finished = false;
		if (path.contains(el)) {
			finished = true;
		}
		if (this.stopOnSnapshot && el instanceof SnapshotNode) {
			finished = true;
		}
		path.append(el);
		return !finished;
	}

	@Override
	public Path createStartPath(Path path) {

		// TODO consider callStack??
		if (path.getHead() == path.getTail()) {
			// circular path
			return null;
		}
		Path startPath = path.continueFromTail();

		if (usedStartPaths.contains(startPath)) {
			return null;
		}
		else {
			usedStartPaths.add(startPath);
			return startPath;
		}
	}

	@Override
	public void found(Path path) {
	}

	@Override
	public void finished(Path newPath) {
	}

	@Override
	public boolean enterSubflow(ComposedNode node, Path path) {
		return false;
	}

}
