/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.d3web.debugger.actions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.ActionSetQuestion;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.indication.ActionContraIndication;
import de.d3web.indication.ActionInstantIndication;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.ActionSuppressAnswer;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.we.basic.SessionProvider;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.d3web.debugger.DebugUtilities;
import de.knowwe.d3web.debugger.renderer.DebuggerRuleRenderer;

/**
 * An action to render the debugger's main-part.
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
			context.getWriter().write(Strings.unmaskHTML(renderMain(context)));
		}
	}

	/**
	 * Render the debugger's main-part
	 */
	public String renderMain(UserActionContext context) {
		StringBuffer buffer = new StringBuffer();
		DebuggerRuleRenderer drr = new DebuggerRuleRenderer();
		try {
			// knoweledgebase id
			String kbID = context.getParameter("kbid");
			// question id
			String qid = context.getParameter("qid");
			// solution id
			String sid = context.getParameter("sid");
			// index of selected item in selec-tag
			int selectInd = 0;
			if (context.getParameters().containsKey("selectInd")) selectInd = Integer.parseInt(context.getParameter("selectInd"));
			String rate = "";
			// id of displayed rule
			int ruleid;
			if (context.getParameters().containsKey("ruleid")) ruleid = Integer.parseInt(context.getParameter("ruleid"));
			else ruleid = 0;
			KnowledgeBase kb = D3webUtils.getKnowledgeBase(context.getWeb(), kbID);
			Session session = SessionProvider.getSession(context, kb);
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

				// highlight the solution's state
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
			int select = 0;
			if (iTos.size() == 0) buffer.append("<p>" + noInfluentialRules + "</p>");
			// select-tag
			else {
				buffer.append("<p>"
						+ influentialRules
						+ "<select onChange='KNOWWE.plugin.debuggr.mainSelected(this.options[this.selectedIndex].value);'>");
				buffer.append("<option>" + allElements + "</option>");
				for (TerminologyObject tobj : iTos) {
					select++;
					if (select == selectInd) buffer.append("<option selected='yes'>" + tobj
							+ "</option>");
					else buffer.append("<option>" + tobj + "</option>");
				}
				buffer.append("</select></p>");
			}
			/* divs with rules for the selected element */
			// rules for all elements
			if (selectInd == 0) buffer.append("<ul id='" + allElements
					+ "_rules' style='display:block;'>");
			else buffer.append("<ul id='" + allElements + "_rules' style='display:none;'>");

			for (Rule r : rules) {
				if (ruleid == r.hashCode()) buffer.append("<li class='debuggerMainEntryActive' ");
				else buffer.append("<li class='debuggerMainEntry' ");

				Condition cond = r.getCondition();
				try {
					if (cond.eval(session)) buffer.append("style='border-color:green;color:green;'");
					else buffer.append("style='border-color:red;color:red;'");
				}
				catch (NoAnswerException e) {
					buffer.append("style='border-color:black;color:black;'");
				}
				catch (UnknownAnswerException e) {
					buffer.append("style='border-color:gray;color:gray;'");
				}

				buffer.append(" ruleid='" + r.hashCode() + "' kbid='" + kb.getId() + "'>"
						+ renderAction(r.getAction()) + "</li>");
			}
			buffer.append("</ul>");

			// rules for each element
			select = 0;
			for (TerminologyObject tobj : iTos) {
				select++;
				if (select == selectInd) buffer.append("<ul id='" + tobj
						+ "_rules' style='display:block;'>");
				else buffer.append("<ul id='" + tobj + "_rules' style='display:none;'>");

				for (Rule r : rules) {
					if (!r.getCondition().getTerminalObjects().contains(tobj)) continue;
					if (ruleid == r.hashCode()) buffer.append("<li class='debuggerMainEntryActive' ");
					else buffer.append("<li class='debuggerMainEntry' ");

					Condition cond = r.getCondition();
					try {
						if (cond.eval(session)) buffer.append("style='border-color:green;color:green;'");
						else buffer.append("style='border-color:red;color:red;'");
					}
					catch (NoAnswerException e) {
						buffer.append("style='border-color:black;color:black;'");
					}
					catch (UnknownAnswerException e) {
						buffer.append("style='border-color:gray;color:gray;'");
					}

					buffer.append(" ruleid='" + r.hashCode() + "' kbid='" + kb.getId() + "'>"
							+ renderAction(r.getAction()) + "</li>");
				}
				buffer.append("</ul>");
			}
			buffer.append("<div style='clear:both'></div>");
		}
		catch (NullPointerException e) {
		}

		return buffer.toString();
	}

	/**
	 * Get the rendering for an action.
	 */
	public String renderAction(PSAction action) {
		StringBuffer buffer = new StringBuffer();

		if (action instanceof ActionHeuristicPS) {
			ActionHeuristicPS ac = (ActionHeuristicPS) action;
			buffer.append("<span class='debuggerSolution'>" + ac.getSolution().getName()
					+ "</span> = " + ac.getScore());
		}
		else if (action instanceof ActionContraIndication) {
			buffer.append(action.toString());
		}
		else if (action instanceof ActionSuppressAnswer) {
			buffer.append(action.toString());
		}
		else if (action instanceof ActionInstantIndication) {
			buffer.append(action.toString());
		}
		else if (action instanceof ActionNextQASet) {
			ActionNextQASet anq = (ActionNextQASet) action;
			for (int i = 0; i < anq.getQASets().size(); i++) {
				if (i < anq.getQASets().size() - 1) buffer.append("<span class='debuggerAction'>"
						+ anq.getQASets().get(i).getName() + "</span>, ");
				else buffer.append("<span class='debuggerAction'>"
						+ anq.getQASets().get(i).getName()
						+ "</span>");
			}
		}
		else if (action instanceof ActionSetQuestion) {
			ActionSetQuestion asv = (ActionSetQuestion) action;
			buffer.append("<span class='debuggerAction'>" + asv.getQuestion()
					+ "</span> = <span class='debuggerValue'>"
					+ asv.getValue() + "</span>");
		}
		else {
			buffer.append(action.toString());
		}

		return buffer.toString();
	}

}
