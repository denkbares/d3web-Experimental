package de.knowwe.ontology.kdom.objectproperty;

import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

public class ObjectPropertyDefinition extends SimpleDefinition {

	public ObjectPropertyDefinition() {
		super(TermRegistrationScope.LOCAL, ObjectPropertyDefinition.class);
		this.setRenderer(new ToolMenuDecoratingRenderer(StyleRenderer.CHOICE));
		this.setSectionFinder(new AllTextFinderTrimmed());
	}

	@Override
	public String getTermName(Section<? extends SimpleTerm> section) {
		return Strings.trimQuotes(section.getText());
	}
}
