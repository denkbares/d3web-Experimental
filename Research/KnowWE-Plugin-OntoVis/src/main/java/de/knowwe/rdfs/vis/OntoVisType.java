package de.knowwe.rdfs.vis;

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class OntoVisType extends DefaultMarkupType {

	public static final String ANNOTATION_CONCEPT = "concept";
	public static final String ANNOTATION_COMMENT = "comment";
	public static final String ANNOTATION_SUCCESSORS = "successors";
	public static final String ANNOTATION_PREDECESSORS = "predecessors";
	public static final String ANNOTATION_EXCLUDENODES = "excludeNodes";
	public static final String ANNOTATION_EXCLUDERELATIONS = "excludeRelations";
	public static final String ANNOTATION_SIZE = "size";
	public static final String ANNOTATION_FORMAT = "format";
	public static final String ANNOTATION_SHOWCLASSES = "showClasses";
	public static final String ANNOTATION_SHOWPROPERTIES = "showProperties";
	public static final String ANNOTATION_MASTER = "master";
	public static final String ANNOTATION_LANGUAGE = "language";

	public static final String ANNOTATION_DOT_APP = "dotApp";
	public static final String ANNOTATION_ADD_TO_DOT = "dotAddLine";

	private static final DefaultMarkup MARKUP;

	private enum dot_apps {
		dot, neato
	};

	static {
		MARKUP = new DefaultMarkup("Vis");
		MARKUP.addAnnotation(ANNOTATION_CONCEPT, true);
		MARKUP.addAnnotation(ANNOTATION_COMMENT, false);
		MARKUP.addAnnotation(ANNOTATION_SUCCESSORS, false);
		MARKUP.addAnnotation(ANNOTATION_PREDECESSORS, false);
		MARKUP.addAnnotation(ANNOTATION_EXCLUDENODES, false);
		MARKUP.addAnnotation(ANNOTATION_EXCLUDERELATIONS, false);
		MARKUP.addAnnotation(ANNOTATION_SIZE, false);
		MARKUP.addAnnotation(ANNOTATION_FORMAT, false);
		MARKUP.addAnnotation(ANNOTATION_SHOWCLASSES, false);
		MARKUP.addAnnotation(ANNOTATION_SHOWPROPERTIES, false);
		MARKUP.addAnnotation(ANNOTATION_MASTER, false);
		MARKUP.addAnnotation(ANNOTATION_LANGUAGE, false);
		MARKUP.addAnnotation(ANNOTATION_DOT_APP, false, dot_apps.values());
		MARKUP.addAnnotation(ANNOTATION_ADD_TO_DOT, false);
	}

	public OntoVisType() {
		super(MARKUP);
		this.setIgnorePackageCompile(true);
		this.setRenderer(new OntoVisTypeRenderer());
	}

}
