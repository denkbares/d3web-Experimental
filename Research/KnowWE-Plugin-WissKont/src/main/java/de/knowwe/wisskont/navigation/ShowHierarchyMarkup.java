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
package de.knowwe.wisskont.navigation;

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author jochenreutelshofer
 * @created 28.11.2012
 */
public class ShowHierarchyMarkup extends DefaultMarkupType {

	public ShowHierarchyMarkup(DefaultMarkup markup) {
		super(markup);
		setIgnorePackageCompile(true);
		this.setRenderer(new ShowHierarchyRenderer());
	}

	public static final String RELATION_ANNOTATION_KEY = "relation";
	public static final String START_CONCEPT = "concept";

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("showHierarchy");
		m.addAnnotation(RELATION_ANNOTATION_KEY, false);
		m.addAnnotation(START_CONCEPT, false);

	}

	public ShowHierarchyMarkup() {
		super(m);
		setIgnorePackageCompile(true);
		this.setRenderer(new ShowHierarchyRenderer());
	}

}
