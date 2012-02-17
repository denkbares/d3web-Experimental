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
import de.d3web.core.session.Session;
import de.d3web.we.basic.SessionBroker;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.d3web.debugger.DebugUtilities;


/**
 * 
 * @author dupke
 */
public class DebuggerMenuAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		StringBuffer buffer = new StringBuffer();
		try {
			String kbID = context.getParameter("kbid");
			String qid = context.getParameter("qid");
			SessionBroker broker = D3webUtils.getBroker(context.getUserName(), context.getWeb());
			Session session = broker.getSession(kbID);
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

			if (qid.equals("Solutions")) tos = (List<TerminologyObject>) DebugUtilities.getSolutionsFromKB(kb);
			else tos = DebugUtilities.getInfluentialTOs(to, kb);

			if (tos.size() == 0) buffer.append("<p style='margin-left:10px;font-weight:bold'>Keine weiteren Elemente</p>");
			else {
				buffer.append("<ul>");
				for (TerminologyObject too : tos) {
					if (qid.equals("Start")) buffer.append("<li class='debuggerMenuSolution'");
					else buffer.append("<li class='debuggerMenu'");
					buffer.append(" kbid='" + kb.getId() + "'>"
							+ too.getName()
							+ "</li>");
				}
				buffer.append("</ul>");
			}
		}
		catch (NullPointerException e) {
		}
		
		if (buffer != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(buffer.toString());
		}
	}


}
