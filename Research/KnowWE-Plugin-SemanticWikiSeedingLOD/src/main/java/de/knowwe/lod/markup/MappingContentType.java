package de.knowwe.lod.markup;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;

public class MappingContentType extends AbstractType {

	public MappingContentType() {
		this.sectionFinder = new AllTextFinderTrimmed();
	}

}
