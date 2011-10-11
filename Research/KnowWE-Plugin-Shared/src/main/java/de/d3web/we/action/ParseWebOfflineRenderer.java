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
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import de.d3web.report.Message;
import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.utils.KnowWEUtils;

public class ParseWebOfflineRenderer extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {
		String webname = context.getParameter(KnowWEAttributes.WEB);

		ResourceBundle rb = KnowWEEnvironment.getInstance().getKwikiBundle(
				context.getRequest());

		Map<String, String> articles = KnowWEEnvironment.getInstance()
				.getWikiConnector().getAllArticles(webname);
		Set<String> articleNames = articles.keySet();
		StringBuffer reports = new StringBuffer();
		int problems = 0;
		for (String name : articleNames) {
			KnowWEArticle article = KnowWEArticle.createArticle(articles.get(name),
					name, KnowWEEnvironment.getInstance().getRootType(),
					webname, true);
			KnowWEEnvironment.getInstance()
					.getArticleManager(webname).registerArticle(
							article);

			boolean hasErrors = article.getSection().hasErrorInSubtree(article);
			if (!hasErrors) {
				Collection<Message> messages = KnowWEUtils.getMessages(article,
						article.getSection(), Message.class);
				for (Message message : messages) {
					if (message.getMessageType().equals(Message.ERROR)) {
						hasErrors = true;
						break;
					}
				}
			}

			if (hasErrors) {
				reports.append("<p class=\"box error\">");
			}
			else {
				reports.append("<p class=\"box ok\">");
			}
			reports.append(rb.getString("webparser.info.parsing")
					+ createLink(name, webname) + "<br />");
			if (hasErrors) {
				problems++;
				reports.append("<br />\n");
			}
		}

		String converted = KnowWEUtils.convertUmlaut(reports.toString());
		reports.delete(0, reports.length());
		reports.append(converted);

		reports.insert(0, "<a href=\"#\" id='js-parseWeb' class='clear-element'>"
				+ rb.getString("KnowWE.buttons.close") + "</a><br />");

		return reports.toString();

	}

	private String createLink(String topicName, String webname) {

		return "<a href='Wiki.jsp?page=" + topicName + "' target='_blank'>"
				+ topicName + "</a>";
	}

	@Override
	public boolean isAdminAction() {
		return true;
		// return false; //for local testing
	}

}
