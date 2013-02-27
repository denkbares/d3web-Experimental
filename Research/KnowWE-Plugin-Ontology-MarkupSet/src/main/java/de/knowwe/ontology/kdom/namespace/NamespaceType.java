package de.knowwe.ontology.kdom.namespace;

import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.basicType.LineBreak;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.ontology.kdom.OntologyLineType;

public class NamespaceType extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("Namespace");
		MARKUP.addAnnotation(PackageManager.PACKAGE_ATTRIBUTE_NAME, false);
		OntologyLineType lineType = new OntologyLineType();
		// remove EndLineComm, its not practical with urls...
		lineType.clearChildrenTypes();
		lineType.addChildType(new LineBreak());
		lineType.addChildType(new NamespaceAbbreviationDefinition());
		MARKUP.addContentType(lineType);
	}

	public NamespaceType() {
		super(MARKUP);
	}

}
