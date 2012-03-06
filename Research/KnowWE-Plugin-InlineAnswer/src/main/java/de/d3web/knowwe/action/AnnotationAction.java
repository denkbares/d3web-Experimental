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
package de.d3web.knowwe.action;

import java.io.IOException;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.we.basic.SessionProvider;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

/**
 * 
 * Delegates the rendering of an InlineAnnotation to the
 * {@link FindingHTMLWriter}
 * 
 * @author Johannes Dienst
 * @created 09.06.2011
 */
public class AnnotationAction extends AbstractAction {

	private final FindingHTMLWriter questionWriter;

	public AnnotationAction() {
		questionWriter = new FindingHTMLWriter();
	}

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {

		String namespace = context.getParameter(KnowWEAttributes.SEMANO_NAMESPACE);
		String termName = context.getParameter(KnowWEAttributes.SEMANO_TERM_NAME);
		String user = context.getParameter(KnowWEAttributes.USER);
		String webname = context.getParameter(KnowWEAttributes.WEB);
		String id = context.getParameter(KnowWEAttributes.SEMANO_OBJECT_ID);
		String targetUrlPrefix = context.getParameter("sendToUrl");
		String topic = context.getTitle();
		if (topic == null) {
			topic = namespace.substring(0, namespace.indexOf(".."));
		}

		if (targetUrlPrefix == null) {
			targetUrlPrefix = "KnowWE.jsp";
		}
		if (namespace == null || termName == null) {
			return null;
		}

		namespace = java.net.URLDecoder.decode(namespace);

		SessionProvider provider = SessionProvider.getSessionProvider(context);

		if (id == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();

		// TODO HOTFIX: This Action was not refactored: New Method for getting
		// the Session.
		String fixMe = namespace;
		if (fixMe.contains("..")) fixMe = fixMe.substring(0, fixMe.indexOf(".."));
		KnowledgeBase kb = D3webUtils.getKnowledgeBase(context.getWeb(), fixMe);
		Session session = provider.getSession(kb);

		if (session == null) {
			KnowledgeBase firstKB = D3webUtils.getFirstKnowledgeBase(webname);
			session = provider.getSession(firstKB);
		}

		TerminologyObject obj = session.getKnowledgeBase().getManager().search(id);
		if (obj instanceof Question) {

			if (user != null) {
				sb.append(questionWriter.getHTMLString((Question) obj,
						session,
						namespace, webname, topic, targetUrlPrefix));
			}
			else {
				sb.append(questionWriter.getHTMLString((Question) obj, null,
						namespace, webname, topic, targetUrlPrefix));
			}
		}

		return sb.toString();

	}

}
