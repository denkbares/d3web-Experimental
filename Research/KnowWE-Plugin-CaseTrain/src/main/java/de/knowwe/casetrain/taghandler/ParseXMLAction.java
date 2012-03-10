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
package de.knowwe.casetrain.taghandler;

import java.io.IOException;
import java.util.Collection;
import java.util.ResourceBundle;

import de.knowwe.casetrain.type.MetaData;
import de.knowwe.casetrain.util.XMLUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;

/**
 * Parses the WikiPage to XML via {@link XMLUtils}
 * 
 * @author Johannes Dienst
 * @created 30.05.2011
 */
public class ParseXMLAction extends AbstractAction {

	private final ResourceBundle bundle = ResourceBundle.getBundle("casetrain_messages");

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	/**
	 * 
	 * @created 01.06.2011
	 * @param context
	 * @return
	 */
	private String perform(UserActionContext context) {

		StringBuilder buildi = new StringBuilder();

		String topic = context.getTopic();
		String web = context.getWeb();
		Article article = Environment.getInstance().getArticle(web, topic);
		Section<Article> sec = article.getSection();

		if (Sections.findSuccessor(sec, MetaData.class) == null) {
			buildi.append("<span class=\"error\">" +
					bundle.getString("NO_CASE_PAGE") +
					"</span>");
			return buildi.toString();
		}
		Collection<Message> allmsgs = Messages.getMessagesFromSubtree(article, sec);
		Collection<Message> errors = Messages.getErrors(allmsgs);
		Collection<Message> warnings = Messages.getWarnings(allmsgs);
		Collection<Message> notices = Messages.getNotices(allmsgs);

		if (!errors.isEmpty()) {
			buildi.append("<span class=\"error\">" +
					bundle.getString("ERROR_PARSE") +
					"</span>");
			return buildi.toString();
		}
		else if (!warnings.isEmpty()) {
			buildi.append("<span class=\"warning\">" +
					bundle.getString("WARNING_PARSE") +
					"</span>");
		}
		else if (!notices.isEmpty()) {
			buildi.append("<span class=\"information\">" +
					bundle.getString("NOTICE_PARSE") +
					"</span>");
		}
		else {
			buildi.append("<span class=\"information\">" +
					bundle.getString("CORRECT_PARSE") +
					"</span>");
		}

		XMLUtils.createXML(article, context.getUserName());

		return buildi.toString();
	}
}
