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
package de.knowwe.revisions.manager.action.details;

import java.io.IOException;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.revisions.Revision;

/**
 * This actions shows details for the selected revision or date.
 * 
 * @author grotheer
 * @created 28.03.2013
 */
public abstract class AbstractRevDetails extends AbstractAction {

	protected static final String SHOW_REVISION_ERROR = "<p class=\"box error\">Error while showing revision</p>";

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}
	}

	protected abstract String perform(UserActionContext context);

	protected abstract String getPageDiffOverview(Revision rev, UserActionContext context);

	protected abstract void appendRevisionActions(Revision rev, StringBuffer result);

	/**
	 * Wrap a html table around the tableContent
	 * 
	 * @created 23.04.2013
	 * @param tableContent
	 * @param context
	 * @param actionsExist
	 * @return a StringBuffer containing an html table with tableContent as
	 *         content
	 */
	protected StringBuffer getRevDetailsTable(Revision rev, String header, String comment, UserActionContext context) {
		StringBuffer result = new StringBuffer();

		String pageDiffOverview = getPageDiffOverview(rev, context);

		result.append(header);
		if (!pageDiffOverview.isEmpty()) {
			result.append("<table border=\"1\" ><tr>");
			result.append("<colgroup><col width=\"400px\"><col></colgroup>");

			result.append("<td valign='top' style='border-right: 1px solid #DDDDDD;'>");

			result.append("<h4>Differences to current revision:</h4>");

			result.append("<div>");
			result.append(pageDiffOverview);
			result.append("</div>");

			result.append("</td>");
			result.append("<td valign='top'>");
			result.append("<h4>Revision actions:</h4>");
			appendRevisionActions(rev, result);

			result.append(formatComment(comment));
			result.append("</td></tr></table>");

			result.append("<div id=\"diffdiv\"></div>");
		}
		else {
			result.append(formatComment(comment));
			result.append("<p class=\"box info\">No differences to current revision.</p>");

			result.append("<h4>Revision actions:</h4>");
			appendRevisionActions(rev, result);
		}
		return result;
	}

	private static String formatComment(String comment) {
		if (comment != null) {
			if (!comment.isEmpty()) {
				return "<h4>Comment:</h4>" + comment + "\n";
			}
		}
		return "";
	}
}
