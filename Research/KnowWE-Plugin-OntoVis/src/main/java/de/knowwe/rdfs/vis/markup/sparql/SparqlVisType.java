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
package de.knowwe.rdfs.vis.markup.sparql;

import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author jochenreutelshofer
 * @created 23.07.2013
 */

public class SparqlVisType extends DefaultMarkupType {

	public static final String ANNOTATION_COMMENT = "comment";
	public static final String ANNOTATION_SIZE = "size";
	public static final String ANNOTATION_FORMAT = "format";
	public static final String ANNOTATION_LANGUAGE = "language";

	public static final String ANNOTATION_DOT_APP = "dotApp";
	public static final String ANNOTATION_ADD_TO_DOT = "dotAddLine";

	public static final String ANNOTATION_RENDERER = "renderer";
	public static final String ANNOTATION_VISUALIZATION = "visualization";

	private static final DefaultMarkup MARKUP;

	private enum dot_apps {
			dot, neato
	};

	public enum Renderer {
			dot, d3
	};

	public enum Visualizations {
			wheel, force
	};

	static {
		MARKUP = new DefaultMarkup("SparqlVis");
		SparqlVisContentType sparqlContentType = new SparqlVisContentType();
		MARKUP.addContentType(sparqlContentType);
		MARKUP.addAnnotation(ANNOTATION_COMMENT, false);
		MARKUP.addAnnotation(ANNOTATION_SIZE, false);
		MARKUP.addAnnotation(ANNOTATION_FORMAT, false);
		MARKUP.addAnnotation(PackageManager.ANNOTATION_MASTER, false);
		MARKUP.addAnnotation(ANNOTATION_LANGUAGE, false);
		MARKUP.addAnnotation(ANNOTATION_DOT_APP, false, dot_apps.values());
		MARKUP.addAnnotation(ANNOTATION_ADD_TO_DOT, false);
		MARKUP.addAnnotation(ANNOTATION_RENDERER, false, Renderer.values());
		MARKUP.addAnnotation(ANNOTATION_VISUALIZATION, false, Visualizations.values());
	}

	public SparqlVisType() {
		super(MARKUP);
		this.setIgnorePackageCompile(true);
	}

}
