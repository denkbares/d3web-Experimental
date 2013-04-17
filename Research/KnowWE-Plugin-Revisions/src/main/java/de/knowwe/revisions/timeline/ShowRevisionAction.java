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
package de.knowwe.revisions.timeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.wikiConnector.WikiConnector;
import de.knowwe.revisions.DateType;

/**
 * 
 * @author grotheer
 * @created 28.03.2013
 */
public class ShowRevisionAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}
	}

	private String perform(UserActionContext context) {
		Map<String, String> params = context.getParameters();

		if (params.containsKey("rev") && params.containsKey("date")) {
			String rev = params.get("rev");
			Date date = new Date(Long.parseLong(params.get("date")));
			return getPagesChangedTable(context, rev, date).toString();
		}
		return "<p class=\"box error\">Error while showing revision</p>";
	}

	public static StringBuffer getPagesChangedTable(UserActionContext context, String rev, Date date) {
		String dateString = DateType.DATE_FORMAT.format(date);
		WikiConnector wiki = Environment.getInstance().getWikiConnector();

		Collection<String> titles = wiki.getAllArticles(context.getWeb()).keySet();
		HashMap<String, Integer> pageversions = new HashMap<String, Integer>();

		ArticleManager currentArticleManager = Environment.getInstance().getArticleManager(
				context.getWeb());
		ArticleManager articleManagerAtVersion = new
				ArticleManager(Environment.getInstance(),
						context.getWeb());
		ArrayList<Article> articlesToRegister = new ArrayList<Article>(
				currentArticleManager.getArticles());

		StringBuffer result = new StringBuffer("Revision '" + rev + "' selected:<br/>Representing "
				+ dateString
				+ ".<br/>");
		titles.remove(context.getTitle());
		StringBuffer tableContent = new StringBuffer();

		boolean actionsExist = false;
		for (String title : titles) {
			Article actualVersionOfCurrentArticle = currentArticleManager.getArticle(title);

			// version number at date
			int version = -1;
			try {
				version = wiki.getVersionAtDate(title,
						date);
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			if (version != -1) {
				// page has changes

				articlesToRegister.remove(actualVersionOfCurrentArticle);

				String versionString;
				String compareString = "";

				if (version != -2) {
					// page was other version
					versionString = "was version <a href=\""
							+ KnowWEUtils.getURLLink(title, version)
							+ "\">" + Integer.toString(version) + "</a>. ";
					compareString = "<a href=\"Diff.jsp?page=" + title + "&r2=" + version
							+ "\" title=\"Compare with current revision\""
							// + "onClick=\"compareRev(" + title + ");\""
							+ ">Compare</a>";
					actionsExist = true;
					String oldText = Environment.getInstance().getWikiConnector().getVersion(
							title,
							version);
					Article oldVersionOfCurrentArticle = Article.createArticle(oldText, title,
							context.getWeb());
					articlesToRegister.add(oldVersionOfCurrentArticle);
				}
				else {
					// page did not exist
					versionString = "did not exist";

				}

				pageversions.put(title, version);
				tableContent.append("<tr><td>" + KnowWEUtils.getLinkHTMLToArticle(title)
						+ "</td><td>"
						+ versionString + "</td><td>" + compareString + "</tr>");
			}
		}

		// throw all collected articles in the new article manager
		for (Article a : articlesToRegister) {
			articleManagerAtVersion.registerArticle(a);
		}

		if (tableContent.length() != 0) {
			result.append("<a href=\"#\" onClick=\"restoreRev(" + date.getTime()
					+ ");\">Restore this revision</a>");
			// result.append(" ");
			// result.append("<a href=\"#\" onClick=\"downloadRev(" +
			// date.getTime()
			// + ");\">Download this revision</a>");
			result.append("\n\nDifferences to current revision:");
			result.append("<table><tr><th>Page</th><th>Status</th><th>");
			if (actionsExist) {
				result.append("Actions");
			}
			result.append("</th></tr>");
			result.append(tableContent);
			result.append("</table>");
		}
		else {
			result.append("<p class=\"box info\">No differences to current revision.</p>");
		}
		return result;
	}
}
