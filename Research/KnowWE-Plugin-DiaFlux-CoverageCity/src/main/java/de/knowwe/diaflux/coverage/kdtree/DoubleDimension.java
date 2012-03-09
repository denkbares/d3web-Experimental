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


/**
 * 
 * @author Reinhard Hatko
 * @created 06.02.2012
 */
public class DoubleDimension extends Dimension2D {

	private double width;
	private double height;

	public DoubleDimension() {
		this(0, 0);
	}

	public DoubleDimension(double width, double height) {
		setSize(width, height);
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[width=" + width + ",height=" + height + "]";
	}

}
