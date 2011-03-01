package de.d3web.we.lod.markup;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.basic.LineBreak;
import de.d3web.we.kdom.sectionFinder.LineSectionFinder;
import de.knowwe.core.LineContent;

public class NewLine extends AbstractType {

	public NewLine() {
		this(LineContent.getInstance());
	}

	public NewLine(Type lineContent) {
		this.setNumberedType(true);
		childrenTypes.add(new LineBreak());
		childrenTypes.add(lineContent);
		sectionFinder = LineSectionFinder.getInstance();
	}

}