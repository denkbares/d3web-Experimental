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

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author Reinhard Hatko
 * @created 05.02.2012
 */
public class CollectAssignedRectangles<I> implements Visitor<I> {

	private final Map<I, Rectangle2D> rects = new HashMap<>();
	private final double margin;

	public CollectAssignedRectangles(double margin) {
		this.margin = margin;
	}

	@Override
	public void visit(KDNode<I> node) {

		if (node.isOccupied()) {
			Rectangle2D bounds = node.getBounds();
			Rectangle2D result = KDUtils.subtractMargins(bounds, margin);

			rects.put(node.getValue(), result);
		}

	}

	public Map<I, Rectangle2D> getAssignments() {
		return rects;
	}

	public double getMargin() {
		return margin;
	}

}