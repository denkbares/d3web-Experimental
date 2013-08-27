package de.knowwe.ontology.kdom.preview;

import de.knowwe.core.preview.DefaultMarkupPreviewRenderer;
import de.knowwe.core.utils.Scope;

public class TurtlePreviewRenderer extends DefaultMarkupPreviewRenderer {

	public TurtlePreviewRenderer() {
		// otherwise show at least defined class and relevant properties
		addPreviewItem(Scope.getScope("TurtleMarkupN3Content/TurtleSubjectSection"), Preview.all);
		addPreviewItem(Scope.getScope("TurtleMarkupN3Content/TurtlePredSentence"),
				Preview.relevantOrAll);

	}
}
