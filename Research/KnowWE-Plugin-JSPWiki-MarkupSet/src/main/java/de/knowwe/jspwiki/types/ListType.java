package de.knowwe.jspwiki.types;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

public class ListType extends AbstractType {

	public ListType() {
		Pattern pattern = Pattern.compile("(^|\n+)((#|\\*).+?)(?=\n[^(#|\\*)])", Pattern.MULTILINE
				+ Pattern.DOTALL);
		this.setSectionFinder(new RegexSectionFinder(pattern));
		this.addChildType(new OrderedListItemType());
		this.addChildType(new UnorderedListItemType());

	}

}
