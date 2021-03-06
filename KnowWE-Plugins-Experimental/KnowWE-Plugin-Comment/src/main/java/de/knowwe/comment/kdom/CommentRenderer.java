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

package de.knowwe.comment.kdom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.knowwe.comment.forum.ForumRenderer;
import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.basicType.PlainText;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.wikiConnector.WikiConnector;
import de.knowwe.kdom.xml.XMLTail;

public class CommentRenderer implements Renderer {

	public static String clean(String s) {

		String toClean = s;

		// characters which have to be replaced in the page-name
		toClean = toClean.replace("ä", "ae");
		toClean = toClean.replace("Ä", "Ae");
		toClean = toClean.replace("ö", "oe");
		toClean = toClean.replace("Ö", "Oe");
		toClean = toClean.replace("ü", "ue");
		toClean = toClean.replace("Ü", "Ue");
		toClean = toClean.replace("ß", "ss");
		toClean = toClean.replace("!", ".");
		toClean = toClean.replace("?", ".");
		toClean = toClean.replace("#", " ");
		toClean = toClean.replace("<", " ");
		toClean = toClean.replace(">", " ");

		return toClean;
	}

	@Override
	public void render(Section<?> sec, UserContext user, RenderResult string) {

		Map<String, String> commentTypes = CommentModule.getCommentTypes();

		StringBuilder toHTML = new StringBuilder();

		try { // check whether WikiEngine is properly started yet

			Environment instance = Environment.getInstance();
			WikiConnector wikiConnector = instance.getWikiConnector();

			String commentTag = Sections.child(
					Sections.child(sec, CommentTypeTag.class), CommentTypeTagName.class).getText();
			String pageName;

			String commentContent = Sections.child(sec, CommentTypeContent.class).getText();

			// split title and content:
			String title = "";
			int i = commentContent.indexOf(":");
			if (i >= 0) {
				title = commentContent.substring(0, i);
				commentContent = commentContent.substring(i + 1);
			}

			Map<String, Integer> ids = CommentModule.getIDs();

			Section<?> idSec = Sections.child(
					Sections.child(sec, CommentTypeTag.class), CommentTypeTagID.class);
			String id = idSec.getText().trim();

			// add ID if not done before:
			if (id.isEmpty()) {

				int newID = ids.get(commentTag);
				Sections.child(idSec, PlainText.class).setText(newID + " ");

				// save id:
				String leaveText = sec.getArticle().getRootSection().collectTextsFromLeaves();

				if (!title.isEmpty()) {
					pageName = clean(title);
				}
				else pageName = commentTag + newID;

				// create new wikipage:
				if (!wikiConnector.doesArticleExist(pageName)) {

					StringBuilder saveContent = new StringBuilder();
					saveContent.append("<forum")
							.append(title == "" ? "" : " name=\"" + title + "\" ")
							.append("ref=\"Wiki.jsp?page=").append(sec.getTitle())
							.append("#").append(pageName.replace(" ", "+"))
							.append("\">\n");
					saveContent.append("<box name=").append(user.getUserName())
							.append("; date=").append(ForumRenderer.getDate())
							.append(">")
							.append(commentContent).append("</box>\n</forum>");

					instance.getWikiConnector().createArticle(pageName, user.getUserName(), saveContent.toString()
					);

				}
				else { // page exists ==> add a new box:

					String save = "<box name=\"" + user.getUserName() + "\"; date=\""
							+ ForumRenderer.getDate()
							+ "\">--> A new comment to this topic on page ["
							+ sec.getTitle() + "]:\\\\ \\\\" + commentContent
							+ "</box>\n</forum>";

					Section<?> forumSec = instance.getArticle(sec.getWeb(), pageName).getRootSection();

					List<Section<XMLTail>> found = new ArrayList<>();
					Sections.successors(forumSec, XMLTail.class, found);

					if (!found.isEmpty()) {
						Section<?> changeSec = found.get(found.size() - 1);
						Sections.child(changeSec, PlainText.class).setText(save);
					}

					StringBuilder buffi = new StringBuilder();
					forumSec.collectTextsFromLeaves(buffi);
					user.getParameters().put(Attributes.WEB, forumSec.getWeb());
					instance.getWikiConnector().writeArticleToWikiPersistence(
							sec.getTitle(), leaveText, user);
				}

				user.getParameters().put(Attributes.WEB, sec.getWeb());
				instance.getWikiConnector().writeArticleToWikiPersistence(
						sec.getTitle(), leaveText, user);
			}
			else {
				int newID = Integer.valueOf(id);

				if (newID >= ids.get(commentTag)) ids.put(commentTag, (newID + 1));

				if (!title.isEmpty()) {
					pageName = clean(title);
				}
				else pageName = commentTag + newID;

				// create new wikipage with failure message caused by changing
				// manually the id
				if (!wikiConnector.doesArticleExist(pageName)) {
					StringBuilder saveContent = new StringBuilder();

					// if you want only one Error-page activate this:
					// pageName="ID_ERROR";

					saveContent.append("<CSS style=\"color:red; font-size:19px\">ATTENTION: Someone changed the id manually!</CSS>\\\\\n\\\\\n");
					saveContent.append("<forum")
							.append(title == "" ? "" : " name=\"" + title + "\" ")
							.append("ref=\"Wiki.jsp?page=").append(sec.getTitle())
							.append("#").append(pageName.replace(" ", "+"))
							.append("\">\n");
					saveContent.append("<box name=System; date=")
							.append(ForumRenderer.getDate())
							.append(">")
							.append(commentContent)
							.append("</box>\n</forum>");
					instance.getWikiConnector().createArticle(pageName, user.getUserName(), saveContent.toString()
					);
				}
			}

			String imageDir = "KnowWEExtension/" + commentTypes.get(commentTag);
			toHTML.append("<a name='")
					.append(pageName.replace(" ", "+"))
					.append("'></a><a target=_blank href=Wiki.jsp?page=")
					.append(pageName.replace(" ", "+"))
					.append("><img src=").append(imageDir)
					.append(" title='").append(commentContent)
					.append("'></a>");
		}
		catch (Exception e) {
			// do nothing if WikiEngine is not properly started yet
		}
		string.appendHtml(toHTML.toString());
	}
}
