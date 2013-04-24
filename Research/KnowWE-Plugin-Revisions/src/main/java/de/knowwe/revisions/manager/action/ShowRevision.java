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

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.revisions.DateType;
import de.knowwe.revisions.DatedRevision;
import de.knowwe.revisions.Revision;
import de.knowwe.revisions.RevisionType;
import de.knowwe.revisions.UploadedRevision;
import de.knowwe.revisions.manager.RevisionManager;

/**
 * This actions shows details for the selected revision or date.
 * 
 * @author grotheer
 * @created 28.03.2013
 */
public class ShowRevision extends AbstractAction {

	private static final String SHOW_REVISION_ERROR = "<p class=\"box error\">Error while showing revision</p>";

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
			String header;

			if (params.containsKey("rev")) {
				String revName = params.get("rev");

				if (params.containsKey("id")) {
					// saved revision
					String id = params.get("id");
					if (id.equals("uploaded")) {
						// uploaded revision
						header = getHeaderForUploadedRevision();
						return header + getUploadedPagesChangedTable(context).toString();
					}
					header = getHeaderForRevision(date, revName, id);
				}
				else if (params.containsKey("comment") && params.containsKey("changed")) {
					// unsaved revision
					boolean changed = Boolean.parseBoolean(params.get("changed"));
					String comment = params.get("comment");
					header = getHeaderForUnsavedRevision(date, revName, comment, changed);
				}
				else {
					return SHOW_REVISION_ERROR;
				}
			}
			else {
				header = getHeaderForDate(date);
			}

