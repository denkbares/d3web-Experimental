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
package de.knowwe.diaflux.coverage.gl;

import java.awt.Color;

/**
 * 
 * @author Reinhard Hatko
 * @created 15.02.2012
 */
public  class GLBuilding {

	private double length;
	private double width;
	private double height;
	private double x;
	private double y;
	private String name;
	private Color color;

	/**
	 * @param xDim
	 * @param yDim
	 * @param zDim
	 */
	public GLBuilding(double length, double width, double height) {
		setLength(length);
		setWidth(width);
		setHeight(height);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String toString() {
		float[] comp = color.getRGBComponents(null);
		String result = "";
		result += "{";
		result += "type: 'translate',";
		result += "x: -" + (getLength() / 2 + getX()) + ",";
		result += "y: -" + getHeight() / 2 + ",";
		result += "z: -" + (getWidth() / 2 + getY()) + ",";
		result += "nodes:[{";
		result += "type: 'name',";
		result += "name: '" + getName() + "',";
		result += "nodes:[{";
		result += "type: 'material',";
		result +=" baseColor:{";
		result += "r: " + comp[0]+ ",";
		result += "g: " + comp[1]+ "," ;
		result += "b: " + comp[2];
		result += "},";
		if (comp[3] < 1) {
			result += "alpha: " +comp[3]+ ",";
			
		}
		result += "nodes:[{";
		result += "type: 'box',";
		result += "xSize: " + getLength() / 2 + ",";
		result += "ySize: " + getHeight() / 2 + ",";
		result += "zSize: " + getWidth() / 2;
		result += "}]"; //box
		result += "}]"; //material
		result += "}]"; //name
		result += "}";//translate
		return result;
	}


}
