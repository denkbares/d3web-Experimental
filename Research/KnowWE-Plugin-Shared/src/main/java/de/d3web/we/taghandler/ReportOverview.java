/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.we.taghandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;

/**
 * The ReportOverview TagHandler renders all {@link Error} and {@link Warning}
 * messages into a wiki page. The rendered list helps to identify
 * errors/warnings in articles.
 * 
 * The Syntax for the TagHandler is:
 * 
 * <pre>
 * %%KnowWEPlugin
 * report = (both|error|warning) [this line is optional]
 * &#064;taghandlername reportoverview
 * %
 * </pre>
 * 
 * The key, value pair specifies witch reports are shown. If the pair is not
 * present all reports are shown as a default. Set to <strong>warning</strong>
 * only messages from type {@link Warning} are shown, set to
 * <strong>error</strong> only {@link Error}.
 * 
 * 
 * @author smark
 * @created 12.10.2010
 */
public class ReportOverview extends AbstractHTMLTagHandler {

	/**
	 * Constructor of the ReportOverview TagHandler. If you want to change the
	 * name used in the wiki page to call this TagHandler simply change the name
	 * in the super call.
	 */
	public ReportOverview() {
		super("reportoverview");
	}

	/**
	 * Renders the result of the TagHandler into the wiki page. In this case it
	 * shows all error and warning reports of all sections of an article.
	 * 
	 * @param topic
	 * @param user
	 * @param value
	 * @param web
	 * @return
	 */
	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> values, String web) {

		String reportType = values.get("report");
		if (reportType == null || reportType == "") {
			reportType = "both";
		}

		StringBuilder html = new StringBuilder();
		StringBuilder errorHTML = new StringBuilder();
		StringBuilder warningHTML = new StringBuilder();

		ArticleManager manager = Environment.getInstance().getArticleManager(web);
		Collection<Article> articles = manager.getArticles();

		for (Article article : articles) {
			Section<Article> root = article.getSection();

			Map<Section<?>, Collection<Message>> errors = new HashMap<Section<?>, Collection<Message>>();
			Map<Section<?>, Collection<Message>> warnings = new HashMap<Section<?>, Collection<Message>>();

			// search messages
			findMessages(root, article, errors, warnings);

			// render messages
			if (errors.size() > 0) {
				renderMessages(errors, errorHTML, article);
			}
			if (warnings.size() > 0) {
				renderMessages(warnings, warningHTML, article);
			}
		}

		if (errorHTML.length() > 0 && !reportType.equals("warning")) {
			html.append("<div class=\"panel\"><h3>Errors Summary</h3><dl>");
			html.append(errorHTML);
			html.append("</dl></div>");
		}
		if (warningHTML.length() > 0 && !reportType.equals("error")) {
			html.append("<div class=\"panel\"><h3>Warnings Summary</h3><dl>");
			html.append(warningHTML);
			html.append("</dl></div>");
		}
		return html.toString();
	}

	/**
	 * Creates the output HTML for a {@link Message}.
	 * 
	 * @created 12.10.2010
	 * @param messages A {@link HashMap} containing the {@link Message} and the
	 *        section the message occurred in.
	 * @param article The {@link Article} containing the erroneous
	 *        {@link Section}
	 * @param result The StringBuilder the verbalized {@link Message} should
	 *        stored in.
	 */
	private void renderMessages(Map<Section<?>, Collection<Message>> messages, StringBuilder result, Article article) {
		if (messages.size() > 0) {

			result.append("<dt><a href=\"Wiki.jsp?page=");
			result.append(article.getTitle()).append("\" class=\"wikipage\">");
			result.append(article.getTitle()).append("</a></dt>\n");

			for (Entry<Section<?>, Collection<Message>> entry : messages.entrySet()) {
				for (Message kdomReportMessage : entry.getValue()) {
					if (kdomReportMessage.getType() == Message.Type.ERROR) {
						result.append("<dd><img src=\"templates/knowweTmps/images/error.gif\" title=\"KnowWEError\" />");
					}
					else {
						result.append("<dd><img src=\"templates/knowweTmps/images/exclamation.gif\" title=\"KnowWEError\" />");
					}
					result.append("<a href=\"Wiki.jsp?page=").append(article.getTitle()).append("#");
					result.append(entry.getKey().getID()).append(
							"\" class=\"wikipage\">");
					result.append(kdomReportMessage.getVerbalization()).append("</a></dd>");
				}
			}
		}
	}

	/**
	 * Searches for all {@link Message} messages in the current article. All
	 * found {@link Error} and {@link Warning} messages are added to the
	 * according StringBuilder.
	 * 
	 * @created 12.10.2010
	 * @param section The root section of an {@link Article}.
	 * @param article The {@link Article} containing erroneous
	 *        {@link Section}'s
	 * @param errors {@link StringBuilder} containing all error messages
	 * @param warnings {@link StringBuilder} containing all warning messages
	 */
	private void findMessages(Section<?> section, Article article,
			Map<Section<?>, Collection<Message>> errors, Map<Section<?>, Collection<Message>> warnings) {

		List<Section<? extends Type>> children = section.getChildren();
		for (Section<?> child : children) {

			errors.put(child, Messages.getErrors(Messages.getMessages(article, child)));
			warnings.put(child, Messages.getWarnings(Messages.getMessages(article, child)));

			findMessages(child, article, errors, warnings);
		}
	}
}
