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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.wikiConnector.WikiConnector;
import de.knowwe.revisions.DateType;

/**
 * 
 * @author grotheer
 * @created 28.03.2013
 */
public class TimelineSelectAction extends AbstractAction {

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
			return getPagesChangedTable(context.getWeb(), rev, date);
		}
		return "failure";
	}

	public static String getPagesChangedTable(String web, String rev, Date date) {
		String dateString = DateType.DATE_FORMAT.format(date);
		WikiConnector wiki = Environment.getInstance().getWikiConnector();
		String result = "Revision " + rev + " selected: " + dateString;
		Collection<String> titles = wiki.getAllArticles(web).keySet();
		HashMap<String, Integer> pageversions = new HashMap<String, Integer>();
		result += "<table><tr><th>Page</th><th>Version</th></tr>";
		for (String title : titles) {
			try {
				int version = wiki.getVersionAtDate(title,
						date);
				String versionString;
				if (version == -1) {
					// version at date d is newest version
					versionString = "No changes since " + dateString;
				}
				else if (version == -2) {
					// page does not exist at date d
					versionString = "Page did not exist at " + dateString;
				}
				else {
					versionString = "<a href=\"" + KnowWEUtils.getURLLink(title, version) + "\">"
							+ Integer.toString(version) + "</a>";
				}
				pageversions.put(title, version);
				result += "<tr><td>" + KnowWEUtils.getLinkHTMLToArticle(title) + "</td><td>"
						+ versionString + "</td></tr>";
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		result += "</table>";
		return result;
	}

}
