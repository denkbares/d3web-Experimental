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
 * @param <T>
 * @created 26.10.2012
 */
public class GraphNodeConnection implements Comparable<GraphNodeConnection> {

	GraphNode sourceNode;
	GraphNode targetNode;
	// TODO String ?
	String connectionType;

	public GraphNodeConnection(GraphNode sourceNode, GraphNode targetNode, String connectionType) {

		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.connectionType = connectionType;

	}

	public int getTargetID() {
		return targetNode.getId();
	}

	public int getSourceID() {
		return sourceNode.getId();
	}

	public int sourceIfTargetIs(int target) {
		if (targetNode.getId() == target) {
			return sourceNode.getId();
		}
		else {
			return -1;
		}
	}


	@Override
	public int compareTo(GraphNodeConnection o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
