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
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.basic.SessionBroker;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.d3webModule.HTMLDialogRenderer;

public class RefreshHTMLDialogAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {

		// TODO find better solution
		String namespace = context.getParameter(KnowWEAttributes.SEMANO_NAMESPACE);

		String parts[] = namespace.split("\\.\\.");
		String topic = parts[0];

		String user = context.getUserName();
		HttpServletRequest request = context.getRequest();
		String web = context.getWeb();

		return callDialogRenderer(topic, user, request, web);
	}

	public static String callDialogRenderer(String topic, String user, HttpServletRequest request, String web) {

		ResourceBundle rb = D3webModule.getKwikiBundle_d3web(request);

		KnowledgeBase knowledgeServiceInTopic = D3webModule.getAD3webKnowledgeServiceInTopic(
				web, topic);
		if (knowledgeServiceInTopic == null) return rb.getString("KnowWE.DialogPane.error");
		String kbid = knowledgeServiceInTopic.getId();
		// String kbid = topic+".."+KnowWEEnvironment.generateDefaultID(topic);

		SessionBroker broker = D3webModule.getBroker(user, web);
		Session session = broker.getServiceSession(kbid);

		if (session != null) {
			return HTMLDialogRenderer.renderDialog(session, web);
		}
		else {
			kbid = KnowWEEnvironment.WIKI_FINDINGS + ".."
					+ KnowWEEnvironment.generateDefaultID(KnowWEEnvironment.WIKI_FINDINGS);
			session = broker.getServiceSession(kbid);
			return HTMLDialogRenderer.renderDialog(session, web);
		}
	}
}
