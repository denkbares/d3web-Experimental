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
package de.knowwe.ophtovis;


/**
 * 
 * @author adm_rieder
 * @created 22.10.2012
 */
public class GraphNode
{

	private String name;
	private int leftCo;
	private int topCo;
	private int id;

	public GraphNode(String name, int leftCo, int topCo, int id)
	{
		this.name = name;
		this.leftCo = leftCo;
		this.topCo = topCo;

		this.id = id;
	}

	public String getStringID() {

		return "" + getId();

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLeftCo() {
		return leftCo;
	}

	public void setLeftCo(int leftCo) {
		this.leftCo = leftCo;
	}

	public int getTopCo() {
		return topCo;
	}

	public void setTopCo(int topCo) {
		this.topCo = topCo;
	}

	// ////////////////////////////////////////////////////////////////////////////////



}

