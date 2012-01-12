package de.knowwe.kdom.manchester.types;

import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.rdfs.IRITermRef;


public class PropertyExpression extends IRITermRef {

	public static final String PATTERN = "\\b[a-z][A-Za-z0-9_:]+\\b";

	public PropertyExpression() {
		this.setSectionFinder(new RegexSectionFinder(PATTERN));
	}
}
