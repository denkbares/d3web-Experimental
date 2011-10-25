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

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.defi.aboutMe.AboutMe;
import de.knowwe.jspwiki.JSPWikiKnowWEConnector;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

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
		String[] activeUsers = wc.getAllActiveUsers();

		userlist.append("<table class='userlist'>");
		for (int i = 0; i < users.length; i++) {
			if (!users[i].startsWith("Patient")) {
			userlist.append("<tr>");
				userlist.append("<td><img src=\"KnowWEExtension/images/avatars/"
						+ getAvatar(users[i])
						+ "\" height=\"80px\" width=\"80px\" alt=\"avatar\" /></td>");
			userlist.append("<td><a href='" + JSPWikiKnowWEConnector.LINK_PREFIX + users[i] + "'>"
					+ users[i]
					+ "</a><br />- " + getStatus(activeUsers, users[i])
					+ " -</td>");
			userlist.append("</tr>");
			}
		}

		userlist.append("</table>");
		return KnowWEUtils.maskHTML(userlist.toString());
	}

	/**
	 * Get the user's avatar.
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
		if (avatar == null || avatar == "") avatar = "1000px-Comic_image_missing.svg.jpg";

		return avatar;
	}

	/**
	 * Get the user's status.
	 * 
	 * @param activeUsers
	 * @param userName
	 * @return
	 */
	private String getStatus(String[] activeUsers, String userName) {

		for (String s : activeUsers) {
			if (s.equals(userName)) return "<span class='online'>online</span>";
		}

		return "<span class='offline'>offline</span>";
	}

}