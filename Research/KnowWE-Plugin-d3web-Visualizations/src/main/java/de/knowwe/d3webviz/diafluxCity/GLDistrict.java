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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * 
 * @author Reinhard Hatko
 * @created 15.02.2012
 */
public class GLDistrict extends GLBuilding {

	private final List<GLBuilding> childs = new LinkedList<GLBuilding>();

	public GLDistrict(double length, double width, double height) {
		super(length, width, height);
	}

	@Override
	public String toString() {
		String result = super.toString();
		result += ",{";
		result += "\"type\": \"translate\",";
		result += "\"x\": -" + getX() + ",";
		result += "\"y\": -" + getHeight() + ",";
		result += "\"z\": -" + getY() + ",";
		result += "\"nodes\":[";

		Iterator<GLBuilding> iterator = childs.iterator();

		while (iterator.hasNext()) {
			GLBuilding building = iterator.next();
			result += building.toString();
			if (iterator.hasNext()) result += ",";

		}

		result += "]";

		result += "}";

		return result;
	}

	public void addChild(GLBuilding building) {

		// building.setX(building.getX());
		// building.setY(building.getY());
		this.childs.add(building);
	}

}
