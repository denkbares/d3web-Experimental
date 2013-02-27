package de.knowwe.ontology.kdom.clazz;

import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.ontology.kdom.OntologyLineType;

public class ClassType extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("Class");
		MARKUP.addAnnotation(PackageManager.PACKAGE_ATTRIBUTE_NAME, false);
		OntologyLineType lineType = new OntologyLineType();
		lineType.addChildType(new ClassDefinition());
		MARKUP.addContentType(lineType);
	}

	public ClassType() {
		super(MARKUP);
	}

}
