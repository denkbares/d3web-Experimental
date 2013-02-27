package de.knowwe.ontology.kdom.relation;

import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.basicType.LineBreak;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.ontology.kdom.OntologyLineType;

public class RelationType extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("Relation");
		MARKUP.addAnnotation(PackageManager.PACKAGE_ATTRIBUTE_NAME, false);
		OntologyLineType lineType = new OntologyLineType();
		lineType.addChildType(new LineBreak());
		lineType.addChildType(new RelationDefinition());
		MARKUP.addContentType(lineType);
	}

	public RelationType() {
		super(MARKUP);
	}
}
