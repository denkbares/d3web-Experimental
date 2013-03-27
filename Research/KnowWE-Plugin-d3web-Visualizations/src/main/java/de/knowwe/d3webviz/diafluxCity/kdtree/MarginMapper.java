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

import de.knowwe.d3webviz.diafluxCity.kdtree.RectanglePacker.Mapping;


/**
 * 
 * @author Reinhard Hatko
 * @created 07.02.2012
 */
public class MarginMapper<I> implements Mapping<I> {

	private final Mapping<I> delegate;
	private final double margin;

	/**
	 * @param delegate
	 * @param margin
	 */
	public MarginMapper(Mapping<I> delegate, double margin) {
		this.delegate = delegate;
		this.margin = margin;
	}

	public Dimension2D map(I object) {
		Dimension2D dim = delegate.map(object);

		return KDUtils.buffer(dim, margin);
	}

	public double getMargin() {
		return margin;
	}

	public Mapping<I> getDelegate() {
		return delegate;
	}
}
