/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.ophtovisD3.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import de.knowwe.termbrowser.HierarchyProvider;

/**
 * 
 * @author adm_rieder
 * @created 10.09.2013
 */
public class VisualizationHierarchyProvider implements HierarchyProvider {

	private final Map<String, String> parentChildPairs;

	/**
	 * 
	 */
	public VisualizationHierarchyProvider(Map<String, String> parentChildPairs) {
		this.parentChildPairs = parentChildPairs;
	}

	@Override
	public List<String> getChildren(String term) {
		throw new NotImplementedException();

	}

	@Override
	public List<String> getParents(String term) {
		List<String> parent = new ArrayList<String>();
		String parentString = parentChildPairs.get(term);
		if (!(parentString == null || parentString.isEmpty())) {
			parent.add(parentString);
		}
		return parent;
	}

	@Override
	public boolean isSubNodeOf(String subnode, String term) {
		String parentString = parentChildPairs.get(subnode);
		if (parentString == null) {
			return false;
		}
		if (term.equals(parentString)) {
			return true;
		}
		else {
			return isSubNodeOf(parentString, term);
		}
	}

}
