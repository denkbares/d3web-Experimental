package de.knowwe.defi.table;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.basicType.LineBreak;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

class ContentEntry extends AbstractType {

	public static int getNumber(Section<ContentEntry> s) {
		Section<InputHead> section = Sections.findChildOfType(s, InputHead.class);
		if (section != null) {
			String numString = section.getText().substring(5,
					section.getText().indexOf(':'));
			return Integer.parseInt(numString);
		}

		return -1;
	}

	public static String getContent(Section<ContentEntry> s) {
		Section<InputContent> section = Sections.findChildOfType(s, InputContent.class);
		if (section != null) {
			return section.getText();
		}

		return null;
	}

	public ContentEntry() {
		this.setSectionFinder(new RegexSectionFinder("INPUT.*?\\r?\\n"));
		this.addChildType(new InputHead());
		this.addChildType(new LineBreak());
		this.addChildType(new InputContent());
	}
}

class InputHead extends AbstractType {

	public InputHead() {
		this.setSectionFinder(new RegexSectionFinder("INPUT\\d*:"));
	}
}

class InputContent extends AbstractType {
	public InputContent() {
		this.setSectionFinder(new AllTextSectionFinder());
	}

}
