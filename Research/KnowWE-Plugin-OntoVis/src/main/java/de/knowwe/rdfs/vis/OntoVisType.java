package de.knowwe.rdfs.vis;

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class OntoVisType extends DefaultMarkupType {

	public static final String ANNOTATION_CONCEPT = "concept";
	public static final String ANNOTATION_COMMENT = "comment";
	public static final String ANNOTATION_SUCCESSORS = "successors";
	public static final String ANNOTATION_PREDECESSORS = "predecessors";
	public static final String ANNOTATION_EXCLUDEKNODES = "excludeKnodes";
	public static final String ANNOTATION_EXCLUDERELATIONS = "excludeRelations";
	public static final String ANNOTATION_WIDTH = "width";
	public static final String ANNOTATION_HEIGHT = "height";

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("Vis");
		MARKUP.addAnnotation(ANNOTATION_CONCEPT, true);
		MARKUP.addAnnotation(ANNOTATION_COMMENT, false);
		MARKUP.addAnnotation(ANNOTATION_SUCCESSORS, false);
		MARKUP.addAnnotation(ANNOTATION_PREDECESSORS, false);
		MARKUP.addAnnotation(ANNOTATION_EXCLUDEKNODES, false);
		MARKUP.addAnnotation(ANNOTATION_EXCLUDERELATIONS, false);
		MARKUP.addAnnotation(ANNOTATION_WIDTH, false);
		MARKUP.addAnnotation(ANNOTATION_HEIGHT, false);
	}

	public OntoVisType() {
		super(MARKUP);
		this.setIgnorePackageCompile(true);
		this.setRenderer(new OntoVisTypeRenderer());
	}

}
