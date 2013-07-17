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
package de.knowwe.d3web.owl;

import de.knowwe.core.compile.packaging.PackageAnnotationNameType;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.compile.packaging.PackageTermReference;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * Type for providing OWL ontologies. The provided ontologies will be attached
 * to a certain knowledge base.
 * 
 * @author Sebastian Furth
 * @created Mar 23, 2011
 */
public class OntologyProviderType extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;

	public static final String MARKUP_NAME = "Ontology";
	public static final String ANNOTATION_SRC = "src";

	static {
		MARKUP = new DefaultMarkup(MARKUP_NAME);
		MARKUP.addAnnotation(ANNOTATION_SRC, false);
		MARKUP.addAnnotation(PackageManager.PACKAGE_ATTRIBUTE_NAME, false);
		MARKUP.addAnnotationNameType(PackageManager.PACKAGE_ATTRIBUTE_NAME,
				new PackageAnnotationNameType());
		MARKUP.addAnnotationContentType(PackageManager.PACKAGE_ATTRIBUTE_NAME,
				new PackageTermReference());
	}

	public OntologyProviderType() {
		super(MARKUP);
		this.addSubtreeHandler(new OntologyHandler());
		this.setRenderer(new OntologyEscapeRenderer());
	}

}
