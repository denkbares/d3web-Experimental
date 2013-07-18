/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.forum;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletResponse;

import de.d3web.strings.Strings;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

/**
 * 
 * @author dupke
 * @created 25.10.2011
 */
public class PersonalMessageAction extends AbstractAction {

	private static final String FORUM_BUTTON = "Zum Diskussionsforum";
	private static final String BACK_BUTTON = "Zur&uuml;ck zur letzten Seite";

	@Override
	public void execute(UserActionContext context) throws IOException {
		String username = context.getUserName();
		String user1 = context.getParameter("user1");
		String user2 = context.getParameter("user2");
		GregorianCalendar now = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		ArticleManager mgr = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB);

		String topic = "Persoenliche Nachrichten(" + user1 + "," + user2 + ")";
		String name = "Gespräch zwischen " + user1 + " und " + user2;

		if (mgr.getArticle(topic) == null) {
			// Erstelle Forum
			String content = "";
			// links above the forum
			content += "<a class=\"forumLinkLeft\" href=\"\" onclick=\"javascript:location.href = document.referrer;return false;\">"
					+ BACK_BUTTON + "</a>\n";
			content += "<a class=\"forumLinkRight\" href=\"Wiki.jsp?page=Diskussion\">"
					+ FORUM_BUTTON
					+ "</a>\n";
			content += "<div style='clear:both'></div>\n";

			content += "\n<forum topic='" + Strings.encodeHtml(topic) + "' unit='chat' name='"
					+ Strings.encodeHtml(name) + "'>\n" +
					"<box name=\"" + username + "\" date=\"" + sdf.format(now.getTime())
					+ "\"></box>\n</forum>";

			// links under the forum
			content += "\n<br /><br />\n<a class=\"forumLinkLeft\" href=\"\" onclick=\"javascript:location.href = document.referrer;return false;\">"
					+ BACK_BUTTON + "</a>\n";
			content += "<a class=\"forumLinkRight\" href=\"Wiki.jsp?page=Diskussion\">"
					+ FORUM_BUTTON
					+ "</a>\n";
			content += "<div style='clear:both'></div>\n";

			Environment.getInstance().buildAndRegisterArticle(content,
						topic, Environment.DEFAULT_WEB);
			Environment.getInstance().getWikiConnector()
						.createArticle(topic, content, username);
		}

		HttpServletResponse response = context.getResponse();
		response.sendRedirect("Wiki.jsp?redirect=" + Strings.encodeURL(topic));
	}
}
