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
package de.knowwe.d3webviz.diafluxCity;

import java.awt.geom.Dimension2D;

import de.knowwe.d3webviz.diafluxCity.kdtree.DoubleDimension;
import de.knowwe.d3webviz.diafluxCity.kdtree.RectanglePacker.Mapping;


/**
 * 
 * @author Reinhard Hatko
 * @created 08.02.2012
 */
public class GLNodeMapper implements Mapping<GLBuilding> {

	public Dimension2D map(GLBuilding object) {

		DoubleDimension dimension = new DoubleDimension(object.getLength(),
				object.getWidth()); // TODO
																								// *2
																								// ??
		return dimension;
	}

}
