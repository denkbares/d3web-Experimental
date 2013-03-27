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
package de.knowwe.d3webviz.diafluxCity.kdtree;

import java.awt.geom.Dimension2D;
import java.util.LinkedList;
import java.util.List;



/**
 * 
 * @author Reinhard Hatko
 * @created 05.02.2012
 */
public class CollectEmptyLeavesOfSizeVisitor<I> implements Visitor<I> {

	private final Dimension2D minSize = new DoubleDimension();
	private final List<KDNode<I>> nodes = new LinkedList<KDNode<I>>();

	public CollectEmptyLeavesOfSizeVisitor(Dimension2D dim) {
		this.minSize.setSize(dim);
	}

	@Override
	public void visit(KDNode<I> node) {
		if (node.isLeaf() && !node.isOccupied()
				&& KDUtils.rectIsBiggerThan(node.getBounds(), minSize)) {
			nodes.add(node);
		}
		
	}

	public List<KDNode<I>> getNodes() {
		return nodes;
	}

}
