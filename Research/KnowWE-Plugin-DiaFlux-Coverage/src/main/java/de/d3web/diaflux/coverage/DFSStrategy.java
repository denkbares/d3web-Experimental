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
import java.util.List;

import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.EndNode;

/**
 * A strategy for traversing diaflux models depth-first.
 * 
 * @author Reinhard Hatko
 * @created 12.04.2012
 */
public interface DFSStrategy {

	/**
	 * Returns a {@link Collection} of Paths, at which the DFS should initially
	 * start. The paths must contain an appropriate callstack of composed nodes
	 * if necessary.
	 * 
	 * @created 12.04.2012
	 * @return
	 */
	List<Path> getInitialStartPaths();

	/**
	 * Determines, if the provided edge should be followed on the given Path.
	 * 
	 * @param edge
	 * @param path
	 * @return true, if the edge should be followed, false otherwise
	 */
	boolean followEdge(Edge edge, Path path);

	
	/**
	 * Offers the supplied {@link DiaFluxElement} to the path. If the path
	 * accepts it, then the DFS continues at this element. If not, the path is
	 * finished and added to the list of found paths. In this case the method
	 * createStartPath is called with the found Path. It may return a new
	 * starting Path with an appropriate call stack.
	 * 
	 * 
	 * @created 12.04.2012
	 * @param node
	 * @param path
	 * @return s true, if the path accepts the element. Then, the path will
	 *         continue. False, otherwise
	 */
	boolean offer(DiaFluxElement el, Path path);

	/**
	 * This method is called after a new path has been found. There are 3
	 * possible ways for a path to finish:
	 * 
	 * 1. The method <code>offer</code> returned <code>false</code>: Then, this
	 * method is called with the path object without modifying it.
	 * 
	 * 2. There are no edges to take at a given node (after filtering by
	 * <code>followEdge</code>).
	 * 
	 * 3. An {@link EndNode} has been reached and there are no subflows to
	 * return from.
	 * 
	 * @created 23.04.2012
	 * @param path
	 */
	void found(Path path);

	/**
	 * This method is called after a path is found, i.e. offer() returned false.
	 * From the found path a new starting path can be generated. Then, a new
	 * Path object has to be returned, or null otherwise.
	 * 
	 * @created 12.04.2012
	 * @param path
	 * @return
	 */
	Path createStartPath(Path path);

	/**
	 * Determines, if the subflow that is called by node, should by entered.
	 * 
	 * @created 13.04.2012
	 * @param node
	 * @param path
	 * @return
	 */
	boolean enterSubflow(ComposedNode node, Path path);

	/**
	 * This method is notified, if a (sub-)path has been completely traversed.
	 * It can be used to, e.g., clean data associated with a path.
	 * 
	 * 
	 * @created 09.08.2012
	 * @param path
	 */
	void finished(Path path);

}