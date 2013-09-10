package de.knowwe.kdom.manchester.types;

import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.kdom.manchester.OWLTermRef;

public class LocalTermReference extends OWLTermRef {

	public LocalTermReference() {
		this.setSectionFinder(new AllTextFinderTrimmed());
	}

}
