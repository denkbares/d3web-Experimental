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
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaFlux.inference.DiaFluxUtils;


/**
 * A strategy to create all (deep) paths of a DiaFlux model. Traverses also Subflows. 
 * @author Reinhard Hatko
 * @created 26.03.2012
 */
public class AllPathsStrategy implements DFSStrategy {

	private final KnowledgeBase kb;
	private final Collection<Path> usedStartPaths;
	long counter = 0;

	public AllPathsStrategy(KnowledgeBase kb) {
		this.kb = kb;
		this.usedStartPaths = new HashSet<Path>();
	}

	@Override
	public boolean followEdge(Edge edge, Path path) {
		return true;
		// return !path.contains(edge.getEndNode());
	}

	@Override
	public List<Path> getInitialStartPaths() {
		LinkedList<Path> paths = new LinkedList<Path>();

		for (Node node : DiaFluxUtils.getAutostartNodes(kb)) {
			paths.add(new Path(node));
		}
		usedStartPaths.addAll(paths);
		return paths;
	}


	@Override
	public boolean offer(DiaFluxElement el, Path path) {
		boolean finished = false;
		if (path.contains(el) || (path.getLength() > 1 && el instanceof SnapshotNode)) {
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
		Path startPath = path.newPath();
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
		counter++;
		if (counter % 10000 == 0) {
			System.out.println(counter);
		}

	}

	@Override
	public boolean enterSubflow(ComposedNode node, Path path) {
		return true;
	}

}
