/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.d3webviz.diafluxCity;

/**
 * @author Reinhard Hatko
 * @created 07.03.2012
 */
public class GLCity {

	private final GLDistrict district;

	public GLCity(GLDistrict district) {
		this.district = district;
	}

	@Override
	public String toString() {
		String result = "{";
		result += "\"type\": \"translate\",";
		result += "\"x\": " + (district.getLength() / 2) + ",";
		result += "\"z\": " + district.getWidth() / 2 + ",";
		result += "\"nodes\":[";
		result += district.toString();
		result += "]";

		result += "}";

		return result;
	}

}
