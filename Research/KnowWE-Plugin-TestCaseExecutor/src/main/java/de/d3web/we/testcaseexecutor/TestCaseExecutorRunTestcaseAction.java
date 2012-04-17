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
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.stream.XMLStreamException;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysis;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysisReport;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.wikiConnector.ConnectorAttachment;
import de.knowwe.core.wikiConnector.WikiConnector;

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

		String sectionID = context.getParameter("kdomid");
		if (sectionID != null) {
			executeFromSection(context, sectionID);
			return;
		}

		String master = context.getParameter("master");
		KnowledgeBase kb = D3webUtils.getKnowledgeBase(
				context.getWeb(), master);

		String testCases = context.getParameter("testcases");
		String fileName = context.getParameter("filename");
		String topic = context.getTitle();
		String[] cases = testCases.split(TESTCASEEXECUTOR_SEPARATOR);

		WikiConnector connector = Environment.getInstance().getWikiConnector();
		Collection<ConnectorAttachment> attachments = connector.getAttachments(topic);
		ConnectorAttachment selectedAttachment = null;

		for (ConnectorAttachment attachment : attachments) {
			if (attachment.getFileName().equals(fileName)) {
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
			TestCaseExecutorUtils.renderTests(context, t);

			// append back button
			result.append("<br />");
			result.append("<div id=\"backToCaseSelection\" onclick=\"return TestCaseExecutor.backToCaseSelection()\"> <strong>back</strong> </div>");

			context.getWriter().write(result.toString());

		} // TODO handle exceptions
		catch (XMLStreamException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Executes the testcase files provided in the according
	 * {@link TestCaseExecutorType} section's file Annotation.
	 * 
	 * @param context
	 * @param sectionID
	 * @throws IOException
	 * @created 16.09.2011
	 */
	private void executeFromSection(UserActionContext context, String sectionID) throws IOException {

		Section<TestCaseExecutorType> section = Sections.cast(Sections.getSection(sectionID),
				TestCaseExecutorType.class);

		TestCaseAnalysis analysis = new TestCaseAnalysis();
		TestCaseExecutorType.execute(section, analysis);

		TestCaseAnalysisReport result = (TestCaseAnalysisReport) section.getSectionStore().getObject(
				TestCaseExecutorType.TEST_RESULT_KEY);
		TestCase t = (TestCase) section.getSectionStore().getObject(
				TestCaseExecutorType.TESTCASE_KEY);

		ResourceBundle rb = D3webUtils.getD3webBundle(context);
		MessageFormat mf = new MessageFormat("");
		String result2 = TestCaseExecutorUtils.renderTestAnalysisResult(t, result, rb, mf);

		context.getWriter().write(result2);

	}

}
