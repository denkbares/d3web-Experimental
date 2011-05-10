/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.we.testcaseexecutor;

import java.util.Collection;
import java.util.Map;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.KnowWERessourceLoader;
import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.wikiConnector.ConnectorAttachment;
import de.d3web.we.wikiConnector.KnowWEWikiConnector;

/**
 * 
 * @author Florian Ziegler
 * @created 28.04.2011
 */
public class TestCaseExecutorTagHandler extends AbstractHTMLTagHandler {

	public TestCaseExecutorTagHandler() {
		super("testcaseexecutor");
		KnowWERessourceLoader.getInstance().add("testcaseexecutor.js",
				KnowWERessourceLoader.RESOURCE_SCRIPT);
		KnowWERessourceLoader.getInstance().add("testcaseexecutor.css",
				KnowWERessourceLoader.RESOURCE_STYLESHEET);

	}

	@Override
	public String renderHTML(String topic, UserContext user,
			Map<String, String> parameters, String web) {

		String articleName = user.getParameter("page");
		if (!topic.equalsIgnoreCase(articleName)) {
			topic = articleName;
		}


		KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();

		Collection<ConnectorAttachment> attachments = connector.getAttachments();

		StringBuilder html = new StringBuilder();
		html.append("<h3 class=\"testExecutor\"> TestCase Executor </h3>");
		html.append("<br /><div id=\"testcases\"><strong>Testcases:</strong><br />");

		for (ConnectorAttachment attachment : attachments) {
			if (attachment.getParentName().equals(topic)
					&& attachment.getFileName().endsWith(".xml")) {
				String name = attachment.getFileName();
				html.append("<div class=\"selectXMLFile\" onclick=\"return TestCaseExecutor.getTestcases('"
						+ name + "')\">"
						+ name + "</div>");
			}
		}

		html.append("</div>");

		return html.toString();
	}

}
