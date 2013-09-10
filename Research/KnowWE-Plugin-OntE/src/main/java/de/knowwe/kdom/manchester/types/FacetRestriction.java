package de.knowwe.kdom.manchester.types;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;

public class FacetRestriction extends AbstractType {

	public FacetRestriction() {
		this.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(new Facet());
		this.addChildType(new Literal());
	}

	public boolean hasLiteral(Section<FacetRestriction> section) {
		return Sections.findSuccessor(section, Literal.class) != null;
	}

	public Section<Literal> getLiteral(Section<FacetRestriction> section) {
		return Sections.findSuccessor(section, Literal.class);
	}

	public boolean hasFacet(Section<FacetRestriction> section) {
		return Sections.findSuccessor(section, Facet.class) != null;
	}

	public Section<Facet> getFacet(Section<FacetRestriction> section) {
		return Sections.findSuccessor(section, Facet.class);
	}
}
