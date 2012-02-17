/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.d3web.owl.assignment;

import de.knowwe.core.compile.packaging.KnowWEPackageManager;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author Sebastian Furth
 * @created Mar 30, 2011
 */
public class AssignmentMarkup extends DefaultMarkupType {

	private static DefaultMarkup MARKUP = null;
	public static String BASEURI = "baseuri";

	static {
		MARKUP = new DefaultMarkup("Assignment");
		MARKUP.addContentType(new AssignmentContentType());
		MARKUP.addAnnotation(KnowWEPackageManager.PACKAGE_ATTRIBUTE_NAME, false);
		MARKUP.addAnnotation(BASEURI, false);
	}

	public AssignmentMarkup() {
		super(MARKUP);
		this.setRenderer(new DefaultMarkupRenderer(
				"KnowWEExtension/d3web/icon/rule24.png"));
	}

}
