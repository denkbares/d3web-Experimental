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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.Unknown;
import de.d3web.we.basic.SessionProvider;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.event.EventManager;
import de.knowwe.d3web.event.FindingSetEvent;

/**
 * 
 * @author Johannes Dienst
 * @created 21.06.2011
 */
public class RetractFindingAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String result = retractValue(context);
		if (result != null && context.getWriter() != null) {
			context.getWriter().write(result);
		}

	}

	private String retractValue(UserActionContext context) {

		String objectid = context.getParameter(KnowWEAttributes.SEMANO_OBJECT_ID);
		String topic = context.getTitle();
		String user = context.getUserName();
		String web = context.getWeb();

		String namespace = null;
		String term = null;
		try {
			topic = java.net.URLDecoder.decode(topic, "UTF-8");
			namespace = java.net.URLDecoder.decode(
					context.getParameter(KnowWEAttributes.SEMANO_NAMESPACE), "UTF-8");
			term = URLDecoder.decode(context.getParameter(KnowWEAttributes.SEMANO_TERM_NAME),
					"UTF-8");
			if (objectid != null) objectid = URLDecoder.decode(objectid, "UTF-8");
		}
		catch (UnsupportedEncodingException e1) {
			// should not occur
		}
		if (term != null && !term.equalsIgnoreCase("undefined")) {
			objectid = term;
		}

		if (namespace == null || objectid == null) {
			return "null";
		}

		KnowledgeBase kb = D3webUtils.getKnowledgeBase(web, topic);
		SessionProvider provider = SessionProvider.getSessionProvider(context);
		Session session = provider.getSession(kb);
		if (session == null) {
			KnowledgeBase firstKB = D3webUtils.getFirstKnowledgeBase(web);
			session = provider.createSession(firstKB);
		}
		Blackboard blackboard = session.getBlackboard();

		Question question = kb.getManager().searchQuestion(objectid);
		if (question != null) {

			// TODO Use this Code?
			// 6.2011 Johannes
			Unknown unknown = Unknown.getInstance();
			synchronized (session) {
				Fact fact = FactFactory.createUserEnteredFact(question,
						unknown);
				blackboard.addValueFact(fact);
			}
			EventManager.getInstance().fireEvent(
					new FindingSetEvent(question, unknown, namespace, web,
							user));
		}

		return null;
	}

}
