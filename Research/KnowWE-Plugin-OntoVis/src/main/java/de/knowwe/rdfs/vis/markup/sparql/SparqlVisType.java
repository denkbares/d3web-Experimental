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
import de.knowwe.core.kdom.rendering.NothingRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.sparql.Rdf2GoCoreCheckRenderer;
import de.knowwe.rdfs.vis.markup.OntoVisType;
import de.knowwe.visualization.GraphDataBuilder;

/**
 * 
 * @author jochenreutelshofer
 * @created 23.07.2013
 */

public class SparqlVisType extends DefaultMarkupType {

	public static final String ANNOTATION_CONCEPT = "concept";
	public static final String ANNOTATION_COMMENT = "comment";
	public static final String ANNOTATION_SIZE = "size";
	public static final String ANNOTATION_FORMAT = "format";
	public static final String ANNOTATION_LANGUAGE = "language";
	public static final String ANNOTATION_LINK_MODE = "linkMode";

	public static final String ANNOTATION_RANK_DIR = "rankDir";

	public static final String ANNOTATION_DOT_APP = "dotApp";
	public static final String ANNOTATION_ADD_TO_DOT = "dotAddLine";

	public static final String ANNOTATION_RENDERER = "renderer";
	public static final String ANNOTATION_VISUALIZATION = "visualization";
	public static final String ANNOTATION_DESIGN = "design";
	public static final String ANNOTATION_LABELS = "labels";

	private static final DefaultMarkup MARKUP;

	private enum dot_apps {
		dot, neato
	};

	public enum LinkMode {
		jump, browse
	};

	public enum Visualizations {
		wheel, force, tree
	};

	static {
		MARKUP = new DefaultMarkup("SparqlVis");
		SparqlVisContentType sparqlContentType = new SparqlVisContentType();
		MARKUP.addContentType(sparqlContentType);
		MARKUP.addAnnotation(ANNOTATION_CONCEPT, false);
		MARKUP.addAnnotation(ANNOTATION_COMMENT, false);
		MARKUP.addAnnotation(ANNOTATION_SIZE, false);
		MARKUP.addAnnotation(ANNOTATION_FORMAT, false);
		MARKUP.addAnnotation(PackageManager.MASTER_ATTRIBUTE_NAME, false);
		MARKUP.addAnnotation(ANNOTATION_LANGUAGE, false);
		MARKUP.addAnnotation(ANNOTATION_DOT_APP, false, dot_apps.values());
		MARKUP.addAnnotation(ANNOTATION_ADD_TO_DOT, false);
		MARKUP.addAnnotation(ANNOTATION_RENDERER, false, GraphDataBuilder.Renderer.values());
		MARKUP.addAnnotation(ANNOTATION_VISUALIZATION, false, Visualizations.values());
		MARKUP.addAnnotation(ANNOTATION_LINK_MODE, false, LinkMode.values());
		MARKUP.addAnnotationRenderer(ANNOTATION_LINK_MODE, NothingRenderer.getInstance());
		MARKUP.addAnnotation(ANNOTATION_DESIGN, false);
		MARKUP.addAnnotationRenderer(ANNOTATION_DESIGN, NothingRenderer.getInstance());
		MARKUP.addAnnotationRenderer(ANNOTATION_CONCEPT, NothingRenderer.getInstance());
		MARKUP.addAnnotationRenderer(ANNOTATION_COMMENT, NothingRenderer.getInstance());
		MARKUP.addAnnotationRenderer(ANNOTATION_SIZE, NothingRenderer.getInstance());
		MARKUP.addAnnotationRenderer(ANNOTATION_FORMAT, NothingRenderer.getInstance());
		MARKUP.addAnnotationRenderer(PackageManager.MASTER_ATTRIBUTE_NAME,
				NothingRenderer.getInstance());
		MARKUP.addAnnotation(Rdf2GoCore.GLOBAL, false, "true", "false");
		MARKUP.addAnnotationRenderer(Rdf2GoCore.GLOBAL, NothingRenderer.getInstance());
		MARKUP.addAnnotation(ANNOTATION_RANK_DIR, false, "LR", "RL", "TB", "BT");
		MARKUP.addAnnotationRenderer(ANNOTATION_RANK_DIR, NothingRenderer.getInstance());
		MARKUP.addAnnotationRenderer(ANNOTATION_LANGUAGE, NothingRenderer.getInstance());
		MARKUP.addAnnotationRenderer(ANNOTATION_DOT_APP, NothingRenderer.getInstance());
		MARKUP.addAnnotationRenderer(ANNOTATION_ADD_TO_DOT, NothingRenderer.getInstance());
		MARKUP.addAnnotationRenderer(ANNOTATION_RENDERER, NothingRenderer.getInstance());
		MARKUP.addAnnotationRenderer(ANNOTATION_VISUALIZATION, NothingRenderer.getInstance());
		MARKUP.addAnnotation(ANNOTATION_LABELS, false, "true", "false");
		MARKUP.addAnnotation(OntoVisType.ANNOTATION_COLORS, false);
		MARKUP.addAnnotationRenderer(OntoVisType.ANNOTATION_COLORS, NothingRenderer.getInstance());
		MARKUP.addAnnotationRenderer(ANNOTATION_LABELS, NothingRenderer.getInstance());

	}

	public SparqlVisType() {
		super(MARKUP);
		this.setRenderer(new Rdf2GoCoreCheckRenderer());
	}

}
