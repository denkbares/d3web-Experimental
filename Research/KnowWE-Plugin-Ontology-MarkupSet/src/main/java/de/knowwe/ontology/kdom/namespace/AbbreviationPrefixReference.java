package de.knowwe.ontology.kdom.namespace;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;

public class AbbreviationPrefixReference extends AbstractType {

	public AbbreviationPrefixReference() {
		this.setSectionFinder(new ConstraintSectionFinder(new RegexSectionFinder("^\\s*\\w+:\\s*"),
				AtMostOneFindingConstraint.getInstance()));
		this.addChildType(new AbbreviationReference());
	}

}
