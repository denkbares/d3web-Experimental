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
package de.knowwe.d3web.debugger;

import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.we.basic.SessionProvider;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.d3web.debugger.actions.DebuggerMenuAction;

/**
 * This d3web-debugger is used to debug a knowledgebase. It uses backtracking to
 * trace an error, starting at a chosen solution.
 * 
 * @author dupke
 */
public class DebuggerTagHandler extends AbstractTagHandler {

	/**
	 * @param name
	 */
	public DebuggerTagHandler() {
		super("ruledebugger");
	}

	/**
	 * Render the debugger.
	 */
	@Override
	public void render(Section<?> section, UserContext userContext,
			Map<String, String> parameters, RenderResult result) {
		// Get the page's knowledgebase and prepare some variables
		StringBuffer buffer = new StringBuffer();
		String title = userContext.getTitle();
		String web = userContext.getParameter(Attributes.WEB);
		// If article already contains a debugger, return error.
		String articleText = Environment.getInstance().getArticle(web, title).getRootSection().getText();
		if (articleText.split("KnowWEPlugin debugger").length > 2) {
			result.appendHTML("<p class='info box'>Fehler: Nur ein Debugger pro Artikel!</p>");
			return;
		}
		// Get knowledgebase
		KnowledgeBase kb = null;
		Session session = null;
		String kbID = "";
		try {
			// If knowledgebase's id is given
			if (parameters.containsKey("kbID")) {
				kbID = parameters.get("kbID");
				kb = D3webUtils.getKnowledgeBase(web, kbID);
			}
			// If knowledgebase was not found or id not given, try to get a
			// local one
			if (kb == null) {
				kb = D3webUtils.getKnowledgeBase(web, title);
			}
		}
		catch (NullPointerException e) {
		}
		// If finally no knowledgebase was found => return error
		finally {
			if (kb == null) {
				result.append("Error: No knowledgebase was found.");
				return;
			}
		}
		session = SessionProvider.getSession(userContext, kb);

		buffer.append("<div id='debugger' class='debugger'>");
		// trace
		buffer.append("<div id='debuggerTrace' class='debuggerTrace'>");
		buffer.append("<span lvl=0 kbid='" + kb.getId()
				+ "' onClick='KNOWWE.plugin.debuggr.traceClicked(this);'>"
				+ DebuggerMenuAction.SOLUTIONS_KEY + "</span>");
		buffer.append("</div>");
		// menu
		buffer.append("<div id='debuggerMenu'>");
		buffer.append(new DebuggerMenuAction().getMenuRendering(kbID,
				DebuggerMenuAction.SOLUTIONS_KEY, session));
		buffer.append("</div><div style='clear:both'></div>");
		// main
		buffer.append("<div id= 'debuggerMain' class='debuggerMain'></div>");
		// rule
		buffer.append("<div id='debuggerRule' class='debuggerRule'></div>");

		buffer.append("</div>");

		result.appendHTML(buffer.toString());
	}

}
