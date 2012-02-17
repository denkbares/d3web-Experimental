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
package de.knowwe.d3web.debugger.renderer;

import de.d3web.core.inference.Rule;
import de.d3web.core.session.Session;
import de.knowwe.d3web.debugger.inference.DebugAction;
import de.knowwe.d3web.debugger.inference.DebugCondition;


/**
 * 
 * @author dupke
 */
public class DebuggerRuleRenderer {

	public String renderRule(Rule r, Session session, String topic, String web) {
		StringBuffer buffer = new StringBuffer();

		DebugCondition dc = new DebugCondition(r.getCondition());
		DebugAction da = new DebugAction(r.getAction());
		buffer.append("IF " + dc.render(session, web, topic) + "<br />");
		if (r.hasFired(session)) buffer.append("<span style='background-color:#CFFFCF'>");
		else buffer.append("<span>");
		buffer.append("THEN " + da.render());
		buffer.append("</span>");

		return buffer.toString();
	}

	public String renderCondition(Rule r, Session session, String topic, String web) {
		StringBuffer buffer = new StringBuffer();

		return "";
	}
}
