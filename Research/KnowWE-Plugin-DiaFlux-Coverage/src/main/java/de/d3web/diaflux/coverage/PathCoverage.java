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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Flow;


/**
 * 
 * @author Reinhard Hatko
 * @created 10.04.2012
 */
public class PathCoverage {

	private final Map<Path, Integer> coveredPaths;
	private final Map<DiaFluxElement, Collection<Path>> coveredPathsPerElement;
	private Collection<Path> allPaths;
	private final Map<DiaFluxElement, Collection<Path>> allPathsPerElement;
	private final KnowledgeBase kb;

	public PathCoverage(Map<Path, Integer> paths, KnowledgeBase kb) {
		this.coveredPaths = paths;
		this.kb = kb;
		this.coveredPathsPerElement = new HashMap<DiaFluxElement, Collection<Path>>(300);
		this.allPathsPerElement = new HashMap<DiaFluxElement, Collection<Path>>(300);
		prepare();
	}

	/**
	 * 
	 * @created 10.04.2012
	 */
	private void prepare() {

		prepareAllPaths();

		indexizePaths(coveredPaths.keySet(), coveredPathsPerElement);

		List<DiaFluxElement> els = new ArrayList<DiaFluxElement>(coveredPathsPerElement.keySet());
		Collections.sort(els, new Comparator<DiaFluxElement>() {

			public int compare(DiaFluxElement o1, DiaFluxElement o2) {
				return coveredPathsPerElement.get(o1).size()
						- coveredPathsPerElement.get(o2).size();
			};
		});
		for (DiaFluxElement el : els) {
			double coveredPaths = coveredPathsPerElement.get(el).size();
			Collection<Path> collection = allPathsPerElement.get(el);
			if (collection == null) {
				continue;
			}
			double allPaths = collection.size();
			System.out.println(el + " : " + coveredPaths + " Ratio: " + coveredPaths / allPaths);
		}

	}

	/**
	 * 
	 * @created 10.04.2012
	 * @param coverePaths2
	 * @param coveredPathsPerElement2
	 */
	private void indexizePaths(Collection<Path> paths, Map<DiaFluxElement, Collection<Path>> pathMap) {
		for (Path path : paths) {
			for (DiaFluxElement element : path) {
				put(element, path, pathMap);
			}

		}
	}

	/**
	 * 
	 * @created 10.04.2012
	 */
	public void prepareAllPaths() {
		AllPathsStrategyShallow strategy = new AllPathsStrategyShallow(kb);
		new PathGenerator(kb, strategy).createPaths();
		// allPaths = strategy. TODO
		int totalPathCount = allPaths.size();

		Map<Flow, Integer> counts = new HashMap<Flow, Integer>();

		for (Path path : allPaths) {
			Flow key = path.getHead().getFlow();
			Integer integer = counts.get(key);
			if (integer == null) {
				counts.put(key, 1);
			}
			else {
				counts.put(key, integer + 1);

			}

		}

		int product = 1;
		for (Flow flow : counts.keySet()) {
			Integer integer = counts.get(flow);
			System.out.println(flow.getName() + ": " + integer);
			product *= integer;
		}
		System.err.println(product);

		System.out.println("All paths: " + totalPathCount);
		System.out.println("Covered paths: " + coveredPaths.size());
		System.out.println("Ratio: " + ((double) totalPathCount) / coveredPaths.size());
		indexizePaths(allPaths, allPathsPerElement);
		System.out.println("done");
	}

	/**
	 * 
	 * @created 10.04.2012
	 * @param element
	 * @param path
	 * @param pathsPerElement2
	 */
	private void put(DiaFluxElement element, Path path, Map<DiaFluxElement, Collection<Path>> map) {
		Collection<Path> collection = map.get(element);
		if (collection == null) {
			collection = new HashSet<Path>();
			map.put(element, collection);
		}
		collection.add(path);
	}

	public int getTotalPathCount() {
		return allPaths.size();
	}

}
