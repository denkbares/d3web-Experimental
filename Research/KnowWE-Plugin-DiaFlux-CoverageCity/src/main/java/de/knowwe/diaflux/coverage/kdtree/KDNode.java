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
package de.knowwe.diaflux.coverage.kdtree;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import de.knowwe.diaflux.coverage.kdtree.RectanglePacker.Mapping;

public class KDNode<I> {

	private final Rectangle2D bounds = new Rectangle2D.Double();
	private final KDNode<I> parent;
	private I value;
	private KDNode<I> left;
	private KDNode<I> right;

	public KDNode(Rectangle2D rectangle) {
		this(null, rectangle);
	}

	public KDNode(KDNode<I> parent, Rectangle2D rectangle) {
		this.parent = parent;
		this.bounds.setRect(rectangle);

	}

	boolean isOccupied() {
		return value != null;
	}

	boolean isLeaf() {
		return left == null && right == null;
	}

	/**
	 * Returns the complete bounds of this node
	 * 
	 */
	public Rectangle2D getBounds() {
		return bounds;
	}


	public <T extends Visitor<I>> T visitInorder(T visitor) {
		if (this.left != null) left.visitInorder(visitor);
		visitor.visit(this);
		if (this.right != null) right.visitInorder(visitor);

		return visitor;

	}

	int getDepth() {
		if (parent == null) return 0;
		else return parent.getDepth() + 1;
	}

	
	KDNode<I> insert(I value, Mapping<I> map) {
		Dimension2D dim = map.map(value);

		if (!KDUtils.fitsInside(dim, bounds)) {
			// System.out.println(indent() + "no fit");
			return null;
		}
		else if (isOccupied()) { // full leaf
			return null;
		}
		else if (!isLeaf()) { // inner node
		// System.out.println(indent() + "insert left");
			KDNode<I> node = this.left.insert(value, map);

			if (node == null) {
				// System.out.println(indent() + "insert right");
				return this.right.insert(value, map);
			}
			else {
				return node;
			}


		}
		else if (!isOccupied() && KDUtils.equalsSize(bounds, dim)) { // perfect leaf

			this.value = value;
			// System.out.println(indent() + "perfect at " + this.value);
			return this;
		}
		else { // too big leaf
			Rectangle2D[] childs = doSplitFor(dim);
			// System.out.println(indent() + "\tnew left: " + childs[0]);
			// System.out.println(indent() + "\tnew right: " + childs[1]);
			this.left = new KDNode<I>(this, childs[0]);
			this.right = new KDNode<I>(this, childs[1]);
			return insert(value, map);

		}
	}

	/**
	 * calculates the resulting rects, if this node would be split for dim. DOES
	 * NOT CHANGE THE TREE
	 * 
	 * @param dim
	 * @return
	 */
	Rectangle2D[] doSplitFor(Dimension2D dim) {
		return KDUtils.splitRect(bounds, dim, getDepth());
	}


	Rectangle2D findCompleteSplitForLeave(Dimension2D dim) {
		assert isLeaf();

		Rectangle2D[] childs;
		Rectangle2D bounds = this.bounds;
		int depth = getDepth();

		do {
			childs = KDUtils.splitRect(bounds, dim, ++depth);
			if (childs[0].equals(bounds)) break;
			bounds = childs[0];
		} while (childs != null);
		return childs[0];
	}


	public I getValue() {
		return value;
	}

	private String indent() {
		String s = "";
		for (int i = 0; i < getDepth(); i++) {
			s += "\t";
		}
		return s;
	}

	@Override
	public String toString() {
		return "KDNode" + bounds.toString() + ", " + (isLeaf() ? "leaf" : "");
	}



}