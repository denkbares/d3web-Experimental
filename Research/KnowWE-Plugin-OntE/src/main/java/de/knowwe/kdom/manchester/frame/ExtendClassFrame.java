package de.knowwe.kdom.manchester.frame;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.manchester.OWLTermRef;

public class ExtendClassFrame extends ClassFrame {

	public static final String KEYWORD = "ExtendClass:";

	public ExtendClassFrame() {
		super();

		// Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		// this.setSectionFinder(new RegexSectionFinder(p, 3));

		this.replaceChildType(new EntityReference(KEYWORD), OWLClassDefinition.class);
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
	public boolean hasClassDefinition(Section<? extends ClassFrame> section) {
		return Sections.successor(section, EntityReference.class) != null;
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
	public Section<? extends Type> getClassDefinition(Section<? extends ClassFrame> section) {
		return Sections.successor(section, OWLTermRef.class);
	}
}