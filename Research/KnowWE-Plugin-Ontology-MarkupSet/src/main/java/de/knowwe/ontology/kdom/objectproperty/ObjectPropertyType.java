package de.knowwe.ontology.kdom.objectproperty;

import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.ontology.kdom.OntologyLineType;

public class ObjectPropertyType extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("ObjectProperty");
		MARKUP.addAnnotation(PackageManager.PACKAGE_ATTRIBUTE_NAME, false);
		OntologyLineType lineType = new OntologyLineType();
		lineType.addChildType(new NamespaceObjectPropertyDefinition());
		MARKUP.addContentType(lineType);
	}

	public ObjectPropertyType() {
		super(MARKUP);
		// TODO Auto-generated constructor stub
	}
}
