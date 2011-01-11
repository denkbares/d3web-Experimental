package de.d3web.we.lod.markup;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;

public class IgnoreContentType extends DefaultAbstractKnowWEObjectType {

	public IgnoreContentType() {
		this.sectionFinder = new AllTextFinderTrimmed();
	}
}
