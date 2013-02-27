package de.knowwe.ontology.kdom.individual;

import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

public class IndividualReference extends SimpleReference {

	public IndividualReference() {
		super(TermRegistrationScope.LOCAL, IndividualDefinition.class);
		this.setSectionFinder(new AllTextFinderTrimmed());
		this.setRenderer(new ToolMenuDecoratingRenderer(StyleRenderer.Question));
	}

	@Override
	public String getTermName(Section<? extends SimpleTerm> section) {
		return Strings.trimQuotes(section.getText());
	}

}
