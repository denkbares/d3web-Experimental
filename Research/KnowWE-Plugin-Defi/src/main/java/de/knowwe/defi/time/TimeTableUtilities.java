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
import java.util.ResourceBundle;

import org.apache.commons.lang.time.DateUtils;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.auth.NoSuchPrincipalException;
import com.ecyrd.jspwiki.auth.user.UserDatabase;
import com.ecyrd.jspwiki.auth.user.UserProfile;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.wikiConnector.WikiConnector;

/**
 * 
 * @author dupke
 * @created 05.02.2013
 */
public class TimeTableUtilities {

	/**
	 * Get a user's timetable. If not exists, build it.
	 */
	public static List<Date> getTimeTable(String user) {
		List<Date> dates = new ArrayList<Date>();
		Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
				getTimeTablePageForUser(user));

		if (article != null) {
			Section<TimeTableMarkup> timetable = Sections.findSuccessor(
					article.getRootSection(), TimeTableMarkup.class);
			if (timetable != null) {
				dates = TimeTableMarkup.getDates(timetable);
			}
		}
		else {
			WikiEngine eng = WikiEngine.getInstance(Environment.getInstance().getContext(), null);
			UserDatabase udb = eng.getUserManager().getUserDatabase();
			UserProfile userProfile;
			try {
				userProfile = udb.find(user);
				buildPersonalTimeTable(userProfile);

				// try again
				article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
						getTimeTablePageForUser(user));

				if (article != null) {
					Section<TimeTableMarkup> timetable = Sections.findSuccessor(
							article.getRootSection(), TimeTableMarkup.class);
					if (timetable != null) {
						dates = TimeTableMarkup.getDates(timetable);
					}
				}
			}
			catch (NoSuchPrincipalException e) {
				e.printStackTrace();
			}
		}

		return dates;
	}

	/**
	 * Get the timetable template.
	 */
	public static List<Integer> getTimeTableTemplate() {
		List<Integer> numOfDays = new ArrayList<Integer>();
		Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
				getTimeTableTemplatePagename());

		if (article != null) {
			Section<TimeTableTemplateMarkup> timeTableTemplate = Sections.findSuccessor(
					article.getRootSection(), TimeTableTemplateMarkup.class);
			if (timeTableTemplate != null) {
				numOfDays = TimeTableTemplateMarkup.getNumbersOfDays(timeTableTemplate);
			}
		}

		return numOfDays;
	}

	/**
	 * Build an article with a personal timetable for user.
	 */
	public static void buildPersonalTimeTable(UserProfile user) {
		WikiConnector wikiConnector = Environment.getInstance().getWikiConnector();
		ArticleManager mgr = Environment.getInstance().getArticleManager(Environment.DEFAULT_WEB);
		String pageName = getTimeTablePageForUser(user.getFullname());
		String timetable = generatePersonalTimeTableContent(user);

		if (wikiConnector.doesArticleExist(pageName))
			mgr.deleteArticle(mgr.getArticle(pageName));

		wikiConnector.createArticle(pageName, timetable, "Defi-system");
		mgr.registerArticle(Article.createArticle(timetable, pageName, Environment.DEFAULT_WEB));
	}

	/**
	 * Get the timetable template's pagename.
	 */
	public static String getTimeTableTemplatePagename() {
		return ResourceBundle.getBundle("KnowWE_Defi_config").getString(
				"defi.timetable.pagename");
	}

	/**
	 * Get the user's timetable pagename.
	 */
	public static String getTimeTablePageForUser(String user) {
		return user + "_" + getTimeTableTemplatePagename();
	}

	/**
	 * Generate timetable content for user.
	 */
	private static String generatePersonalTimeTableContent(UserProfile user) {
		Date created = user.getCreated();
		String content = "%%Zeitplan" + System.getProperty("line.separator");
		// TODO: Zeitzone englisch deutsch: CET <> MEZ, eventuell
		// Userdatabase anpassen, Server: CET, Lokal: MEZ
		if (created != null) {
			created = DateUtils.truncate(created, Calendar.DAY_OF_MONTH);
			for (Integer days : getTimeTableTemplate()) {
				Date d = DateUtils.addDays(created, days);
				content += DateT.dateFormat.format(d) + System.getProperty("line.separator");
			}
		}
		content += "%";

		return content;
	}
}
