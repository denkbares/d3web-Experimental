package de.knowwe.lod.markup;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.basicType.LineBreak;
import de.knowwe.core.kdom.basicType.LineContent;
import de.knowwe.kdom.sectionFinder.LineSectionFinder;

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