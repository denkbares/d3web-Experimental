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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.basic.SessionBroker;
import de.d3web.we.basic.WikiEnvironment;
import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

public class RemoveD3webKnowledgeServiceAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {
		String baseID = context.getParameter(KnowWEAttributes.KNOWLEDGEBASE_ID);
		if (baseID == null) return "no kbid to remove knowledge service";
		WikiEnvironment env = D3webModule.getDPSE(context.getParameters());
		KnowledgeBase service = env.getKnowledgeBase(baseID);
		if (service == null) return "no service found for id: " + baseID;
		env.removeKnowledgeBase(service);
		for (SessionBroker broker : env.getBrokers()) {
			Session serviceSession = env.createSession(
					service.getId());
			broker.addSession(service.getId(), serviceSession);
		}
		return "done";
	}

}
