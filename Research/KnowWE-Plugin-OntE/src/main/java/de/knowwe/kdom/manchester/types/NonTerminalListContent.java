package de.knowwe.kdom.manchester.types;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;

public class NonTerminalListContent extends AbstractType {

	public NonTerminalListContent() {
		this.setSectionFinder(new AllTextFinderTrimmed());
	}
}
