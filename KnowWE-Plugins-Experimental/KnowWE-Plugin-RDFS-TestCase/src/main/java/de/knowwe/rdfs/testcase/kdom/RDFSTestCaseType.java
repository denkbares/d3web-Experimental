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
package de.knowwe.rdfs.testcase.kdom;

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdfs.testcase.renderer.RDFSTestCaseRenderer;

/**
 * 
 * @author Sebastian Furth
 * @created 20.12.2011
 */
public class RDFSTestCaseType extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;

	public static final String MARKUP_NAME = "RDFSTestCase";
	public static final String ANNOTATION_BASE = "base";
	public static final String ANNOTATION_NAME = "name";

	static {
		MARKUP = new DefaultMarkup(MARKUP_NAME);
		MARKUP.addContentType(new RDFSTestCaseContentType());
		MARKUP.addAnnotation(ANNOTATION_BASE, false);
		MARKUP.addAnnotation(ANNOTATION_NAME, true);

	}

	public RDFSTestCaseType() {
		super(MARKUP);
		this.addCompileScript(new RDFSTestCaseHandler());
		this.setRenderer(new RDFSTestCaseRenderer());
	}

}
