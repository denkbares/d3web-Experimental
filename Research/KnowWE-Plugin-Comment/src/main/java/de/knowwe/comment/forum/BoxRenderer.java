/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.knowwe.comment.forum;

import java.util.Map;
import java.util.ResourceBundle;

import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.xml.AbstractXMLType;
import de.knowwe.kdom.xml.XMLHead;

public class BoxRenderer implements Renderer {

	private static BoxRenderer instance = null;

	public static BoxRenderer getInstance() {
		if (instance == null) instance = new BoxRenderer();
		return instance;
	}

	@Override
	public void render(Section<?> sec, UserContext user, RenderResult string) {

		ResourceBundle rb = ResourceBundle.getBundle("Forum_messages");

		String name;
		String date;

		try {
			Map<String, String> boxMap = AbstractXMLType.getAttributes(sec);
			name = boxMap.get("name");
			date = boxMap.get("date");
		}
		catch (NullPointerException n) {
			name = "System";
			date = "-";
		}

		Section<?> contentSec = ForumBox.getContentChild(sec);

		// no empty posts --> return
		if (contentSec == null || contentSec.getText().length() < 1) return;

		if (name == null || date == null) {

			if (name == null) name = user.getUserName();
			if (date == null) date = ForumRenderer.getDate();

			Section<XMLHead> head = Sections.findChildOfType(sec, XMLHead.class);
			head.setText("<box name=\"" + name + "\" date=\"" + date + "\">");

			// save article:
			try {

				StringBuilder buffi = new StringBuilder();
				String topic = sec.getTitle();
				String web = sec.getWeb();

				Environment instance = Environment.getInstance();
				instance.getArticle(web, topic).getRootSection().collectTextsFromLeaves(buffi);
				user.getParameters().put(Attributes.WEB, web);
				user.getParameters().put(Attributes.TOPIC, topic);
				user.getParameters().put(Attributes.USER, user.toString());
				instance.getWikiConnector().writeArticleToWikiPersistence(topic,
						buffi.toString(), user);
			}
			catch (Exception e) {
				// Do nothing if WikiEngine is not properly started yet
			}
		}

		RenderResult ret = new RenderResult(user);
		// cut seconds if there
		if (date.length() == 19) date = date.substring(0, date.length() - 3);

		ret.append("<table class=wikitable width=99% border=0><tr>\n");
		ret.append("<th align=\"left\"><a href='Wiki.jsp?page=" + name + "'>" + name
				+ "</a></th>\n");
		ret.append("<th align=\"right\" width=\"150\">" + date + "</th>\n");
		ret.append("<th align=\"right\" width=\"100\">\n");

		// String link =
		// Environment.getInstance().getWikiConnector().getBaseUrl();
		// link += "/Wiki.jsp?page=" + user.getTopic() + "&amp;reply=" +
		// sec.getID();
		// Nested link not used at the moment

		ret.append("<div class=\"forumbutton\">");
		ret.append("<a class=\"forum-reply\" rel='{\"id\" : \"" + sec.getID()
				+ "\"}' href=\"#message\">");
		ret.append(rb.getString("Forum.button.reply"));
		ret.append("</a>");
		ret.append("</div>");

		ret.append("</th></tr>");
		ret.append("<tr><td colspan=\"3\" id=\"forum-comment-" + sec.getID() + "\">");

		String reply = user.getParameter("reply");
		if (sec.getID().equals(reply)) {
			ForumRenderer.addCommentBox(ret, rb);
		}

		DelegateRenderer.getInstance().render(sec, user, ret);

		ret.append("</td></tr>\n</table>\n");
		string.appendHtml(ret.toStringRaw());
	}

}
