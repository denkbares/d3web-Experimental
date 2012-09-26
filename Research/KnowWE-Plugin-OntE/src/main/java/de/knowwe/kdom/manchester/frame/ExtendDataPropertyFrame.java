package de.knowwe.kdom.manchester.frame;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.rdfs.IRITermRef;

public class ExtendDataPropertyFrame extends DataPropertyFrame {

	public static final String KEYWORD = "ExtendDataProperty:";

	public ExtendDataPropertyFrame() {
		super();

		// Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		// this.setSectionFinder(new RegexSectionFinder(p, 3));

		int pos = this.getChildrenTypes().indexOf(DataPropertyDefinition.getInstance());
		this.removeChildType(DataPropertyDefinition.class);
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
	public boolean hasDataPropertyDefinition(Section<DataPropertyFrame> section) {
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
	public Section<? extends Type> getDataPropertyDefinition(Section<DataPropertyFrame> section) {
		return Sections.findSuccessor(section, IRITermRef.class);
	}
}
