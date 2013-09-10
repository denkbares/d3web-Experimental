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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.DiaFluxElement;


/**
 * 
 * @author Reinhard Hatko
 * @created 10.04.2012
 */
public class PathIndex {

	private final KnowledgeBase kb;
	private Collection<Path> allPaths;
	private final Map<Path, Integer> coveredPaths;
	private final Map<DiaFluxElement, Collection<Path>> coveredPathsPerElement;
	private final Map<DiaFluxElement, Collection<Path>> allPathsPerElement;

	// private final Map<DiaFluxElement, Double> pathCoveragePerelement;

	public PathIndex(Map<Path, Integer> paths, KnowledgeBase kb) {
		this.coveredPaths = paths;
		this.kb = kb;
		this.coveredPathsPerElement = new HashMap<DiaFluxElement, Collection<Path>>(300);
		this.allPathsPerElement = new HashMap<DiaFluxElement, Collection<Path>>(300);
		// this.pathCoveragePerelement = new HashMap<DiaFluxElement,
		// Double>(300);
		prepare();
	}

	/**
	 * 
	 * @created 10.04.2012
	 */
	private void prepare() {

		PathCollector strategy = new PathCollector(
				new AllPathsShallowStrategy(true, kb));
		new PathGenerator(kb, strategy).createPaths();
		allPaths = strategy.getPaths();

		indexizePaths(coveredPaths.keySet(), coveredPathsPerElement);
		indexizePaths(allPaths, allPathsPerElement);


	}

	private void indexizePaths(Collection<Path> paths, Map<DiaFluxElement, Collection<Path>> pathMap) {
		for (Path path : paths) {
			for (DiaFluxElement element : path) {
				Collection<Path> collection = pathMap.get(element);
				if (collection == null) {
					collection = new HashSet<Path>();
					pathMap.put(element, collection);
				}
				collection.add(path);
			}
		}
	}

	public Collection<Path> getAllPaths(DiaFluxElement element) {
		return getList(element, allPathsPerElement);
	}

	public Collection<Path> getCoveredPaths(DiaFluxElement element) {
		return getList(element, coveredPathsPerElement);
	}


	private Collection<Path> getList(DiaFluxElement element, Map<DiaFluxElement, Collection<Path>> pathsPerElement) {
		Collection<Path> paths = pathsPerElement.get(element);
		if (paths == null) {
			return Collections.emptyList();
		}
		else {
			return paths;
		}
	}

}
