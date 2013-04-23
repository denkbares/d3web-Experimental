/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.revisions.manager.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.revisions.DateType;
import de.knowwe.revisions.manager.RevisionManager;

/**
 * 
 * @author grotheer
 * @created 15.04.2013
 */
public class RestoreRevision extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}
	}

	private String perform(UserActionContext context) throws IOException {
		Map<String, String> params = context.getParameters();
		if (params.containsKey("date")) {
			Date date = new Date(Long.parseLong(params.get("date")));
			Date preRestoreTime = new Date();
			String preRestoreMarkup = "%%Revision\n@name = Backup: pre-restoring revision from "
					+ DateType.DATE_FORMAT.format(date)
					+ "\n@date = "
					+ DateType.DATE_FORMAT.format(preRestoreTime) + "\n%";

			Article a = Environment.getInstance().getArticle(context.getWeb(), context.getTitle());
			HashMap<String, String> sectionsMap = new HashMap<String, String>();
			Section<RootType> s = a.getRootSection();
			sectionsMap.put(s.getID(), s.getText().concat(preRestoreMarkup));

			String message = getSectionsToUpdate(sectionsMap, date, context);
			Sections.replaceSections(context, sectionsMap);

			return message;
		}
		return "<p class=\"box error\">Error while restoring revision</p>";
	}

	/**
	 * get all sections to be restored
	 * 
	 * @created 15.04.2013
	 * @param sectionsToUpdate the Map where the new Sections get written into
	 * @param date the date of the restore
	 * @param context
	 * @return String containing message boxes
	 */
	private static String getSectionsToUpdate(HashMap<String, String> sectionsToUpdate, Date date, UserActionContext context) {
		StringBuffer messages = new StringBuffer();

		HashMap<String, Integer> changedPages = RevisionManager.getRM(context).compareWithCurrentState(
				date);
		ArticleManager aman = RevisionManager.getRM(context).getArticleManager(
				date);

		// do not restore the revision page
		changedPages.remove(context.getTitle());

		for (String title : changedPages.keySet()) {
			int version = changedPages.get(title);

			if (version != -1) {
				// page has changes
				Article currentArticle = Environment.getInstance().getArticleManager(
						context.getWeb()).getArticle(title);
				if (version != -2) {
					// page was other version, so restore the old content
					Article oldVersionOfCurrentArticle = aman.getArticle(title);
					Section<RootType> s = oldVersionOfCurrentArticle.getRootSection();
					sectionsToUpdate.put(currentArticle.getRootSection().getID(), s.getText());
					messages.append("<p class=\"box ok\">Version " + version
							+ " restored for article '" + title + "'</p>");
				}
				else {
					// page did not exist, so delete the content, but keep the
					// page
					sectionsToUpdate.put(currentArticle.getRootSection().getID(), "");
					messages.append("<p class=\"box ok\">Article '" + title + "' cleared</p>");
				}
			}
		}
		return messages.toString();
	}

}
