/*
 * Copyright (C) 2010 denkbares GmbH, Wuerzburg
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
package de.d3web.we.ci4ke.groovy;

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * Type for dynamically implemented CITests with Groovy
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 30.11.2010
 */
public class GroovyCITestType extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;

	public static final String ANNOTATION_NAME = "name";

	static {
		MARKUP = new DefaultMarkup("CITest");
		MARKUP.addAnnotation(ANNOTATION_NAME, true);
	}

	public GroovyCITestType() {
		super(MARKUP);
		this.setIgnorePackageCompile(true);
		this.addSubtreeHandler(new GroovyCITestSubtreeHandler());
		this.setCustomRenderer(new GroovyCITestRenderer());
	}

}
