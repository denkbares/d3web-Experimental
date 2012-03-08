/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
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
