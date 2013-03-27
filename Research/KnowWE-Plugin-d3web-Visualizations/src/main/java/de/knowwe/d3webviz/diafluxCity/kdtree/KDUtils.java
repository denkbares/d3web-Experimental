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
import java.awt.geom.Rectangle2D;


/**
 * 
 * @author Reinhard Hatko
 * @created 06.02.2012
 */
public class KDUtils {

	private KDUtils() {
	}


	public static Rectangle2D[] splitRect(Rectangle2D bounds, Dimension2D dim, int level) {
		Rectangle2D[] childs;
		if (level % 2 == 0) {
			// split width;
			double width = dim.getWidth();
			// System.out.println(indent() + "split Vertical at " + width +
			// " in " + bounds);
			childs = splitVerticalAt(bounds, width);
		}
		else {
			// split height
			double height = dim.getHeight();
			// System.out.println(indent() + "split Horizontal at " + height +
			// " in " + bounds);
			childs = splitHorizontalAt(bounds, height);
		}
		return childs;
	}

	/**
	 * size is per side (= *2);
	 */
	public static Dimension2D buffer(Dimension2D dim, double size) {
		return new DoubleDimension(dim.getWidth() + (2 * size), dim.getHeight() + (2 * size));
	}

	public static boolean equalsSize(Rectangle2D r1, Dimension2D dim) {
		return r1.getWidth() == dim.getWidth()
				&& r1.getHeight() == dim.getHeight();
	
	}

	public static boolean fitsInside(Dimension2D dim, Rectangle2D rect) {
		return rect.getWidth() >= dim.getWidth() && rect.getHeight() >= dim.getHeight();
	}

	public static Rectangle2D[] splitHorizontalAt(Rectangle2D rect, double y) {
		return new Rectangle2D[] {
				new Rectangle2D.Double(rect.getX(), rect.getY(), rect.getWidth(), y),
				new Rectangle2D.Double(rect.getX(), rect.getY() + y, rect.getWidth(), rect.getHeight() - y)
		};
	}

	public static Rectangle2D[] splitVerticalAt(Rectangle2D rect, double x) {
	
		return new Rectangle2D[] {
				new Rectangle2D.Double(rect.getX(), rect.getY(), x, rect.getHeight()),
				new Rectangle2D.Double(rect.getX() + x, rect.getY(), rect.getWidth() - x,
						rect.getHeight())
		};
	}

	public static boolean rectIsBiggerThan(Rectangle2D r1, Dimension2D dim) {
		return r1.getWidth() >= dim.getWidth()
				&& r1.getHeight() >= dim.getHeight();
	
	}

	public static Dimension2D getDimension(Rectangle2D rect) {
		return new DoubleDimension(rect.getWidth(), rect.getHeight());
	}

	public static double getRatio(Dimension2D dim) {
		return dim.getWidth() / dim.getHeight();
	}

	public static double getRatio(Rectangle2D rect) {
		return rect.getWidth() / rect.getHeight();
	}

	public static double getArea(Dimension2D dim) {
		return dim.getWidth() * dim.getHeight();
	}

	public static double getArea(Rectangle2D rect) {
		return rect.getWidth() * rect.getHeight();
	}

	public static Rectangle2D subtractMargins(Rectangle2D r, double margin) {
		return new Rectangle2D.Double(r.getX() + margin, r.getY() + margin, r.getWidth()
				- (2 * margin), r.getHeight() - (2 * margin));
	}

}
