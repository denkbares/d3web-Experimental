package de.knowwe.rdfs.vis.markup;

import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.rendering.NothingRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.renderer.AsynchronRenderer;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.vis.markup.sparql.SparqlVisType;
import de.knowwe.visualization.GraphDataBuilder;

public class OntoVisType extends DefaultMarkupType {

	public static final String ANNOTATION_CONCEPT = "concept";
	public static final String ANNOTATION_COLORS = "colors";
	public static final String ANNOTATION_COMMENT = "comment";
	public static final String ANNOTATION_SUCCESSORS = "successors";
	public static final String ANNOTATION_PREDECESSORS = "predecessors";
	public static final String ANNOTATION_EXCLUDENODES = "excludeNodes";
	public static final String ANNOTATION_EXCLUDERELATIONS = "excludeRelations";
	public static final String ANNOTATION_FILTERCLASSES = "filterClasses";
	public static final String ANNOTATION_FILTERRELATIONS = "filterRelations";
	public static final String ANNOTATION_SIZE = "size";
	public static final String ANNOTATION_WIDTH = "width";
	public static final String ANNOTATION_HEIGHT = "height";
	public static final String ANNOTATION_FORMAT = "format";
	public static final String ANNOTATION_SHOWCLASSES = "showClasses";
	public static final String ANNOTATION_SHOWPROPERTIES = "showProperties";
	public static final String ANNOTATION_LANGUAGE = "language";
	public static final String ANNOTATION_OUTGOING_EDGES = "outgoingEdges";

	public static final String ANNOTATION_DOT_APP = "dotApp";
	public static final String ANNOTATION_ADD_TO_DOT = "dotAddLine";

	public static final String ANNOTATION_RENDERER = "renderer";
	public static final String ANNOTATION_VISUALIZATION = "visualization";

	public static final String ANNOTATION_CONFIG = "config";

	private static final DefaultMarkup MARKUP;

	public enum dot_apps {
		dot, neato
	}

	public enum Visualizations {
		wheel, force
	}

	static {
		MARKUP = new DefaultMarkup("Vis");
		MARKUP.addAnnotation(ANNOTATION_CONCEPT, true);
		MARKUP.addAnnotation(ANNOTATION_COLORS, false);
		MARKUP.addAnnotationRenderer(ANNOTATION_COLORS, NothingRenderer.getInstance());
		MARKUP.addAnnotation(ANNOTATION_COMMENT, false);
		MARKUP.addAnnotation(ANNOTATION_SUCCESSORS, false);
		MARKUP.addAnnotation(ANNOTATION_PREDECESSORS, false);
		MARKUP.addAnnotation(ANNOTATION_EXCLUDENODES, false);
		MARKUP.addAnnotation(ANNOTATION_EXCLUDERELATIONS, false);
		MARKUP.addAnnotation(ANNOTATION_FILTERCLASSES, false);
		MARKUP.addAnnotation(ANNOTATION_FILTERRELATIONS, false);
		MARKUP.addAnnotation(ANNOTATION_SIZE, false);
		MARKUP.addAnnotationRenderer(ANNOTATION_SIZE, NothingRenderer.getInstance());
		MARKUP.addAnnotation(ANNOTATION_WIDTH, false);
		MARKUP.addAnnotationRenderer(ANNOTATION_WIDTH, NothingRenderer.getInstance());
		MARKUP.addAnnotation(ANNOTATION_HEIGHT, false);
		MARKUP.addAnnotationRenderer(ANNOTATION_HEIGHT, NothingRenderer.getInstance());
		MARKUP.addAnnotation(ANNOTATION_FORMAT, false);
		MARKUP.addAnnotation(ANNOTATION_SHOWCLASSES, false, "true", "false");
		MARKUP.addAnnotation(ANNOTATION_SHOWPROPERTIES, false, "true", "false");
		MARKUP.addAnnotation(PackageManager.MASTER_ATTRIBUTE_NAME, false);
		MARKUP.addAnnotation(ANNOTATION_LANGUAGE, false);
		MARKUP.addAnnotation(ANNOTATION_DOT_APP, false, dot_apps.values());
		MARKUP.addAnnotation(ANNOTATION_ADD_TO_DOT, false);
		MARKUP.addAnnotation(ANNOTATION_OUTGOING_EDGES, false, "true", "false");
		MARKUP.addAnnotation(Rdf2GoCore.GLOBAL, false, "true", "false");
		MARKUP.addAnnotationRenderer(Rdf2GoCore.GLOBAL, NothingRenderer.getInstance());
		MARKUP.addAnnotation(ANNOTATION_RENDERER, false, GraphDataBuilder.Renderer.values());
		MARKUP.addAnnotation(ANNOTATION_VISUALIZATION, false, Visualizations.values());
		MARKUP.addAnnotation(SparqlVisType.ANNOTATION_LINK_MODE, false, SparqlVisType.LinkMode.values());
		MARKUP.addAnnotationRenderer(SparqlVisType.ANNOTATION_LINK_MODE, NothingRenderer.getInstance());
		MARKUP.addAnnotation(SparqlVisType.ANNOTATION_RANK_DIR, false, "LR", "RL", "TB", "BT");
		MARKUP.addAnnotationRenderer(SparqlVisType.ANNOTATION_RANK_DIR, NothingRenderer.getInstance());
		MARKUP.addAnnotation(SparqlVisType.ANNOTATION_LABELS, false, "true", "false");
		MARKUP.addAnnotationRenderer(SparqlVisType.ANNOTATION_LABELS, NothingRenderer.getInstance());
		MARKUP.addAnnotation(ANNOTATION_CONFIG, false);
	}

	public OntoVisType() {
		super(MARKUP);
        this.setRenderer(new AsynchronRenderer(new OntoVisTypeRenderer()));
    }

}
