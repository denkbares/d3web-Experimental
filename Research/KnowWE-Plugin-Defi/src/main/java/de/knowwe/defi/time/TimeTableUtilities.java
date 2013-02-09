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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.auth.NoSuchPrincipalException;
import com.ecyrd.jspwiki.auth.user.UserDatabase;

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
		List<Date> dates = new ArrayList<Date>();
		WikiEngine eng = WikiEngine.getInstance(Environment.getInstance().getContext(), null);
		UserDatabase udb = eng.getUserManager().getUserDatabase();
		try {
			Date created = udb.find(user).getCreated();
			// TODO: Zeitzone englisch deutsch: CET <> MEZ, eventuell
			// Userdatabase anpassen, Server: CET, Lokal: MEZ
			if (created == null) return dates;
			created = DateUtils.truncate(created, Calendar.DAY_OF_MONTH);
			for (Integer days : getTimeTableTemplate()) {
				dates.add(DateUtils.addDays(created, days));
			}
		}
		catch (NoSuchPrincipalException e) {
			e.printStackTrace();
		}

		return dates;
	}

	public static List<Integer> getTimeTableTemplate() {
		List<Integer> numOfDays = new ArrayList<Integer>();
		Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
				TimeTableUtilities.TIMETABLE_ARTICLE);

		if (article != null) {
			Section<TimeTableTemplateMarkup> timeTableTemplate = Sections.findSuccessor(
					article.getRootSection(), TimeTableTemplateMarkup.class);
			if (timeTableTemplate != null) {
				numOfDays = TimeTableTemplateMarkup.getNumbersOfDays(timeTableTemplate);
			}
		}

		return numOfDays;
	}
}
