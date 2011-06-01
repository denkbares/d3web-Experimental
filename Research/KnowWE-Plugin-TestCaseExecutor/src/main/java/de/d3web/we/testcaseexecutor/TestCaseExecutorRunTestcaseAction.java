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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.testcase.action.TestCaseRunAction;
import de.d3web.we.wikiConnector.ConnectorAttachment;
import de.d3web.we.wikiConnector.KnowWEWikiConnector;

/**
 * 
 * @author Florian Ziegler
 * @created 07.05.2011
 */
public class TestCaseExecutorRunTestcaseAction extends AbstractAction {

	private static final String TESTCASEEXECUTOR_SEPARATOR = "#####";
	private static final String TESTCASEEXECUTOR_UNNAMED = "Unnamed TestCase";

	@Override
	public void execute(UserActionContext context) throws IOException {

		String testCases = context.getParameter("testcases");
		String fileName = context.getParameter("filename");
		String master = context.getParameter("master");
		String topic = context.getTopic();
		String[] cases = testCases.split(TESTCASEEXECUTOR_SEPARATOR);

		KnowledgeBase kb = D3webModule.getAD3webKnowledgeServiceInTopic(
				context.getWeb(), master);

		KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();
		Collection<ConnectorAttachment> attachments = connector.getAttachments();
		ConnectorAttachment selectedAttachment = null;

		for (ConnectorAttachment attachment : attachments) {
			if (attachment.getParentName().equals(topic)
					&& attachment.getFileName().equals(fileName)) {
				selectedAttachment = attachment;
				break;
			}
		}

		try {
			List<SequentialTestCase> testcases =
					TestPersistence.getInstance().loadCases(selectedAttachment.getInputStream(), kb);

			StringBuilder result = new StringBuilder();
			result.append("<div>");
			result.append(TestCaseExecutorUtils.createHiddenFilenameDiv(fileName));
			result.append(TestCaseExecutorUtils.createHiddenMasterDiv(master));
			result.append("</div>");

			TestCase t = new TestCase();
			List<SequentialTestCase> repo = new LinkedList<SequentialTestCase>();

			for (SequentialTestCase testcase : testcases) {
				for (String s : cases) {

					// unnamed TestCases are named TESTCASEEXECUTOR_UNNAMED, but
					// in the kb they are named ""
					if (s.equals(TESTCASEEXECUTOR_UNNAMED)) s = "";

					if (testcase.getName().equals(s)) {
						repo.add(testcase);
						break;
					}
				}

			}

			// render the result
			t.setKb(kb);
			t.setRepository(repo);
			TestCaseRunAction action = new TestCaseRunAction();
			action.renderTests(context, t);

			// append back button
			result.append("<br />");
			result.append("<div id=\"backToCaseSelection\" onclick=\"return TestCaseExecutor.backToCaseSelection()\"> <strong>back</strong> </div>");

			context.getWriter().write(result.toString());

		} // TODO handle exceptions
		catch (XMLStreamException e) {
			e.printStackTrace();
		}

	}

}
