package de.knowwe.ontology.kdom.namespace;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.utils.Patterns;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;

public class AbbreviationPrefixReference extends AbstractType {

	public static final String ABBREVIATION_PREFIX_PATTERN = "^\\s*"
			+ Patterns.WORD + ":\\s*";

	public AbbreviationPrefixReference() {
		this.setSectionFinder(new ConstraintSectionFinder(new RegexSectionFinder(
				ABBREVIATION_PREFIX_PATTERN),
				AtMostOneFindingConstraint.getInstance()));
		this.addChildType(new AbbreviationReference());
	}

}
