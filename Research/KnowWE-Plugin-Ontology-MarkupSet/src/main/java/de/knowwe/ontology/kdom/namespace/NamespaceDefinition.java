package de.knowwe.ontology.kdom.namespace;

import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

public class NamespaceDefinition extends SimpleDefinition {

	public NamespaceDefinition() {
		super(TermRegistrationScope.LOCAL, String.class);
		this.setSectionFinder(new AllTextFinderTrimmed());
		this.setRenderer(new ToolMenuDecoratingRenderer(StyleRenderer.CONTENT));
	}

}