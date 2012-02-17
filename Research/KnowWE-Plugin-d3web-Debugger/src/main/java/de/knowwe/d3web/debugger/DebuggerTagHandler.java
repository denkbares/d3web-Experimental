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
package de.knowwe.d3web.debugger;

import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.we.basic.SessionBroker;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;


/**
 * 
 * @author dupke
 */
public class DebuggerTagHandler extends AbstractTagHandler {

	/**
	 * @param name
	 */
	public DebuggerTagHandler() {
		super("debug");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		// Get the page's knowledgebase and prepare some variables
		StringBuffer buffer = new StringBuffer();
		String title = userContext.getTitle();
		String web = userContext.getParameter(KnowWEAttributes.WEB);
		SessionBroker broker;
		Session session = null;
		KnowledgeBase kb = null;
		String kbID;
		// Get knowledgebase
		try {
			// If knowledgebase's id is given
			if (parameters.containsKey("kbID")) {
				kbID = parameters.get("kbID");
				broker = D3webUtils.getBroker(userContext.getUserName(), web);
				session = broker.getSession(kbID);
				kb = session.getKnowledgeBase();
			}
			// If knowledgebase was not found or id not given, try to get a
			// local one
			if (kb == null || session == null) {
				session = D3webUtils.getSession(title, userContext, web);
				kb = D3webUtils.getKnowledgeBase(web, title);
			}
		}
		catch (NullPointerException e) {
		}
		// If finally no knowledgebase was found => return error
		finally {
			if (kb == null || session == null) return KnowWEUtils.maskHTML("Error: No knowledgebase was found.");
		}

		buffer.append("<div id='debugger' class='debugger'>");
		// trace
		buffer.append("<div id='debuggerTrace' class='debuggerTrace'>");
		buffer.append("<span lvl=0 kbid='" + kb.getId() + "'>Solutions</span>");
		buffer.append("</div>");
		// menu
		buffer.append("<div id='debuggerMenu'><ul>");
		for (TerminologyObject s : DebugUtilities.getSolutionsFromKB(kb)) {
			buffer.append("<li class='debuggerMenuSolution' kbid='" + kb.getId() + "'>" + s
					+ "</li>");
		}
		buffer.append("</ul></div><div style='clear:both'></div>");
		// main
		buffer.append("<div id= 'debuggerMain' class='debuggerMain'></div>");
		// rule
		buffer.append("<div id='debuggerRule' class='debuggerRule'></div>");

		buffer.append("</div>");

		return KnowWEUtils.maskHTML(buffer.toString());
	}

}
