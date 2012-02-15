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
import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class TableEntryType extends DefaultMarkupType {

	public TableEntryType(DefaultMarkup markup) {
		super(markup);
		this.setRenderer(new DefaultMarkupRenderer<DefaultMarkupType>(false));
	}

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("Tabellendaten");
		m.addContentType(new TableEntryContentType());
		m.addAnnotation("tableid", true);
	}

	public TableEntryType() {
		super(m);
		this.setRenderer(new DefaultMarkupRenderer<DefaultMarkupType>(false));
	}

	public static List<Section<VersionEntry>> getVersionBlocks(Section<TableEntryType> s) {
		List<Section<VersionEntry>> found = new ArrayList<Section<VersionEntry>>();
		Sections.findSuccessorsOfType(s, VersionEntry.class, found);
		return found;
	}

}

class TableEntryContentType extends AbstractType {

	public TableEntryContentType() {
		this.addChildType(new VersionEntry());
		this.setSectionFinder(new AllTextSectionFinder());
	}
}


class VersionEntry extends AbstractType {

	public static List<Section<ContentEntry>> getEntries(Section<VersionEntry> s) {
		List<Section<ContentEntry>> found = new ArrayList<Section<ContentEntry>>();
		Sections.findSuccessorsOfType(s, ContentEntry.class, found);
		return found;
	}

	public VersionEntry() {
		this.setSectionFinder(new RegexSectionFinder(
				"VERSION\\d+\\r?\\n.*?\\r?\\n\\r?\\n",
				Pattern.DOTALL));
		this.addChildType(new VersionEntryData());
	}

	class VersionEntryData extends AbstractType {
		public VersionEntryData() {
			this.setSectionFinder(new RegexSectionFinder(
					"VERSION\\d+\\r?\\n(.*?)\\r?\\n\\r?\\n", Pattern.DOTALL));
			this.addChildType(new ContentEntry());
		}
	}
}
