/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.ophtovisD3.utils;

import java.util.Map;

import de.d3web.collections.PartialHierarchy;
import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;

/**
 * 
 * @author adm_rieder
 * @created 10.09.2013
 */
public class VisualizationHierarchyProvider implements PartialHierarchy<NodeWithName> {

	private final Map<String, String> parentChildPairs;

	/**
	 * 
	 */
	public VisualizationHierarchyProvider(Map<String, String> parentChildPairs) {
		this.parentChildPairs = parentChildPairs;
	}


	private boolean isSuccessorOf(Identifier subnode, Identifier term) {
		String parentString = parentChildPairs.get(Strings.unquote(subnode.toExternalForm()));
		if (parentString == null) {
			return false;
		}
		if (Strings.unquote(term.toExternalForm()).equals(parentString)) {
			return true;
		}
		else {
			return isSuccessorOf(new Identifier(parentString), term);
		}
	}


	@Override
	public boolean isSuccessorOf(NodeWithName node1, NodeWithName node2) {
		return isSuccessorOf(new Identifier(node1.name), new Identifier(node2.name));
	}

}
