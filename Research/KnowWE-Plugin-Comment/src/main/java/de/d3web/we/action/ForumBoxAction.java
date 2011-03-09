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

package de.d3web.we.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.basic.PlainText;
import de.d3web.we.kdom.xml.XMLTail;
import de.d3web.we.plugin.forum.Forum;
import de.d3web.we.plugin.forum.ForumRenderer;

public class ForumBoxAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {

		Map<String, String> parameterMap = context.getParameters();

		String text = parameterMap.get("ForumBoxText");
		HttpServletRequest r = context.getRequest();

		String topic = parameterMap.get("ForumArticleTopic");
		String web = context.getWeb();

		String html = "";

		if (KnowWEEnvironment.getInstance().getWikiConnector().userCanEditPage(topic, r)) {

			if (text != null && text.length() > 0) { // don't add an empty box

				// ISO 8859-1 --> UTF-8
				Charset iso = Charset.forName("ISO-8859-1");
				ByteBuffer bb = iso.encode(text);
				Charset utf8 = Charset.forName("UTF-8");
				text = utf8.decode(bb).toString();

				try {
					text = java.net.URLDecoder.decode(text, "UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					// do nothing!
				}

				html = "<table class=wikitable width=95% border=0><tr>\n<th align=\"left\">"
						+ context.getUserName() + "</th>" +
						"<th width=\"150\" align=\"right\">" + ForumRenderer.getDate() + "</th>\n" +
						"</tr>\n<tr>\n<td colspan=\"2\">" + text + "</td>\n</tr>\n</table>";

				text = text.replace("\n", "\\\\ ");

				String save = "<box name=\"" + context.getUserName() + "\"; date=\""
						+ ForumRenderer.getDate() + "\">" + text + "</box>\n</forum>";

				Section<?> sec = KnowWEEnvironment.getInstance().getArticle(web, topic).getSection();

				List<Section<XMLTail>> found = new ArrayList<Section<XMLTail>>();
				Sections.findSuccessorsOfType(Sections.findSuccessor(sec, Forum.class),
						XMLTail.class, found);

				if (found.size() != 0) {
					Section<?> changeSec = found.get(found.size() - 1);
					Sections.findChildOfType(changeSec, PlainText.class).setOriginalText(save);
				}

				StringBuilder buffi = new StringBuilder();
				sec.collectTextsFromLeaves(buffi);
				KnowWEEnvironment.getInstance().getWikiConnector().writeArticleToWikiEnginePersistence(
						topic, buffi.toString(),
						context);

			}
		}

		return html;
	}

}
