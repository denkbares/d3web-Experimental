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

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.utils.Strings;

/**
 * An action to rerender the debugger.
 * 
 * @author dupke
 */
public class DebuggerRerenderAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		StringBuffer buffer = new StringBuffer();
		DebuggerRuleboxAction dra = new DebuggerRuleboxAction();
		DebuggerMainAction dma = new DebuggerMainAction();
		String menu, trace;
		// get menu
		if (!context.getParameters().containsKey("menu")) menu = "";
		else menu = context.getParameter("menu");
		// get trace
		if (!context.getParameters().containsKey("trace")) trace = "";
		else trace = context.getParameter("trace");

		// trace
		buffer.append("<div id='debuggerTrace' class='debuggerTrace'>" + trace + "</div>");
		// menu
		buffer.append("<div id='debuggerMenu'>" + menu + "</div>");
		buffer.append("<div style='clear:both'></div>");
		// main
		buffer.append("<div id= 'debuggerMain' class='debuggerMain'>" + dma.renderMain(context)
				+ "</div>");
		// rule
		buffer.append("<div id='debuggerRule' class='debuggerRule'>" + dra.renderRule(context)
				+ "</div>");

		if (buffer != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(Strings.unmaskHTML(buffer.toString()));
		}

	}

}
