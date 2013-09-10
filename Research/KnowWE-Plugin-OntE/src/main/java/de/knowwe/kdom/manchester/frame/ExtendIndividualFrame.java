package de.knowwe.kdom.manchester.frame;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.manchester.OWLTermRef;

public class ExtendIndividualFrame extends IndividualFrame {

	public static final String KEYWORD = "ExtendIndividual:";

	public ExtendIndividualFrame() {
		super();

		// Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		// this.setSectionFinder(new RegexSectionFinder(p));

		this.replaceChildType(new EntityReference(KEYWORD), IndividualDefinition.class);
	}

	/**
	 * Returns the {@link OWLClass} section containing the name of the to define
	 * OWLClass.
	 * 
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	@Override
	public boolean hasIndividualDefinition(Section<? extends DefaultFrame<?>> section) {
		return Sections.findSuccessor(section, EntityReference.class) != null;
	}

	/**
	 * Returns the {@link OWLClass} section containing the name of the to define
	 * OWLClass.
	 * 
	 * @created 27.09.2011
	 * @param Section<ClassFrame> section
	 * @return The found section
	 */
	@Override
	public Section<? extends Type> getIndividualDefinition(Section<? extends DefaultFrame<?>> section) {
		return Sections.findSuccessor(section, OWLTermRef.class);
	}
}
