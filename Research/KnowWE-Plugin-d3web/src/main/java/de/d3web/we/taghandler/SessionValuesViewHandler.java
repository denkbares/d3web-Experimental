/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.d3web.we.taghandler;

import java.util.Map;

import de.d3web.we.action.SessionValuesViewAction;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.user.UserContext;

/**
 * Displays the values of the article's d3web-Session. The magic is done with
 * AJAX {@link SessionValuesViewAction}
 * 
 * @author Sebastian Furth
 * @created 06.06.2010
 */
public class SessionValuesViewHandler extends AbstractHTMLTagHandler {

	public SessionValuesViewHandler() {
		super("sessionvalues");
	}

	@Override
	public String getExampleString() {
		return "[{KnowWEPlugin sessionValues}]";
	}

	@Override
	public String getDescription(UserContext user) {
		return D3webModule.getKwikiBundle_d3web(user).getString("KnowWE.SessionValues.description");
	}

	@Override
	public String renderHTML(String topic, UserContext user,
			Map<String, String> values, String web) {

		return "<div class='panel'>"
				+ "<h3>Values</h3><div id='sessionvalues-panel'>"
				+ "<input type='hidden' id='sessionvalues-user' value='"
				+ user.getUserName()
				+ "'/>"
				+ "<input type='hidden' id='sessionvalues-topic' value='"
				+ topic
				+ "'/>"
				+ "<div id='sessionvalues-result'>"
				+ "No values set in this article's d3web-Session."
				+ "</div>"
				+ "</div></div>";
	}

}
