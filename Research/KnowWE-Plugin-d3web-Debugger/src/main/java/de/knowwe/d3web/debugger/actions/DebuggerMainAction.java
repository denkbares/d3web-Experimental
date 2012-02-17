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
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.we.basic.SessionBroker;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.d3web.debugger.DebugUtilities;
import de.knowwe.d3web.debugger.inference.DebugAction;


/**
 * 
 * @author dupke
 */
public class DebuggerMainAction extends AbstractAction {


	@Override
	public void execute(UserActionContext context) throws IOException {

		if (context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(renderMain(context));
		}
	}

	public String renderMain(UserActionContext context) {
		StringBuffer buffer = new StringBuffer();

		try {
			String kbID = context.getParameter("kbid");
			String qid = context.getParameter("qid");
			String sid = context.getParameter("sid");
			String rate = "";
			int ruleid;
			if (context.getParameters().containsKey("ruleid")) ruleid = Integer.parseInt(context.getParameter("ruleid"));
			else ruleid = 0;
			SessionBroker broker = D3webUtils.getBroker(context.getUserName(), context.getWeb());
			Session session = broker.getSession(kbID);
			KnowledgeBase kb = session.getKnowledgeBase();
			TerminologyObject to = null;
			List<Rule> rules;

			// Search for the given TerminologyObject
			List<? extends TerminologyObject> sos = DebugUtilities.getSolutionsFromKB(kb);
			List<TerminologyObject> tos = DebugUtilities.getAllTOsFromKB(kb);
			tos.addAll(sos);
			for (TerminologyObject too : tos) {
				if (too.getName().equals(qid)) {
					to = too;
					break;
				}
			}
			if (to == null) return "";

			rules = DebugUtilities.getRulesWithTO(to, kb);
			if (sid != "") {
				for (TerminologyObject s : sos) {
					if (sid.equals(s.toString())) {
						rate = session.getBlackboard().getRating((Solution) s).toString();
						break;
					}
				}

				if (rate != "") {
					if (rate.equals("UNCLEAR")) buffer.append("<p style='background-color:"
							+ DebugUtilities.COLOR_UNCLEAR + "'>" + sid + " (UNCLEAR)</p>");
					else if (rate.equals("EXCLUDED")) buffer.append("<p style='background-color:"
							+ DebugUtilities.COLOR_EXCLUDED + "'>" + sid + " (EXCLUDED)</p>");
					else if (rate.equals("SUGGESTED")) buffer.append("<p style='background-color:"
							+ DebugUtilities.COLOR_SUGGESTED + "'>" + sid + " (SUGGESTED)</p>");
					else if (rate.equals("ESTABLISHED")) buffer.append("<p style='background-color:"
							+ DebugUtilities.COLOR_ESTABLISHED + "'>" + sid + " (ESTABLISHED)</p>");
				}
			}
			buffer.append("<ul>");
			for (Rule r : rules) {
				if (ruleid == r.hashCode()) buffer.append("<li class='debuggerMainEntryActive' ");
				else buffer.append("<li class='debuggerMainEntry' ");

				if (r.hasFired(session)) buffer.append("style='border-color:green;color:green;'");
				else buffer.append("style='border-color:black;color:black'");
				buffer.append(" ruleid='" + r.hashCode() + "' kbid='"
						+ kb.getId() + "'>" + new DebugAction(r.getAction()).render()
							+ "</li>");
			}
			buffer.append("</ul>");
			buffer.append("<div style='clear:both'></div>");
		}
		catch (NullPointerException e) {
		}

		return buffer.toString();
	}

}
