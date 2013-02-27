package de.knowwe.ontology.kdom.individual;

import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

public class IndividualReference extends SimpleReference {

	public IndividualReference() {
		super(TermRegistrationScope.LOCAL, IndividualDefinition.class);
		this.setSectionFinder(new AllTextSectionFinder());
		this.setRenderer(new ToolMenuDecoratingRenderer(StyleRenderer.Question));
	}

}
