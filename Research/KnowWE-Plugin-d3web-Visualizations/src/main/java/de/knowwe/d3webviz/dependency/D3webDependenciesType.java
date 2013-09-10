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
package de.knowwe.d3webviz.dependency;

import de.knowwe.core.compile.packaging.PackageAnnotationNameType;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.compile.packaging.PackageTermReference;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;


/**
 * 
 * @author Reinhard Hatko
 * @created 04.11.2012
 */
public class D3webDependenciesType extends DefaultMarkupType {

	private static final DefaultMarkup m;
	public static final String ANNOTATION_SHOW_TYPE = "showtypes";
	public static final String ANNOTATION_IGNORE = "ignore";
	public static final String ANNOTATION_SHOW_ALL = "showall";

	static {
		m = new DefaultMarkup("d3webDependencies");
		m.addAnnotation(PackageManager.PACKAGE_ATTRIBUTE_NAME);
		m.addAnnotationNameType(PackageManager.PACKAGE_ATTRIBUTE_NAME,
				new PackageAnnotationNameType());
		m.addAnnotationContentType(PackageManager.PACKAGE_ATTRIBUTE_NAME,
				new PackageTermReference());
		m.addAnnotation(ANNOTATION_SHOW_TYPE, false, "true", "false");
		m.addAnnotation(ANNOTATION_SHOW_ALL, false, "true", "false");
		m.addAnnotation(ANNOTATION_IGNORE);
	}


	public D3webDependenciesType() {
		super(m);
		setRenderer(new D3webDependenciesRenderer());
	}

}
