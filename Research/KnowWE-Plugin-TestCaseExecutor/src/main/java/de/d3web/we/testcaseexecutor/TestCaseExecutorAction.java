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
package de.d3web.we.testcaseexecutor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.wikiConnector.ConnectorAttachment;

/**
 * 
 * @author Florian Ziegler
 * @created 04.05.2011
 */
public class TestCaseExecutorAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String filename = context.getParameter("filename");
		String topic = context.getTopic();

		Collection<ConnectorAttachment> attachments =
				KnowWEEnvironment.getInstance().getWikiConnector().getAttachments();
		String xml = "";
		List<String> testCases = new LinkedList<String>();
		for (ConnectorAttachment att : attachments) {
			if (att.getFileName().equalsIgnoreCase(filename)) {

				InputStream is = att.getInputStream();
				xml = TestCaseExecutorUtils.convertStreamToString(is);
				testCases = getSTestcases(xml);

			}
		}

		String tc = "<ul>";
		for (String s : testCases) {
			tc += "<li class=\"runTestExecutor\" onclick=\"return TestCaseExecutor.runTestcase(this)\">"
					+ s + "</li>";
		}
		tc += "</ul>";

		context.getWriter().write(
				"<div><div id=\"filename\" style=\"display: none\">" + filename
						+ "</div>Available Testcases:"
						+ tc + "</div>");
	}

	public List<String> getSTestcases(String xml) {
		List<String> sTestCases = new LinkedList<String>();
		String[] split = xml.split("<STestCase");
		for (String s : split) {
			s = s.trim();
			if (s.startsWith("Name=\"")) {
				s = s.substring(s.indexOf("\""));
				s = s.substring(s.indexOf("\"") + 1);
				s = s.substring(0, s.indexOf("\""));
				sTestCases.add(s);
			}
		}

		return sTestCases;
	}

}
