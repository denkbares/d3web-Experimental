package de.knowwe.kdom.manchester.types;

import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.OWLTermRef;

public class PropertyExpression extends OWLTermRef {

	public static final String PATTERN = "\\b[a-z][A-Za-z0-9_:]+\\b";

	public PropertyExpression() {
		this.setSectionFinder(new RegexSectionFinder(PATTERN));
	}
}
