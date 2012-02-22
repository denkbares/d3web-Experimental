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
import java.util.LinkedList;
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
import de.knowwe.d3web.debugger.inference.DebugCondition;


/**
 * 
 * @author dupke
 */
public class DebuggerMainAction extends AbstractAction {

	/** Displayed text */
	private final String noInfluentialRules = "Keine relevanten Regeln gefunden.";
	private final String influentialRules = "Relevante Regeln mit ";
	private final String allElements = "allen Elementen";

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
			for (TerminologyObject tobj : tos) {
				if (tobj.getName().equals(qid)) {
					to = tobj;
					break;
				}
			}
			if (to == null) return "";

			rules = DebugUtilities.getRulesWithTO(to, kb);

			// Get all influential TerminologyObjects
			List<TerminologyObject> iTos = new LinkedList<TerminologyObject>();
			for (Rule r : rules) {
				for (TerminologyObject tobj : r.getCondition().getTerminalObjects()) {
					if (!iTos.contains(tobj)) iTos.add(tobj);
				}
			}

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
			int eval;
			if (iTos.size() == 0) buffer.append("<p>" + noInfluentialRules + "</p>");
			else {
				buffer.append("<p>"
						+ influentialRules
						+ "<select onChange='KNOWWE.plugin.debuggr.mainSelected(this.options[this.selectedIndex].value);'>");
				buffer.append("<option>" + allElements + "</option>");
				for (TerminologyObject tobj : iTos) {
					buffer.append("<option>" + tobj + "</option>");
				}
				buffer.append("</select></p>");
			}
			// Build list for all Rules
			buffer.append("<ul id='" + allElements + "_rules' style='display:block;'>");
			for (Rule r : rules) {
				if (ruleid == r.hashCode()) buffer.append("<li class='debuggerMainEntryActive' ");
				else buffer.append("<li class='debuggerMainEntry' ");

				DebugCondition dc = new DebugCondition(r.getCondition());
				eval = dc.evaluateForRendering(session);
				if (eval == 0) buffer.append("style='border-color:gray;color:gray;'");
				else if (eval == 1) buffer.append("style='border-color:green;color:green;'");
				else if (eval == -1) buffer.append("style='border-color:red;color:red;'");
				else buffer.append("style='border-color:black;color:black;'");

				buffer.append(" ruleid='" + r.hashCode() + "' kbid='"
						+ kb.getId() + "'>" + new DebugAction(r.getAction()).render()
							+ "</li>");
			}
			buffer.append("</ul>");

			// Build lists for Rules with object in condition
			for (TerminologyObject tobj : iTos) {
				buffer.append("<ul id='" + tobj + "_rules' style='display:none;'>");
				for (Rule r : rules) {
					if (!r.getCondition().getTerminalObjects().contains(tobj)) continue;
					if (ruleid == r.hashCode()) buffer.append("<li class='debuggerMainEntryActive' ");
					else buffer.append("<li class='debuggerMainEntry' ");

					DebugCondition dc = new DebugCondition(r.getCondition());
					eval = dc.evaluateForRendering(session);
					if (eval == 0) buffer.append("style='border-color:gray;color:gray;'");
					else if (eval == 1) buffer.append("style='border-color:green;color:green;'");
					else if (eval == -1) buffer.append("style='border-color:red;color:red;'");
					else buffer.append("style='border-color:black;color:black;'");

					buffer.append(" ruleid='" + r.hashCode() + "' kbid='"
							+ kb.getId() + "'>" + new DebugAction(r.getAction()).render()
								+ "</li>");
				}
				buffer.append("</ul>");
			}
			buffer.append("<div style='clear:both'></div>");
		}
		catch (NullPointerException e) {
		}

		return buffer.toString();
	}

}
