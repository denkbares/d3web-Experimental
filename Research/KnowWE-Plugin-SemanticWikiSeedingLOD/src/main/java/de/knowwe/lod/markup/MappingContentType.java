package de.knowwe.lod.markup;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;

public class MappingContentType extends AbstractType {

	public MappingContentType() {
		this.sectionFinder = new AllTextFinderTrimmed();
	}

}
