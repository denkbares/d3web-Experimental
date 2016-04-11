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
package de.d3web.we.diaflux.datamanagement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.d3web.diaFlux.flow.EndNode;
import de.d3web.diaFlux.flow.Flow;


/**
 * 
 * @author Reinhard Hatko
 * @created 29.06.2012
 */
public class FlowDomain implements Domain {

	private final Flow flow;
	private final Collection<EndNode> endNodes;

	private FlowDomain(FlowDomain domain) {
		this(domain.getFlow(), domain.getEndNodes());
	}


	public FlowDomain(EndNode... nodes) {
		this(nodes[0].getFlow(), Arrays.asList(nodes));
	}

	private FlowDomain(Flow flow, Collection<EndNode> endNodes) {
		this.flow = flow;
		this.endNodes = new HashSet<EndNode>();
		this.endNodes.addAll(endNodes);
	}

	public FlowDomain(Flow flow) {
		this(flow, flow.getExitNodes());
	}

	@Override
	public Domain add(Domain domain) {
		FlowDomain other = (FlowDomain) domain;
		FlowDomain result = new FlowDomain(this);

		result.endNodes.addAll(other.getEndNodes());

		return result;
	}

	@Override
	public Domain negate() {
		List<EndNode> exitNodes = flow.getExitNodes();
		exitNodes.removeAll(this.endNodes);
		FlowDomain result = new FlowDomain(getFlow(), exitNodes);

		return result;
	}

	@Override
	public boolean contains(Domain domain) {
		FlowDomain other = (FlowDomain) domain;
		return this.endNodes.containsAll(other.getEndNodes());
	}

	@Override
	public boolean intersects(Domain domain) {
		return !this.intersect(domain).isEmpty();
	}

	@Override
	public boolean isEmpty() {
		return endNodes.isEmpty();
	}

	@Override
	public Domain intersect(Domain domain) {
		FlowDomain other = (FlowDomain) domain;
		LinkedList<EndNode> list = new LinkedList<EndNode>(this.getEndNodes());
		list.retainAll(other.getEndNodes());

		return new FlowDomain(getFlow(), list);
	}

	public Flow getFlow() {
		return flow;
	}

	public Collection<EndNode> getEndNodes() {
		return Collections.unmodifiableCollection(endNodes);
	}


}
