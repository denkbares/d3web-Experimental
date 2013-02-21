/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.emergency;

import java.util.Map;

import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;

/**
 * Renders a link to the emergency plan of a user in the ICD forum. The
 * emergency plan holds information about the model of the ICD, cardiologist,
 * physicians, blood type and much more. This can easily be printed out.
 * 
 * @author Stefan Mark
 * @created 22.09.2011
 */
public class EmergencyPlanTagHandler extends AbstractHTMLTagHandler {

	/**
	 * @param name
	 */
	public EmergencyPlanTagHandler() {
		super("defi.emergency");
	}

	@Override
	public void renderHTML(String web, String topic, UserContext user, Map<String, String> parameters, RenderResult result) {

		result.appendHTML("<a href=\"EmergencyPlan.jsp?user=" + user.getUserName()
				+ "\" title=\"&gt;&gt;Notfallplan erzeugen&lt;&lt;\" target=\"_new\">");
		result.appendHTML("&gt;&gt;Notfallplan erzeugen&lt;&lt;");
		result.appendHTML("</a>");

	}
}
