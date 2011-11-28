package de.knowwe.kdom.manchester.frame;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.rdfs.IRITermRef;


public class ExtendObjectPropertyFrame extends ObjectPropertyFrame {

	public static final String KEYWORD = "ExtendObjectProperty:";

	public ExtendObjectPropertyFrame() {
		super();

		Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p));

		int pos = this.getAllowedChildrenTypes().indexOf(ObjectPropertyDefinition.getInstance());
		this.removeChildType(ObjectPropertyDefinition.getInstance());
		this.childrenTypes.add(pos, new EntityReference(KEYWORD));
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
	public boolean hasObjectPropertyDefinition(Section<? extends DefaultFrame> section) {
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
	public Section<? extends Type> getObjectPropertyDefinition(Section<? extends DefaultFrame> section) {
		return Sections.findSuccessor(section, IRITermRef.class);
	}
}
