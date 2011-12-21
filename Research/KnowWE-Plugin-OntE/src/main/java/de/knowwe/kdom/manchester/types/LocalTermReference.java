package de.knowwe.kdom.manchester.types;

import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.rdfs.IRITermRef;

public class LocalTermReference extends IRITermRef {

	public LocalTermReference() {
		this.setSectionFinder(new AllTextFinderTrimmed());
	}

}
