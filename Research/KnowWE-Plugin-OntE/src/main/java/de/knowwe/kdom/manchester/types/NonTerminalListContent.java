package de.knowwe.kdom.manchester.types;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;

public class NonTerminalListContent extends AbstractType {

	public NonTerminalListContent() {
		this.setSectionFinder(new AllTextFinderTrimmed());
	}
}
