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

package de.knowwe.comment.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import de.d3web.strings.Strings;
import de.d3web.utils.Log;
import de.d3web.we.event.NewCommentEvent;
import de.knowwe.comment.forum.Forum;
import de.knowwe.comment.forum.ForumRenderer;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import com.denkbares.events.EventManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.xml.XMLTail;

/**
 *
 *
 */
public class ForumBoxAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		Map<String, String> map = context.getParameters();

		String text = map.get("data");
		String topic = map.get("ForumArticleTopic");
		String web = context.getWeb();


		boolean canEditPage = Environment.getInstance().getWikiConnector().userCanEditArticle(
				topic, context.getRequest());

		if (canEditPage) {
			if (text != null && !text.isEmpty()) { // don't add an empty box

				// // ISO 8859-1 --> UTF-8
				// Charset iso = Charset.forName("ISO-8859-1");
				// ByteBuffer bb = iso.encode(text);
				// Charset utf8 = Charset.forName("UTF-8");
				// text = utf8.decode(bb).toString();
				//
				// try {
				// text = java.net.URLDecoder.decode(text, "UTF-8");
				// }
				// catch (UnsupportedEncodingException e) {
				// // do nothing!
				// }

				text = Strings.encodeHtml(text);
				text = text.replace("\n", "\\\\ ");

				Article article = Environment.getInstance().getArticle(web, topic);
				if (article == null) {
					topic = Strings.decodeURL(topic);
					article = Environment.getInstance().getArticle(web, topic);
				}
				Section<?> sec = article.getRootSection();
				List<Section<XMLTail>> found = new ArrayList<>();

				String save = "";

				String reply = map.get("reply");
				if (reply != null && !reply.isEmpty()) {

					save = "<box name=\"" + context.getUserName() + "\" date=\""
							+ ForumRenderer.getDate() + "\">" + text + "</box>\n</box>\n";

					sec = Sections.get(reply);
					Sections.successors(sec, XMLTail.class, found);
					sec = article.getRootSection();
				}
				else {
					save = "<box name=\"" + context.getUserName() + "\" date=\""
							+ ForumRenderer.getDate() + "\">" + text + "</box>\n</forum>";

					Sections.successors(Sections.successor(sec, Forum.class),
							XMLTail.class, found);
				}

				if (!found.isEmpty() && !save.isEmpty()) {
					Section<?> changeSec = found.get(found.size() - 1);
					try {
						Sections.replace(context, changeSec.getID(), save);
					}
					catch (IOException e) {
						Log.severe("Error while saving section content", e);
						context.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errow while saving message");
					}
//					changeSec.setText(save);
				}

//				StringBuilder buffi = new StringBuilder();
//				sec.collectTextsFromLeaves(buffi);
//				Environment.getInstance().getWikiConnector().writeArticleToWikiPersistence(
//						topic, buffi.toString(), context);

				// fire new comment event
				EventManager.getInstance().fireEvent(
						new NewCommentEvent(Strings.decodeHtml(text), topic));

				return;

			}

			context.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: no text found!");
		}

		context.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sorry, something went wrong.");
	}

}
