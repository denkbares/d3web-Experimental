/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.forum;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletResponse;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

/**
 * Reachable from the ForumMenu. Builds new forum.
 * 
 * @author dupke
 */
public class NewForumAction extends AbstractAction {

	private static final String BACK_BUTTON = "<< zur&uuml;ck zum Diskussionsforum";

	@Override
	public void execute(UserActionContext context) throws IOException {
		String username = context.getUserName();
		// Seitenname = Einheit + Unterseite
		String pageName = context.getParameter("pagename");
		// Thema
		String topic = context.getParameter("topic");
		// Nachricht
		String message = context.getParameter("message");
		String title = "";
		String responseString = "\n";
		GregorianCalendar now = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		ArticleManager mgr = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB);
		String content = "<a href=\"Wiki.jsp?page=Diskussion\">" + BACK_BUTTON + "</a><br />\n";

		if (pageName == "Sonstiges") {
			content += "\n<forum topic='" + topic + "' name='" + topic + "'>\n" +
					"<box name=\"" + username + "\" date=\"" + sdf.format(now.getTime()) + "\">"
					+ message + "</box>\n</forum>";
		}
		else {
			content += "\n<forum topic='" + topic + "' unit='" + pageName + "' name='" + topic
					+ " (" + pageName + ")" + "'>\n" +
					"<box name=\"" + username + "\" date=\"" + sdf.format(now.getTime()) + "\">"
					+ message + "</box>\n</forum>";
		}

		content += "<br /><br />\n<a href=\"Wiki.jsp?page=Diskussion\">" + BACK_BUTTON + "</a>";

		if (pageName == "") title = "Forum zu " + " \"" + topic + "\"";
		else title = "Forum zu " + pageName + " (" + topic + ")";
		if (mgr.getArticle(title) == null) {
			Environment.getInstance().buildAndRegisterArticle(content,
						title, Environment.DEFAULT_WEB);
			Environment.getInstance().getWikiConnector()
						.createWikiPage(title, content, username);
		}
		else {
			responseString = "Ein Forum zu diesem Thema existiert bereits.\n";
		}

		responseString += title;
		HttpServletResponse response = context.getResponse();
		response.getWriter().write(responseString);
	}
}
