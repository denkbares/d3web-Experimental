/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.time;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;


/**
 * @author dupke
 * @created 07.02.2013
 */
public class TimeTableTemplateMarkup extends DefaultMarkupType {

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("ZeitplanTemplate");
		m.addContentType(new TimeTableTemplateContentType());
	}

	public TimeTableTemplateMarkup() {
		super(m);
	}

	public TimeTableTemplateMarkup(DefaultMarkup markup) {
		super(markup);
	}

	public static List<Integer> getNumbersOfDays(Section<TimeTableTemplateMarkup> s) {
		List<Integer> result = new ArrayList<Integer>();
		List<Section<NumberOfDaysT>> found = new ArrayList<Section<NumberOfDaysT>>();
		Sections.findSuccessorsOfType(s, NumberOfDaysT.class, found);
		for (Section<NumberOfDaysT> section : found) {
			if (!section.hasErrorInSubtree(s.getArticle())) {
				int i = Integer.parseInt(section.getText().trim());
				result.add(i);
			}
		}

		return result;
	}

}
