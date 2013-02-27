package de.knowwe.ontology.kdom.namespace;

import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

public class AbbreviationDefinition extends SimpleDefinition {

	public AbbreviationDefinition() {
		super(TermRegistrationScope.LOCAL, AbbreviationDefinition.class);
		this.setSectionFinder(new ConstraintSectionFinder(
				new RegexSectionFinder("\\w+"),
				AtMostOneFindingConstraint.getInstance()));
		this.setRenderer(new ToolMenuDecoratingRenderer(StyleRenderer.Questionaire));
	}

}