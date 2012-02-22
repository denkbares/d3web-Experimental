/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.d3web.debugger.actions;

import java.io.IOException;
import java.util.List;

import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.we.basic.SessionBroker;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.d3web.debugger.DebugUtilities;
import de.knowwe.d3web.debugger.inference.DebugCondition;


/**
 * 
 * @author dupke
 */
public class DebuggerRuleAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		if (context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(renderRule(context));
		}
	}

	public String renderRule(UserActionContext context) {
		StringBuffer buffer = new StringBuffer();
		try {
			String title = context.getTitle();
			String web = context.getWeb();
			String kbID = context.getParameter("kbid");
			if (context.getParameter("ruleid") == null) return "";
			int ruleid = Integer.parseInt(context.getParameter("ruleid"));
			SessionBroker broker = D3webUtils.getBroker(context.getUserName(), context.getWeb());
			Session session = broker.getSession(kbID);
			KnowledgeBase kb = session.getKnowledgeBase();
			List<Rule> rules = DebugUtilities.getRulesFromKB(kb);
			DebugCondition dc;

			buffer.append("<span ruleid='" + ruleid + "'>");
			for (Rule r : rules) {
				if (r.hashCode() == ruleid) {
					dc = new DebugCondition(r.getCondition());
					buffer.append(KnowWEUtils.unmaskHTML(dc.render(session, web, title)));
					buffer.append(KnowWEUtils.unmaskHTML("<a class='ruleLink' href='Wiki.jsp?page="
							+ DebugUtilities.getRuleResource(r) + "'></a>"));
					break;
				}
			}
			buffer.append("</span>");
		}
		catch (NullPointerException e) {
		}

		return buffer.toString();
	}

}
