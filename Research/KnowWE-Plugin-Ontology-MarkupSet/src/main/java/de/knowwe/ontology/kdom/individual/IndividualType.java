package de.knowwe.ontology.kdom.individual;

import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.ontology.kdom.OntologyLineType;

public class IndividualType extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("Individual");
		MARKUP.addAnnotation(PackageManager.PACKAGE_ATTRIBUTE_NAME, false);
		OntologyLineType lineType = new OntologyLineType();
		lineType.addChildType(new IndividualDefinition());
		MARKUP.addContentType(lineType);
	}

	public IndividualType() {
		super(MARKUP);
	}

}
