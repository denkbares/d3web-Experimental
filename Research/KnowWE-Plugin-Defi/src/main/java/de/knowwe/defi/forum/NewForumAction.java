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

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;

/**
 * Reachable from the ForumMenu. Buildes new Forum.
 * 
 * @author dupke
 */
public class NewForumAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String username = context.getUserName();
		String id = context.getParameter("id");
		String message = context.getParameter("message");
		String title = "";
		GregorianCalendar now = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB);

		String content = "!!! Thema: " + id + "\n<forum>\n" +
				"<box name=\"" + username + "\" date=\"" + sdf.format(now.getTime()) + "\">"
				+ message + "</box>\n</forum>";

		for (int i = 1; i < 10000; i++) {
			title = id + "_forum" + i;
			if (mgr.getArticle(title) == null) {
				KnowWEEnvironment.getInstance().buildAndRegisterArticle(username, content,
						title, KnowWEEnvironment.DEFAULT_WEB);
				KnowWEEnvironment.getInstance().getWikiConnector()
						.createWikiPage(title, content, username);

				break;
			}
		}

		HttpServletResponse response = context.getResponse();
		response.sendRedirect("Wiki.jsp?redirect=" + title);
	}
}
