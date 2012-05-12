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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.SnapshotNode;
import de.d3web.diaflux.coverage.Path;

/**
 * 
 * @author Roland Jerg
 * @created 09.05.2012
 */
public class SnapshotStrategy extends AbstractAnomalyStrategy {

	/**
	 * @param kb
	 */
	public SnapshotStrategy(KnowledgeBase kb) {
		super(kb);
	}

	@Override
	public List<Path> getInitialStartPaths() {
		List<Path> result = super.getInitialStartPaths();
		return result;
	}

	@Override
	public boolean followEdge(Edge edge, Path path) {
		return true;
	}

	@Override
	public boolean offer(DiaFluxElement el, Path path) {
		boolean finished = false;
		if ((path.getLength() > 1 && el instanceof SnapshotNode)) {
			finished = true;
		}
		else if (path.contains(el)) {
			anomalies.put((Node) el, path);
			finished = true;
		}
		super.offer(el, path);
		return !finished;
	}
}
