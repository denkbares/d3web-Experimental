package de.d3web.we.lod.markup;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.basic.LineBreak;
import de.d3web.we.kdom.sectionFinder.LineSectionFinder;
import de.knowwe.core.LineContent;

public class NewLine extends DefaultAbstractKnowWEObjectType {

	public NewLine() {
		this(LineContent.getInstance());
	}

	public NewLine(KnowWEObjectType lineContent) {
		this.setNumberedType(true);
		childrenTypes.add(new LineBreak());
		childrenTypes.add(lineContent);
		sectionFinder = LineSectionFinder.getInstance();
	}

}