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
package de.knowwe.defi.userlist;

import java.util.Map;

import com.ecyrd.jspwiki.WikiEngine;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.jspwiki.JSPWikiKnowWEConnector;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.defi.aboutMe.AboutMe;

/**
 * Creates a list of all users.
 * 
 * @author dupke
 */
public class UserlistTaghandler extends AbstractTagHandler {

	/**
	 * @param name
	 */
	public UserlistTaghandler() {
		super("userlist");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuilder userlist = new StringBuilder();
		JSPWikiKnowWEConnector wc = new JSPWikiKnowWEConnector(WikiEngine.getInstance(
				KnowWEEnvironment.getInstance().getContext(), null));
		String[] users = wc.getAllUsers();

		userlist.append("<table class='userlist'>");
		for (int i = 0; i < users.length; i++) {
			userlist.append("<tr>");
			userlist.append("<td><img src=\"KnowWEExtension/images/"
					+ getAvatar(users[i])
					+ ".png\" height=\"80px\" width=\"80px\" alt=\"avatar\" /></td>");
			userlist.append("<td>" + users[i] + "<br />- [status] -</td>");
			userlist.append("</tr>");
		}

		userlist.append("</table>");
		return KnowWEUtils.maskHTML(userlist.toString());
	}

	/**
	 * Get the user's avatar
	 * 
	 * @param userName
	 * @return
	 */
	private String getAvatar(String userName) {
		String avatar = "";

		try {
			KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(
				KnowWEEnvironment.DEFAULT_WEB, userName);
			Section<?> s = article.getSection();
			Section<AboutMe> sec = Sections.findSuccessor(s, AboutMe.class);
			avatar = DefaultMarkupType.getAnnotation(sec, "avatar");
		}
		catch (NullPointerException e) {
			// e.printStackTrace();
		}
		if (avatar == null || avatar == "") avatar = "A01";

		return avatar;
	}

}