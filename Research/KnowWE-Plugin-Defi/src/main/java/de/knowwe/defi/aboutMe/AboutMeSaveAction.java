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
package de.knowwe.defi.aboutMe;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;

/**
 * The AboutMeSaveAction stores the information provided in the AboutMe-Edit
 * view into the article named after the user's name.
 *
 * @author smark
 * @created 25.01.2011
 */
public class AboutMeSaveAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String title = context.getTopic();

		boolean isAuthenticated = context.userIsAsserted();

		// Check for user access
		if (!isAuthenticated) {
			context.sendError(403, "You do not have the permission to edit this page.");
			return;
		}

		String username = context.getUserName();
		String avatar = context.getParameter(AboutMe.HTMLID_AVATAR);
		String about = context.getParameter(AboutMe.HTMLID_ABOUT);
		String web = context.getWeb();


		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);
		Section<?> section = mgr.getArticle(title).getSection();
		Section<AboutMe> child = Sections.findSuccessor(section, AboutMe.class);

		int aboutFound = 0;
		if (about != null) {
			KnowWEEnvironment.getInstance().getWikiConnector().appendContentToPage(title, about);
			aboutFound = 1;
		}

		HashMap<String, String> nodesMap = new HashMap<String, String>();
		nodesMap.put(child.getID(), "%%aboutme\r\n@avatar: " + avatar
				+ "\r\n@about: " + aboutFound + "\r\n%\r\n");
		mgr.replaceKDOMNodesSaveAndBuild(context, title, nodesMap);

		HttpServletResponse response = context.getResponse();
		response.sendRedirect("Wiki.jsp?page=" + username);
	}
}
