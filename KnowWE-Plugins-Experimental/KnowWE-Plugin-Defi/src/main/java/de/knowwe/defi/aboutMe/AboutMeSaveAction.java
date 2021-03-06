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
package de.knowwe.defi.aboutMe;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.defi.utils.ReplaceSectionUtils;

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

		boolean isAuthenticated = context.userIsAsserted();

		// Check for user access
		if (!isAuthenticated) {
			context.sendError(403, "You do not have the permission to edit this page.");
			return;
		}

		String title = context.getTitle();
		String username = context.getUserName();
		String web = context.getWeb();

		ArticleManager mgr = KnowWEUtils.getArticleManager(web);
		Section<?> section = mgr.getArticle(title).getRootSection();
		Section<AboutMe> child = Sections.successor(section, AboutMe.class);

		StringBuilder params = new StringBuilder();

		params.append("@" + AboutMe.HTML_AGE + ": ").append(getAnnotationValue(context, AboutMe.HTML_AGE));
		params.append("\n");
		params.append("@" + AboutMe.HTML_CITY + ": ").append(getAnnotationValue(context, AboutMe.HTML_CITY));
		params.append("\n");
		params.append("@" + AboutMe.HTML_PRODUCER + ": ").append(getAnnotationValue(context, AboutMe.HTML_PRODUCER));
		params.append("\n");
		params.append("@" + AboutMe.HTML_TYPE + ": ").append(getAnnotationValue(context, AboutMe.HTML_TYPE));
		params.append("\n");
		params.append("@" + AboutMe.HTML_REASON + ": ").append(getAnnotationValue(context, AboutMe.HTML_REASON));
		params.append("\n");
		params.append("@" + AboutMe.HTML_HOBBIES + ": ").append(getAnnotationValue(context, AboutMe.HTML_HOBBIES));
		params.append("\n");
		params.append("@" + AboutMe.HTML_ABOUT + ": ").append(getAnnotationValue(context, AboutMe.HTML_ABOUT));
		params.append("\n");
		params.append("@" + AboutMe.HTML_AVATAR + ": ").append(getAnnotationValue(context, AboutMe.HTML_AVATAR));
		params.append("\n");

		HashMap<String, String> nodesMap = new HashMap<>();
		nodesMap.put(child.getID(), "%%aboutme\n"
				+ params
				+ "%\n");
		ReplaceSectionUtils.replaceSections(context, nodesMap);

		HttpServletResponse response = context.getResponse();
		response.sendRedirect("Wiki.jsp?page=" + username);
	}

	private String getAnnotationValue(UserActionContext context, String key) {
		if (context.getParameter(key) != null) {
			return context.getParameter(key);
		}
		return "";
	}
}
