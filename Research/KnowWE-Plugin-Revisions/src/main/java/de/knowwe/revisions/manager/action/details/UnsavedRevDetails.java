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

import java.util.Date;
import java.util.Map;

import de.knowwe.core.action.UserActionContext;
import de.knowwe.revisions.DateType;
import de.knowwe.revisions.Revision;
import de.knowwe.revisions.manager.RevisionManager;

/**
 * 
 * @author grotheer
 * @created 01.05.2013
 */
public class UnsavedRevDetails extends AbstractWikiRevDetails {

	@Override
	protected String perform(UserActionContext context) {
		Map<String, String> params = context.getParameters();

		if (params.containsKey("date") & params.containsKey("rev") & params.containsKey("comment")
				&& params.containsKey("changed")) {
			Date date = new Date(Long.parseLong(params.get("date")));
			String revName = params.get("rev");
			String comment = params.get("comment");
			boolean changed = Boolean.parseBoolean(params.get("changed"));

			String header = getHeader(date, revName, changed);

			// append page version diff overview
			Revision rev = RevisionManager.getRM(context).getRevision(date);
			return getRevDetailsTable(rev, header, comment, context).toString();
		}
		return SHOW_REVISION_ERROR;
	}

	private static String getHeader(Date date, String revTitle, boolean changed) {
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

		return result;
	}

}
