package de.knowwe.jspwiki.types;

import de.knowwe.core.kdom.AbstractType;

public class StyledText extends AbstractType {

	public StyledText() {

		this.addChildType(new LinkType());
		this.addChildType(new BoldType());
		this.addChildType(new ItalicType());
		this.addChildType(new TTType());
		this.addChildType(new VerbatimType());
		this.addChildType(new StrikeThroughType());
		this.addChildType(new OrderedListItemType());
		this.addChildType(new UnorderedListItemType());
		this.addChildType(new HeaderType(HeaderType.REGEX_HEADER2));
		this.addChildType(new HeaderType(HeaderType.REGEX_HEADER3));
		this.addChildType(new ImageType());
		this.addChildType(new TableOfContentsType());
		this.addChildType(new WikiTextType());

	}

}