			// append page version diff overview
			Revision rev = RevisionManager.getRM(context).getRevision(date);
			return header + getPagesChangedTable(context, rev).toString();
		}
		return SHOW_REVISION_ERROR;
	}

	/**
	 * get all pages which have changed from the specified date until today
	 * 
	 * @created 21.04.2013
	 * @param context
	 * @param date
	 * @return
	 */
	private static StringBuffer getPagesChangedTable(UserActionContext context, Revision rev) {
		StringBuffer tableContent = new StringBuffer();

		boolean actionsExist = appendChangesTableContent(tableContent, (DatedRevision) rev, context);
		return ShowRevision.wrapTableAroundContent(tableContent, actionsExist, rev);
	}

	/**
	 * 
	 * @created 22.04.2013
	 * @param tableContent
	 * @return
	 */
	private static boolean appendChangesTableContent(StringBuffer tableContent, DatedRevision rev, UserActionContext context) {
		HashMap<String, Integer> compareDiff = rev.compareWithCurrentState();

		// remove the current (revision) page,
		// restoring a old version of this page should be avoided
		compareDiff.remove(context.getTitle());

		boolean actionsExist = false;
		for (String title : compareDiff.keySet()) {
			int version = compareDiff.get(title);
			if (version != -1) {
				String versionString;
				String compareString = "";

				if (version != -2) {
					// page was other version
					versionString = "was version <a href=\""
							+ KnowWEUtils.getURLLink(title, version)
							+ "\">" + Integer.toString(version) + "</a>. ";
					compareString = "<a " +
							"title=\"Compare with current revision\""
							+ "onClick=\"showDiff('" + title + "', '" + version + "');\""
							+ ">Show Diff</a>";
					actionsExist = true;
				}
				else {
					// page did not exist
					versionString = "did not exist";
				}

				// append a new row in html table
				tableContent.append("<tr>");
				tableContent.append("<td>" + KnowWEUtils.getLinkHTMLToArticle(title) + "</td>");
				tableContent.append("<td>" + versionString + "</td>");
				tableContent.append("<td>" + compareString + "</td>");
				tableContent.append("<td>");
			}
		}
		return actionsExist;
	}

	private static String getHeaderForDate(Date date) {
		String s = DateType.DATE_FORMAT.format(date);
		s = "<p class=\"box ok\">Date " + s + " selected.</p>";
		return s;
	}

	private static String getHeaderForRevision(Date date, String revTitle, String id) {
		String dateString = DateType.DATE_FORMAT.format(date);

		String result = "<p class=\"box ok\">Revision '" + revTitle
				+ "' selected, which is representing wiki state as of " + dateString + "</p>";

		// try to get revision comment and add it
		@SuppressWarnings("unchecked")
		Section<RevisionType> section = (Section<RevisionType>) Sections.getSection(id);
		if (section != null) {
			// add the revision comment if exists
			String comment = RevisionType.getRevisionComment(section);
			if (comment != null) {
				result += "<h4>Comment:</h4>" + comment + "\n";
			}
		}

		return result;
	}

	private static String getHeaderForUnsavedRevision(Date date, String revTitle, String comment, boolean changed) {
		String dateString = DateType.DATE_FORMAT.format(date);
		String result = "<p class=\"box ok\">Revision '" + revTitle + "' ";

		if (changed) {
			result += ("<b>moved</b>. It is now representing wiki state as of <b>" + dateString
					+ "</b></p>");
		}
		else {
			result += "selected, which is representing wiki state as of " + dateString + "</p>";
		}
		result += "<p class=\"box info\">If necessary, you can <b>drag</b> it to the correct position.</p>";

		// append the provided comment
		if (!comment.isEmpty()) {
			result += "Comment:\n" + comment + "\n";
		}

		return result;
	}

	private static String getHeaderForUploadedRevision() {
		return "<p class=\"box ok\">Uploaded Revision selected.</p>";
	}

	private static StringBuffer getUploadedPagesChangedTable(UserActionContext context) {
		StringBuffer tableContent = new StringBuffer();
		UploadedRevision rev = RevisionManager.getRM(context).getUploadedRevision();

		boolean actionsExist = appendUploadedChangesTableContent(tableContent,
				(UploadedRevision) rev, context);
		return wrapTableAroundContent(tableContent, actionsExist, rev);
	}

	/**
	 * Wrap a html table around the tableContent
	 * 
	 * @created 23.04.2013
	 * @param tableContent
	 * @param actionsExist
	 * @return a StringBuffer containing an html table with tableContent as
	 *         content
	 */
	public static StringBuffer wrapTableAroundContent(StringBuffer tableContent, boolean actionsExist, Revision rev) {
		StringBuffer result = new StringBuffer();

		if (tableContent.length() != 0) {
			result.append("<table border=\"1\" ><tr>");
			// result.append("<colgroup><col width=\"1*\"><col width=\"2*\"></colgroup>");
			result.append("<td valign='top' style='border-right: 1px solid #DDDDDD;'>");
			result.append("<h4>Differences to current revision:</h4>");

			// Beginning of inner Table
			result.append("<table>");
			result.append("<colgroup><col width=\"250\"><col><col></colgroup>");
			// header row
			result.append("<tr><th>Page</th><th>Status</th><th>");
			if (actionsExist) {
				result.append("Actions");
			}
			result.append("</th></tr>");
			// content
			result.append(tableContent);
			result.append("</table>");
			// End of inner Table

			result.append("<h4>Revision Actions:</h4>");
			if (rev instanceof DatedRevision) {
				Date date = ((DatedRevision) rev).getDate();

				result.append("<a onClick=\"restoreRev(" + date.getTime()
						+ ");\">Restore this state</a>");
				result.append(", ");
				result.append("<a onClick=\"downloadRev(" +
						date.getTime()
						+ ");\">Download this revision</a>");
			}
			else if (rev instanceof UploadedRevision) {
				result.append("<a onClick=\"alert('not yet implemented');\">Merge into wiki</a>");
				result.append(", ");
				result.append("<a onClick=\"alert('not yet implemented');\">Restore this state</a>");
			}

			result.append("</td><td valign='top'>");
			result.append("<div id=\"diffdiv\"></div>");
			result.append("</td></tr></table>");
		}
		else {
			result.append("<p class=\"box info\">No differences to current revision.</p>");
		}
		return result;
	}

	/**
	 * 
	 * @created 22.04.2013
	 * @param tableContent
	 * @return
	 */
	private static boolean appendUploadedChangesTableContent(StringBuffer tableContent, UploadedRevision rev, UserActionContext context) {
		HashMap<String, Integer> compareDiff = rev.compareWithCurrentState();

		// remove the current (revision) page,
		// restoring a old version of this page should be avoided
		compareDiff.remove(context.getTitle());

		boolean actionsExist = false;
		for (String title : compareDiff.keySet()) {
			int version = compareDiff.get(title);
			if (version != -1) {
				String versionString;
				String compareString = "";

				if (version == 0) {
					// diff
					versionString = "changed";
					compareString = "<a " +
							"title=\"Compare with wiki revision\""
							+ "onClick=\"showUploadedDiff('" + title + "', '" + version + "');\""
							+ ">Show Diff</a>";
					actionsExist = true;
				}
				else if (version == -2) {
					versionString = "not in upload";
				}
				else if (version == 1) {
					versionString = "not in wiki";
				}
				else {
					versionString = "erorr";
				}

				// append a new row in html table
				tableContent.append("<tr>");
				tableContent.append("<td>" + KnowWEUtils.getLinkHTMLToArticle(title) + "</td>");
				tableContent.append("<td>" + versionString + "</td>");
				tableContent.append("<td>" + compareString + "</td>");
				tableContent.append("<td>");
			}
		}
		return actionsExist;
	}
}
