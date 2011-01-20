package de.d3web.we.lod.markup;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;

public class MappingContentType extends DefaultAbstractKnowWEObjectType {

	public MappingContentType() {
		this.sectionFinder = new AllTextFinderTrimmed();
	}

}
