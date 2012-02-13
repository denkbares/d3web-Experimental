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
import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.basicType.PlainText;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.wikiConnector.KnowWEWikiConnector;
import de.knowwe.kdom.xml.XMLTail;

public class CommentRenderer extends KnowWEDomRenderer<CommentType> {

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
	public void render(KnowWEArticle article, Section<CommentType> sec, UserContext user, StringBuilder string) {

		Map<String, String> commentTypes = CommentModule.getCommentTypes();

		StringBuffer toHTML = new StringBuffer();

		try { // check whether WikiEngine is properly started yet

			KnowWEEnvironment instance = KnowWEEnvironment.getInstance();
			KnowWEWikiConnector wikiConnector = instance.getWikiConnector();

			String commentTag = Sections.findChildOfType(
					Sections.findChildOfType(sec, CommentTypeTag.class), CommentTypeTagName.class).getText();
			String pageName;

			String commentContent = Sections.findChildOfType(sec, CommentTypeContent.class).getText();

			// split title and content:
			String title = "";
			int i = commentContent.indexOf(":");
			if (i >= 0) {
				title = commentContent.substring(0, i);
				commentContent = commentContent.substring(i + 1);
			}

			Map<String, Integer> ids = CommentModule.getIDs();

			Section<?> idSec = Sections.findChildOfType(
						Sections.findChildOfType(sec, CommentTypeTag.class), CommentTypeTagID.class);
			String id = idSec.getText().trim();

			// add ID if not done before:
			if (id.isEmpty()) {

				int newID = ids.get(commentTag);
				Sections.findChildOfType(idSec, PlainText.class).setText(newID + " ");

				// save id:
				StringBuilder buffy = new StringBuilder();
				sec.getArticle().getSection().collectTextsFromLeaves(buffy);

				if (title.length() > 0) {
					pageName = clean(title);
				}
				else pageName = commentTag + newID;

				// create new wikipage:
				if (!wikiConnector.doesPageExist(pageName)) {

					StringBuffer saveContent = new StringBuffer();
					saveContent.append("<forum" + (title == "" ? "" : " name=\"" + title + "\" ")
							+ "ref=\"Wiki.jsp?page=" + sec.getTitle() + "#"
							+ pageName.replace(" ", "+") + "\">\n");
					saveContent.append("<box name=" + user.getUserName() + "; date="
							+ ForumRenderer.getDate() + ">" + commentContent + "</box>\n</forum>");

					instance.getWikiConnector().createWikiPage(pageName, saveContent.toString(),
							user.getUserName());

				}
				else { // page exists ==> add a new box:

					String save = "<box name=\"" + user.getUserName() + "\"; date=\""
									+ ForumRenderer.getDate()
							+ "\">--> A new comment to this topic on page ["
									+ sec.getTitle() + "]:\\\\ \\\\" + commentContent
							+ "</box>\n</forum>";

					Section<?> forumSec = instance.getArticle(sec.getWeb(), pageName).getSection();

					List<Section<XMLTail>> found = new ArrayList<Section<XMLTail>>();
					Sections.findSuccessorsOfType(forumSec, XMLTail.class, found);

					if (found.size() != 0) {
						Section<?> changeSec = found.get(found.size() - 1);
						Sections.findChildOfType(changeSec, PlainText.class).setText(save);
					}

					StringBuilder buffi = new StringBuilder();
					forumSec.collectTextsFromLeaves(buffi);
					user.getParameters().put(KnowWEAttributes.WEB, forumSec.getWeb());
					instance.getWikiConnector().writeArticleToWikiEnginePersistence(
							sec.getTitle(), buffy.toString(), user);

				}

				user.getParameters().put(KnowWEAttributes.WEB, sec.getWeb());
				instance.getWikiConnector().writeArticleToWikiEnginePersistence(
						sec.getTitle(), buffy.toString(), user);

			}
			else {
				int newID = Integer.valueOf(id);

				if (newID >= ids.get(commentTag)) ids.put(commentTag, (newID + 1));

				if (title.length() > 0) {
					pageName = clean(title);
				}
				else pageName = commentTag + newID;

				// create new wikipage with failure message caused by changing
				// manually the id
				if (!wikiConnector.doesPageExist(pageName)) {
					StringBuffer saveContent = new StringBuffer();

					// if you want only one Error-page activate this:
					// pageName="ID_ERROR";

					saveContent.append("<CSS style=\"color:red; font-size:19px\">ATTENTION: Someone changed the id manually!</CSS>\\\\\n\\\\\n");
					saveContent.append("<forum" + (title == "" ? "" : " name=\"" + title + "\" ")
							+ "ref=\"Wiki.jsp?page=" + sec.getTitle() + "#"
							+ pageName.replace(" ", "+") + "\">\n");
					saveContent.append("<box name=System; date=" + ForumRenderer.getDate() + ">"
							+ commentContent + "</box>\n</forum>");
					instance.getWikiConnector().createWikiPage(pageName, saveContent.toString(),
							user.getUserName());
				}

			}

			String imageDir = "KnowWEExtension/" + commentTypes.get(commentTag);
			toHTML.append("<a name='" + pageName.replace(" ", "+")
					+ "'></a><a target=_blank href=Wiki.jsp?page=" + pageName.replace(" ", "+")
					+ "><img src=" + imageDir + " title='" + commentContent + "'></a>");

		}
		catch (Exception e) {

			// do nothing if WikiEngine is not properly started yet
		}

		string.append(KnowWEUtils.maskHTML(toHTML.toString()));
	}
}
