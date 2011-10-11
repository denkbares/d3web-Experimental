/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.defi.time;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * @author Jochen
 * 
 * 
 * 
 */

public class TimeTableMarkup extends DefaultMarkupType {

	public TimeTableMarkup(DefaultMarkup markup) {
		super(markup);
		this.setCustomRenderer(new DefaultMarkupRenderer<DefaultMarkupType>(false));
	}

	// @Override
	// public KnowWEDomRenderer getRenderer() {
	// return new KnowWEDomRenderer() {
	// @Override
	// public void render(KnowWEArticle article, Section sec, UserContext user,
	// StringBuilder string) {
	// user.getParameters().put(TableRenderer.QUICK_EDIT_FLAG, "false");
	// DelegateRenderer.getInstance().render(article, sec, user, string);
	// }
	// };
	// }

	public static List<Date> getDates(Section<TimeTableMarkup> s) {
		List<Date> result = new ArrayList<Date>();
		List<Section<DateT>> found = new ArrayList<Section<DateT>>();
		Sections.findSuccessorsOfType(s, DateT.class, found);
		for (Section<DateT> section : found) {
			if (!section.hasErrorInSubtree(s.getArticle())) {
				Date d = null;
				try {
					d = DateT.dateFormat.parse(section.getOriginalText().trim());
				}
				catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (d != null) result.add(d);
			}
		}

		Collections.sort(result);
		return result;
	}

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("Zeitplan");
		m.addContentType(new TimeTableContentType());
	}

	public TimeTableMarkup() {
		super(m);
		this.setCustomRenderer(new DefaultMarkupRenderer<DefaultMarkupType>(false));
	}
}
