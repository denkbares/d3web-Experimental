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
package de.knowwe.we.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.core.KnowWEEnvironment;

/**
 * Handles actions within the QuickEdit mode box.
 * 
 * @author smark
 * @created 15.06.2011
 */
public class QuickEditHandlingAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = handle(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}
	}

	/**
	 * Handles actions within the QuickEdit mode.
	 * 
	 * Returns a JSON string for further processing on the client-side within
	 * the JavaScript.
	 * 
	 * @created 15.06.2011
	 * @param context
	 * @return success JSON string
	 * @throws IOException
	 */
	private String handle(UserActionContext context) throws IOException {

		String topic = context.getTopic();
		String action = context.getParameter("action");
		String id = context.getParameter("KdomNodeId");
		String web = context.getParameter(KnowWEAttributes.WEB);
		String value = context.getParameter("changes");

		boolean canEditPage = KnowWEEnvironment.getInstance().getWikiConnector().userCanEditPage(
				topic, context.getRequest());

		if (canEditPage == false) {
			return "Your are not allowed to edit page";
		}

		if (action.equals("QuickEditHandlingAction")) {
			Map<String, String> nodesMap = new HashMap<String, String>();
			nodesMap.put(id, value);
			KnowWEEnvironment.getInstance().getArticleManager(web).replaceKDOMNodesSaveAndBuild(
					context, topic, nodesMap);
			return "{\"success\":true}";
		}
		return "{\"success\":false}";
	}
}
