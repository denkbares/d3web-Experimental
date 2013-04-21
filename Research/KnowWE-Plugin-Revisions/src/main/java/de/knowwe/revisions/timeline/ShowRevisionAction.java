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
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.wikiConnector.WikiConnector;
import de.knowwe.revisions.DateType;
import de.knowwe.revisions.RevisionType;

/**
 * This actions shows details for the selected revision or date.
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

		if (params.containsKey("date")) {
			Date date = new Date(Long.parseLong(params.get("date")));
			if (params.containsKey("rev")) {
				String rev = params.get("rev");

				if (params.containsKey("id")) {
					String id = params.get("id");
					return getRevisionDetails(context, date, rev, id).toString();
				}
				else if (params.containsKey("comment") && params.containsKey("changed")) {
					boolean changed = Boolean.parseBoolean(params.get("changed"));
					String comment = params.get("comment");
					return getUnsavedRevisionDetails(context, date, rev, comment, changed).toString();
				}
			}
			return getDateDetails(context, date).toString();
		}
		return "<p class=\"box error\">Error while showing revision</p>";
	}

	/**
	 * get all pages which have changed from the specified date until today
	 * 
	 * @created 21.04.2013
	 * @param context
	 * @param date
	 * @return
	 */
	public static StringBuffer getPagesChangedTable(UserActionContext context, Date date) {
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

		StringBuffer result = new StringBuffer();

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
			result.append("<a onClick=\"restoreRev(" + date.getTime()
					+ ");\">Restore this state</a>");
			// result.append(" ");
			// result.append("<a onClick=\"downloadRev(" +
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

	public static StringBuffer getDateDetails(UserActionContext context, Date date) {
		String dateString = DateType.DATE_FORMAT.format(date);
		StringBuffer result = new StringBuffer("<p class=\"box ok\">Date "
				+ dateString + " selected.</p>");
		result.append(getPagesChangedTable(context, date));
		return result;
	}

	public static StringBuffer getRevisionDetails(UserActionContext context, Date date, String rev, String id) {
		String dateString = DateType.DATE_FORMAT.format(date);

		StringBuffer result = new StringBuffer("<p class=\"box ok\">Revision '" + rev
				+ "' selected, which is representing wiki state as of " + dateString + "</p>");

		// try to get revision comment and add it
		@SuppressWarnings("unchecked")
		Section<RevisionType> section = (Section<RevisionType>) Sections.getSection(id);
		if (section != null) {
			// add the revision comment if exists
			String comment = RevisionType.getRevisionComment(section);
			if (comment != null) {
				result.append("Comment:\n" + comment + "\n");
			}
		}

		// append page version diff overview
		result.append(getPagesChangedTable(context, date));
		return result;
	}

	public static StringBuffer getUnsavedRevisionDetails(UserActionContext context, Date date, String rev, String comment, boolean changed) {

		String dateString = DateType.DATE_FORMAT.format(date);

		StringBuffer result = new StringBuffer("<p class=\"box ok\">Revision '" + rev + "' ");

		if (changed) {
			result.append("moved. It is now representing wiki state as of <b>" + dateString
					+ "</b></p>");
		}
		else {
			result.append(("selected, which is representing wiki state as of " + dateString + "</p>"));
		}
		// append the provided comment if not empty
		if (!comment.isEmpty()) {
			result.append("Comment:\n" + comment + "\n");
		}

		// append page version diff overview
		result.append(getPagesChangedTable(context, date));

		return result;
	}
}
