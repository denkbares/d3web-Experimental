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

package de.d3web.we.taghandler;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

public class QuestionSheetHandler extends AbstractHTMLTagHandler {

	public static QuestionSheetHandler instance = null;

	public static QuestionSheetHandler getInstance() {
		if (instance == null) {
			instance = new QuestionSheetHandler();
		}

		return instance;
	}

	public QuestionSheetHandler() {
		super("QuestionSheet");
	}

	@Override
	public String getDescription(UserContext user) {
		return D3webModule.getKwikiBundle_d3web(user).getString("KnowWE.QuestionSheet.description");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> values, String web) {

		Session session = D3webUtils.getSession(topic, user, web);

		KnowledgeBase kb = D3webModule.getKnowledgeBase(web, topic);

		ResourceBundle rb = D3webModule.getKwikiBundle_d3web(user);

		StringBuffer html = new StringBuffer();
		html.append("<div id=\"questionsheet-panel\" class=\"panel\"><h3>"
				+ rb.getString("KnowWE.QuestionSheet.header") + "</h3>");

		if (kb != null) {
			List<Question> questions = kb.getManager().getQuestions();

			html.append("<ul>");
			for (Question question : questions) {
				Object value = question.getInfoStore().getValue(
						BasicProperties.ABSTRACTION_QUESTION);
				if (value != null && value instanceof Boolean && ((Boolean) value).booleanValue()) {
					// dont show abstract questions
					continue;
				}

				String answerstring = "";

				if (session != null && session.getBlackboard().getValue(question) != null) {
					answerstring += " : ";
					answerstring += session.getBlackboard().getValue(question).toString();
					// for (Object object : answers) {
					// answerstring += object.toString()+", ";
					// }
					// answerstring = answerstring.substring(0,
					// answerstring.length()-2);
				}
				String rendered = KnowWEUtils.getRenderedInput(question.getId(),
						question.getName(), kb.getId(), user.getUserName(), "Question",
						question.getName(), "");
				html.append("<li class=\"pointer\"><img src=\"KnowWEExtension/images/arrow_right.png\" border=\"0\"/>"
						+ " " + rendered + answerstring + "</li>\n"); // \n only
				// to
				// avoid
				// hmtl-code
				// being
				// cut
				// by
				// JspWiki
				// (String.length
				// >
				// 10000)
			}
			html.append("</ul>");
		}
		else {
			html.append("<p class=\"box error\">" + rb.getString("KnowWE.QuestionSheet.error")
					+ "</p>");
		}
		html.append("</div>");
		return html.toString();
	}
}
