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
import java.util.Date;
import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;

/**
 * 
 * @author dupke
 * @created 05.02.2013
 */
public class TimeTableUtilities {

	public final static String TIMETABLE_ARTICLE = "Zeitplan";

	public static List<Date> getTimeTable(String user) {
		// TODO: Zeitplan individuell nach user holen
		List<Date> dates = new ArrayList<Date>();
		Article zeitplanArticle = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB).getArticle(TIMETABLE_ARTICLE);

		if (zeitplanArticle != null) {
			Section<TimeTableMarkup> timetable = Sections.findSuccessor(
					zeitplanArticle.getRootSection(), TimeTableMarkup.class);
			if (timetable != null) {
				dates = TimeTableMarkup.getDates(timetable);
			}
		}

		return dates;
	}
}
