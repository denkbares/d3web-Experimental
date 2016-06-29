package de.knowwe.typevis.markup;

import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class TypeVisType extends DefaultMarkupType {

	public static final String ANNOTATION_CONCEPT = "type";
	public static final String ANNOTATION_COMMENT = "comment";
	public static final String ANNOTATION_SUCCESSORS = "successors";
	public static final String ANNOTATION_EXCLUDENODES = "excludeNodes";
	public static final String ANNOTATION_SIZE = "size";
	public static final String ANNOTATION_FORMAT = "format";
	public static final String ANNOTATION_LANGUAGE = "language";
	public static final String ANNOTATION_OUTGOING_EDGES = "outgoingEdges";

	public static final String ANNOTATION_DOT_APP = "dotApp";
	public static final String ANNOTATION_ADD_TO_DOT = "dotAddLine";

	public static final String ANNOTATION_VISUALIZATION = "visualization";

	private static final DefaultMarkup MARKUP;

	private enum dot_apps {
		dot, neato
	}

	public enum Visualizations {
		wheel, force
	}

	static {
		MARKUP = new DefaultMarkup("TypeVis");
		MARKUP.addAnnotation(ANNOTATION_CONCEPT, true);
		MARKUP.addAnnotation(ANNOTATION_COMMENT, false);
		MARKUP.addAnnotation(ANNOTATION_SUCCESSORS, false);
		MARKUP.addAnnotation(ANNOTATION_EXCLUDENODES, false);
		MARKUP.addAnnotation(ANNOTATION_SIZE, false);
		MARKUP.addAnnotation(ANNOTATION_FORMAT, false);
		MARKUP.addAnnotation(PackageManager.MASTER_ATTRIBUTE_NAME, false);
		MARKUP.addAnnotation(ANNOTATION_LANGUAGE, false);
		MARKUP.addAnnotation(ANNOTATION_DOT_APP, false, dot_apps.class);
		MARKUP.addAnnotation(ANNOTATION_ADD_TO_DOT, false);
		MARKUP.addAnnotation(ANNOTATION_OUTGOING_EDGES, false, "true", "false");
		MARKUP.addAnnotation(ANNOTATION_VISUALIZATION, false, Visualizations.class);
	}

	public TypeVisType() {
		super(MARKUP);
		this.setRenderer(new TypeVisTypeRenderer());
	}

}
