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
	private int id;
	private int parentId;

	boolean hasChildren = false;

	public GraphNode(String name, int leftCo, int id, int parentId, boolean hasChildren)
	{
		this.name = name;
		this.leftCo = leftCo;
		this.id = id;
		this.parentId = parentId;
		this.hasChildren = hasChildren;
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

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
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


	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	// ////////////////////////////////////////////////////////////////////////////////



}

