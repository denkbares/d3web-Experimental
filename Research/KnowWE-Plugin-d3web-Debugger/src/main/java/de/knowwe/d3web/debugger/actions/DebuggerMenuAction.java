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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.we.basic.SessionProvider;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.d3web.debugger.DebugUtilities;

/**
 * An action to render the debugger's menu.
 * 
 * @author dupke
 */
public class DebuggerMenuAction extends AbstractAction {

	public static final String SOLUTIONS_KEY = "Lösungen";

	@Override
	public void execute(UserActionContext context) throws IOException {
		// knowledgebase id
		String kbID = context.getParameter("kbid");
		// question id
		String qid = context.getParameter("qid");
		KnowledgeBase kb = null;
		Session session = null;
		if (context.getParameters().containsKey("kbID")) {
			kbID = context.getParameter("kbID");
			kb = D3webUtils.getKnowledgeBase(context.getWeb(), kbID);
			session = SessionProvider.getSession(context, kb);
		}
		String result = getMenuRendering(kbID, qid, session);

		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}
	}

	/**
	 * Render the debugger's menu.
	 */
	@SuppressWarnings("unchecked")
	public String getMenuRendering(String kbID, String qid, Session session) {
		StringBuffer buffer = new StringBuffer();
		String rate = "";
		try {
			KnowledgeBase kb = session.getKnowledgeBase();
			TerminologyObject to = null;
			
			// Search for the given TerminologyObject
			List<TerminologyObject> tos = DebugUtilities.getAllTOsFromKB(kb);
			tos.addAll(DebugUtilities.getSolutionsFromKB(kb));
			for (TerminologyObject too : tos) {
				if (too.getName().equals(qid)) {
					to = too;
					break;
				}
			}

			// if question id = solution's key, menu elements = solutions
			if (qid.equals(SOLUTIONS_KEY)) tos = (List<TerminologyObject>) DebugUtilities.getSolutionsFromKB(kb);
			else tos = DebugUtilities.getInfluentialTOs(to, kb);
			// no elements to render
			if (tos.size() == 0) buffer.append("<p style='margin-left:10px;'>Keine relevanten Elemente gefunden.</p>");
			else {
				// render menu
				if (!qid.equals(SOLUTIONS_KEY)) buffer.append("<p>Relevante Elemente:</p><ul>");
				for (TerminologyObject tobj : tos) {
					if (tobj instanceof Solution) {
						rate = session.getBlackboard().getRating((Solution) tobj).toString();
						buffer.append("<li class='debuggerMenuSolution' style='");
						if (rate.equals("EXCLUDED")) buffer.append("border-color:"
								+ DebugUtilities.COLOR_EXCLUDED + "'");
						else if (rate.equals("SUGGESTED")) buffer.append("border-color:"
								+ DebugUtilities.COLOR_SUGGESTED + "'");
						else if (rate.equals("ESTABLISHED")) buffer.append("border-color:"
								+ DebugUtilities.COLOR_ESTABLISHED + "'");
						else buffer.append("border-color: " + DebugUtilities.COLOR_UNCLEAR + "'");
					}
					else buffer.append("<li class='debuggerMenu'");
					buffer.append(" kbid='" + kb.getId() + "'>"
							+ tobj.getName()
							+ "</li>");
				}
				buffer.append("</ul>");
			}
		}
		catch (NullPointerException e) {
		}
		
		return buffer.toString();
	}

}
