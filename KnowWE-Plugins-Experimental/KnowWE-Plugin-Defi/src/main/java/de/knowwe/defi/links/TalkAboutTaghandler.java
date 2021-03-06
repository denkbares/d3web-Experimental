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
package de.knowwe.defi.links;

import java.util.Map;

import com.denkbares.strings.Strings;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;

/**
 * The TalkAboutTaghandler adds a link into any Wiki article. It links to a page
 * containing a forum. For more information on the forum see the
 * KnowWE-Plugin-Comment. Please use the following syntax:
 * 
 * <blockquote> [{KnowWEPlugin talkabout , title=LinkName, subject=SubjectName}]
 * </blockquote>
 * 
 * The title attribute is optional. Only used for the HTML title attribute.
 * 
 * @author smark
 * @created 24.03.2011
 */
public class TalkAboutTaghandler extends AbstractTagHandler {

	public TalkAboutTaghandler() {
		super("talkabout");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {

		StringBuilder talkAbout = new StringBuilder();

		String talkPage = userContext.getUserName() + "_comment_therapist";

		String title = parameters.get("title");
		String subject = parameters.get("subject");

		if (title == null) {
			title = " &raquo; " + talkPage;
		}
		if (subject == null || subject.equals("")) {
			subject = userContext.getTitle();
		}

		talkAbout.append("<a href=\"Wiki.jsp?page=");
		talkAbout.append(Strings.encodeURL(talkPage.trim()));
		talkAbout.append("&amp;talkabout=");

		talkAbout.append(Strings.encodeURL(subject.trim()));
		talkAbout.append("\" title=\"Title:");
		talkAbout.append(title);
		talkAbout.append("\" rel=\"nofollow\">");
		talkAbout.append("Mit Berater dar&uuml;ber sprechen");
		talkAbout.append("</a>");

		result.appendHtml(talkAbout.toString());
	}
}
