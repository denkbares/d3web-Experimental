package de.knowwe.ontology.kdom.preview;

import de.knowwe.core.preview.DefaultMarkupPreviewRenderer;
import de.knowwe.core.utils.Scope;

public class ClassPreviewRenderer extends DefaultMarkupPreviewRenderer {

	public ClassPreviewRenderer() {
		// otherwise show at least defined class and relevant properties
		addPreviewItem(
				Scope.getScope("ClassType/ContentType/OntologyLineType/AbbreviatedClassDefinition"),
				Preview.all);
		addPreviewItem(Scope.getScope("ClassType/@property"), Preview.relevantOrAll);

	}
}
