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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.xml.AbstractXMLType;

public class ForumRenderer implements Renderer {

	private static boolean sortUpwards = ResourceBundle.getBundle("Forum_config").getString(
			"upwards").equals("true");

	public static String getDate() {

		Date d = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat(
				ResourceBundle.getBundle("Forum_config").getString("timeformat"));
		return fmt.format(d);
	}

	public static void setSortUpwards(boolean sortUp) {
		sortUpwards = sortUp;
	}

	public static void setSortUpwards(String sortParameter) {
		if (sortParameter == null) { // sort-parameter doesn't exist

			sortUpwards = ResourceBundle.getBundle("Forum_config").getString("upwards").equals(
					"true");

		}
		else if (sortParameter.equals("up")) {
			sortUpwards = true;
		}
		else if (sortParameter.equals("down")) {
			sortUpwards = false;

		}
		else { // sort-parameter is set in the wrong way

			sortUpwards = ResourceBundle.getBundle("Forum_config").getString("upwards").equals(
					"true");
		}
	}

	public static boolean getSortUpwards() {
		return sortUpwards;
	}

	public static void sortUpwards() {
		setSortUpwards(true);
	}

	public static void sortDownwards() {
		setSortUpwards(false);
	}

	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder string) {

		StringBuilder ret = new StringBuilder();

		ret.append("<link rel=stylesheet type=text/css href=KnowWEExtension/css/forum.css>\n");
		ret.append("<script type=text/javascript src=KnowWEExtension/scripts/ForumPlugin.js></script>\n");

		ResourceBundle rb = ResourceBundle.getBundle("Forum_messages");
		Map<String, String> forumMap = AbstractXMLType.getAttributeMapFor(sec);
		String topic = sec.getTitle();

		String reply = user.getParameter("reply");

		// load sort-parameter from URL:
		setSortUpwards(user.getParameter("sort"));

		// back link:
		String link = forumMap.get("ref");
		if (link != null) {
			ret.append("<a href=" + link + "><< back</a><br /><br />\n");
		}

		String title = forumMap.get("name");
		if (title != null && !topic.equals(title)) {
			ret.append("<h2>" + title + "</h2><hr />\n");
		}

		List<Section<ForumBox>> contentSectionList = Sections.findChildrenOfType(sec,
				ForumBox.class);

		if (!contentSectionList.isEmpty()) {
			boolean canEditPage = true;

			// append sort links / images
			ret.append("<div id=\"knowwe-plugin-comment-sorting\" style=\"float:right;padding-right:30px;\">\n");
			// ret.append(this.sortLink("down", topic, rb));
			// ret.append(this.sortLink("up", topic, rb));
			ret.append("</div>\n");

			// render posts
			if (sortUpwards) {
				for (int i = 0; i < contentSectionList.size(); i++) {
					Section<?> sectionI = contentSectionList.get(i);
					sectionI.get().getRenderer().render(sectionI, user, ret);
				}
				ret.append("<div id=newBox></div>");
			}
			else {
				ret.append("<div id=newBox></div>");
				for (int i = contentSectionList.size() - 1; i >= 0; i--) {
					Section<?> sectionI = contentSectionList.get(i);
					sectionI.get().getRenderer().render(sectionI, user, ret);
				}
			}

			// if user can edit page add comment box
			if (canEditPage && !(reply != null && reply != "")) {
				addCommentBox(ret, rb);
			}
		}
		string.append(KnowWEUtils.maskHTML(ret.toString()));
	}

	/**
	 * 
	 * 
	 * @created 11.03.2011
	 * @return
	 */
	public static void addCommentBox(StringBuilder builder, ResourceBundle rb) {
		builder.append("<div class='forumTextareaContainer' style=\"clear:both;float:right;padding-right: 30px;\"><a name='message'></a>\n");
		builder.append("<textarea id=\"knowwe-plugin-comment\" name=\"knowwe-plugin-comment\" cols=\"60\" rows=\"8\"></textarea>\n");
		builder.append("<div class=\"forumbutton\" id=\"forum-button-save\" onclick=\"javascript:KNOWWE.plugin.comment.saveComment();\">");
		builder.append(rb.getString("Forum.button.postMessage"));
		builder.append("</div>");
		builder.append("</div>\n");
	}

}
