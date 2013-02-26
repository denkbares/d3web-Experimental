package de.knowwe.ontology.kdom.namespace;

import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;

public class AbbreviationDefinition extends SimpleDefinition {

	public AbbreviationDefinition() {
		super(TermRegistrationScope.LOCAL, String.class);
		this.setSectionFinder(new ConstraintSectionFinder(
				new RegexSectionFinder("\\w+"),
				AtMostOneFindingConstraint.getInstance()));
	}

}