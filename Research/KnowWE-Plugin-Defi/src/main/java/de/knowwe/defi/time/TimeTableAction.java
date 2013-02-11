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
package de.knowwe.defi.time;

import java.io.IOException;
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
import de.knowwe.core.wikiConnector.WikiConnector;
import de.knowwe.defi.utils.ReplaceSectionUtils;

/**
 * 
 * @author dupke
 * @created 06.02.2013
 */
public class TimeTableAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String[] days = context.getParameter("inputs").split(":");
		String timetableContent = createTimeTableTemplate(days);
		ArticleManager mgr = Environment.getInstance().getArticleManager(Environment.DEFAULT_WEB);
		WikiConnector wikiConnector = Environment.getInstance().getWikiConnector();
		String pageName = TimeTableUtilities.getTimeTableTemplatePagename();

		// timetable doesn't exist
		if (!wikiConnector.doesArticleExist(pageName)) {
			wikiConnector.createArticle(pageName, timetableContent, "Defi-system");
			mgr.registerArticle(Article.createArticle(timetableContent,
					pageName, Environment.DEFAULT_WEB));
		}
		// timetable already exists
		else {
			Map<String, String> nodesMap = new HashMap<String, String>();
			Section<RootType> rootSection = Environment.getInstance().getArticle(
					Environment.DEFAULT_WEB, pageName).getRootSection();
			Section<TimeTableTemplateMarkup> templateSec = Sections.findSuccessor(
					rootSection, TimeTableTemplateMarkup.class);
			if (templateSec == null) {
				nodesMap.put(rootSection.getID(), rootSection.getText()
						+ System.getProperty("line.separator") + timetableContent);
			}
			else {
				nodesMap.put(templateSec.getID(), timetableContent);
			}
			ReplaceSectionUtils.replaceSections(context, nodesMap);
		}

		context.getResponse().getWriter().write("Zeitplan wurde erfolgreich geändert.");
	}

	private String createTimeTableTemplate(String[] days) {
		String content = "%%ZeitplanTemplate" + System.getProperty("line.separator");
		for (String number : days) {
			content += number + System.getProperty("line.separator");
		}
		content += "%";

		return content;
	}

}
