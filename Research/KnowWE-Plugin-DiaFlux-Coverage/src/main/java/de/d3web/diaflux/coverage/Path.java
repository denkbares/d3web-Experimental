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
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Node;

/**
 * 
 * @author Reinhard Hatko
 * @created 12.03.2012
 */
public class Path implements Iterable<DiaFluxElement> {
	

	private final List<DiaFluxElement> path;
	private final Deque<ComposedNode> callStack;
	private boolean finished;

	private Path(List<DiaFluxElement> path, Deque<ComposedNode> callStack) {
		this.path = path;
		this.callStack = new LinkedList<ComposedNode>(callStack);
		this.finished = false;
	}

	Path(DiaFluxElement node) {
		this(Arrays.asList(node), new LinkedList<ComposedNode>());
	}
	
	protected Path(DiaFluxElement node, Deque<ComposedNode> callStack) {
		this(Arrays.asList(node), callStack);
	}

	Path append(Node node) {
		if (path.contains(node)) {
			finished = true;
		}
		return appendElement(node);

	}

	Path append(Edge edge) {
		return appendElement(edge);
	}

	private Path appendElement(DiaFluxElement el) {
		ArrayList<DiaFluxElement> newPath = new ArrayList<DiaFluxElement>(this.path);
		newPath.add(el);
		Path np = new Path(newPath, this.callStack);
		return np;

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

	Path newPath() {
		return new Path(getTail(), this.callStack);
	}

	public boolean isFinished() {
		return finished;
	}

	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof Path)) {
			return false;
		}

		return this.path.equals(((Path) obj).path);

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
