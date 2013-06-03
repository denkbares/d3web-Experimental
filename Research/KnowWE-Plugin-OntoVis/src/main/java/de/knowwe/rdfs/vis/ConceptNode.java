/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.rdfs.vis;

import de.d3web.strings.Strings;
import de.knowwe.rdfs.vis.RenderingCore.NODE_TYPE;

/**
 * 
 * @author jochenreutelshofer
 * @created 23.05.2013
 */
public class ConceptNode {

	private final String name;
	private String conceptUrl = null;
	private String conceptLabel = null;
	private boolean outer = false;
	private boolean root = false;
	private NODE_TYPE type;

	public NODE_TYPE getType() {
		return type;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public void setOuter(boolean outer) {
		this.outer = outer;
	}

	public boolean isOuter() {
		return outer;
	}

	/**
	 * 
	 */
	public ConceptNode(String name) {
		this.name = Strings.unquote(name);
	}

	/**
	 * 
	 */
	public ConceptNode(String name, NODE_TYPE type, String url, String label) {
		this(name);
		this.type = type;
		if (label == null) {
			throw new NullPointerException("label is null");
		}
		if (url == null) {
			throw new NullPointerException("url is null");
		}
		this.conceptLabel = label;
		this.conceptUrl = url;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + " :" + getName();
	}

	public String getConceptUrl() {
		return conceptUrl;
	}

	public String getConceptLabel() {
		return conceptLabel;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof ConceptNode) {
			return name.equals(((ConceptNode) obj).name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
