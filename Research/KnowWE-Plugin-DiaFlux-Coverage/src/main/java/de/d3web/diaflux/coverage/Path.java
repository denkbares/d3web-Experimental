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
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.DiaFluxElement;
import de.d3web.diaFlux.flow.Node;

/**
 * A sequence of consecutive nodes and edges.
 * 
 * @author Reinhard Hatko
 * @created 12.03.2012
 */
public class Path implements Iterable<DiaFluxElement> {
	

	private final List<DiaFluxElement> path;
	private final Deque<ComposedNode> callStack;

	private Path(List<DiaFluxElement> path, Deque<ComposedNode> callStack) {
		this.path = new ArrayList<DiaFluxElement>(path);
		this.callStack = new LinkedList<ComposedNode>(callStack);
	}

	private Path(Path path) {
		this(path.path, path.callStack);
	}

	public Path(DiaFluxElement node) {
		this(Arrays.asList(node), new LinkedList<ComposedNode>());
	}
	
	protected Path(DiaFluxElement node, Deque<ComposedNode> callStack) {
		this(Arrays.asList(node), callStack);
	}

	void append(DiaFluxElement el) {
		this.path.add(el);
	}

	Path copy() {
		return new Path(this);
	}


	public Deque<ComposedNode> getCallStack() {
		return new LinkedList<ComposedNode>(callStack);
	}

	void enterFlow(ComposedNode node) {
		callStack.push(node);
	}


	ComposedNode returnFromFlow() {
		return callStack.pop();
	}

	boolean hasEnteredFlow() {
		return !callStack.isEmpty();
	}

	/**
	 * creates a new Path, that start at the tail of this path and has the same
	 * callstack.
	 * 
	 * @created 12.04.2012
	 * @return
	 */
	Path newPath() {
		return new Path(getTail(), this.callStack);
	}


	@Override
	public int hashCode() {
		return this.path.hashCode() /** this.callStack.hashCode() */
		;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof Path)) {
			return false;
		}

		Path other = (Path) obj;
		return this.path.equals(other.path)/*
											 * &&
											 * this.callStack.equals(other.callStack
											 * )
											 */;

	}

	@Override
	public Iterator<DiaFluxElement> iterator() {
		return path.iterator();
	}

	Node getTail() {
		return (Node) this.path.get(this.path.size() - 1);
	}

	Node getHead() {
		return (Node) this.path.get(0);
	}

	@Override
	public String toString() {
		return super.toString() + this.path.toString();
	}

	public boolean contains(DiaFluxElement el) {
		return this.path.contains(el);
	}

	/**
	 * 
	 * @created 04.04.2012
	 * @return
	 */
	public int getLength() {
		return path.size();
	}

}
