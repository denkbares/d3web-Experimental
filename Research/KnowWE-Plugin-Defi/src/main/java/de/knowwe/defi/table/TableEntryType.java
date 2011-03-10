/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.knowwe.defi.table;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.basic.LineBreak;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkup;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.sectionFinder.AllTextSectionFinder;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;

public class TableEntryType extends DefaultMarkupType {

	public TableEntryType(DefaultMarkup markup) {
		super(markup);
		this.setCustomRenderer(new DefaultMarkupRenderer<DefaultMarkupType>(false));
	}

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("Tabellendaten");
		m.addContentType(new TableEntryContentType());
		m.addAnnotation("tableid", true);
	}

	public TableEntryType() {
		super(m);
		this.setCustomRenderer(new DefaultMarkupRenderer<DefaultMarkupType>(false));
	}

	public static List<Section<ContentEntry>> getEntries(Section<TableEntryType> s) {
		List<Section<ContentEntry>> found = new ArrayList<Section<ContentEntry>>();
		Sections.findSuccessorsOfType(s, ContentEntry.class, found);
		return found;
	}

}

class TableEntryContentType extends AbstractType {

	public TableEntryContentType() {
		this.addChildType(new ContentEntry());
		this.setSectionFinder(new AllTextSectionFinder());
	}
}

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