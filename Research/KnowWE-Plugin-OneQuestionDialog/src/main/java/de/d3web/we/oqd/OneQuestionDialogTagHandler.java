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
package de.d3web.we.oqd;

import java.util.Map;
import java.util.ResourceBundle;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.RessourceLoader;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;

/**
 * 
 * @author Florian Ziegler
 * @created 16.08.2010
 */
public class OneQuestionDialogTagHandler extends AbstractHTMLTagHandler {

	public OneQuestionDialogTagHandler() {
		super("onequestiondialog");
		RessourceLoader.getInstance().add("onequestiondialog.js",
				RessourceLoader.RESOURCE_SCRIPT);
		RessourceLoader.getInstance().add("onequestiondialog.css",
				RessourceLoader.RESOURCE_STYLESHEET);
	}

	@Override
	public void renderHTML(String web, String topic, UserContext user, Map<String, String> values, RenderResult result) {
		// D3webKnowledgeService knowledgeService =
		// D3webModule.getAD3webKnowledgeServiceInTopic(
		// web, topic);
		ResourceBundle rb = D3webUtils.getD3webBundle(user.getRequest());

		// if the OQDialog is not in the main article (e.g. LeftMenu),
		// then no KB is found. Set to article.
		String articleName = user.getParameter("page");
		if (!topic.equalsIgnoreCase(articleName)) {
			topic = articleName;
		}

		KnowledgeBase knowledgeServiceInTopic = D3webUtils.getKnowledgeBase(
				web, topic);
		if (knowledgeServiceInTopic == null) {
			result.append(rb.getString("KnowWE.quicki.error"));
			return;
		}

		Session current = OneQuestionDialogUtils.getSession(topic, web, user);

		InterviewObject o = current.getInterview().nextForm().getInterviewObject();

		OneQuestionDialogHistory.getInstance().addInterviewObject(o);

		result.appendHtml("<h3 class=\"oneQuestionDialog\">Dialog</h3>");

		if (o == null) {
			result.appendHtml("<div class=\"oneQuestionDialog\">Keine weiteren Fragen vorhanden</div>");
			return;
		}

		result.appendHtml("<div class=\"oneQuestionDialog\">");
		OneQuestionDialogUtils.createNewForm(o, result);
		result.appendHtml("</div>");

	}
}
