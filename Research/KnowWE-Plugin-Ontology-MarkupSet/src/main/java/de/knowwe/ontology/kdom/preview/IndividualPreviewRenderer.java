package de.knowwe.ontology.kdom.preview;

import de.knowwe.core.preview.DefaultMarkupPreviewRenderer;
import de.knowwe.core.utils.Scope;

public class IndividualPreviewRenderer extends DefaultMarkupPreviewRenderer {

	public IndividualPreviewRenderer() {
		addPreviewItem(Scope.getScope("IndividualType/ContentType/OntologyLineType"), false);
		addPreviewItem(Scope.getScope("IndividualType/@type"), true);
	}
}
