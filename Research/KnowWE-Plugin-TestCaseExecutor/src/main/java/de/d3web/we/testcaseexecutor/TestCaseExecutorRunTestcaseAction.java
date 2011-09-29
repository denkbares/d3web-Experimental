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
import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
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

		String master = context.getParameter("master");
		KnowledgeBase kb = D3webModule.getKnowledgeBase(
				context.getWeb(), master);

		String sectionID = context.getParameter("kdomid");
		if (sectionID != null) {
			executeFromSection(context, kb, sectionID);
			return;
		}

		String testCases = context.getParameter("testcases");
		String fileName = context.getParameter("filename");
		String topic = context.getTopic();
		String[] cases = testCases.split(TESTCASEEXECUTOR_SEPARATOR);


		KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();
		Collection<String> attachments = connector.getAttachmentFilenamesForPage(topic);
		ConnectorAttachment selectedAttachment = null;

		for (String attachment : attachments) {
			if (attachment.equals(fileName)) {
				selectedAttachment = connector.getAttachment(topic + "/" + attachment);
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
			TestCaseRunAction.renderTests(context, t);

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
	 * 
	 * @param context
	 * @param kb
	 * @param sectionID
	 * @created 16.09.2011
	 */
	private void executeFromSection(UserActionContext context, KnowledgeBase kb, String sectionID) {

		Section<TestCaseExecutorType> section = (Section<TestCaseExecutorType>) Sections.getSection(sectionID);

		String file = DefaultMarkupType.getAnnotation(section, TestCaseExecutorType.ANNOTATION_FILE);

		KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();
		ConnectorAttachment attachment = connector.getAttachment(section.getArticle().getTitle()
				+ "/" + file);


		List<SequentialTestCase> testcases;
		try {
			testcases = TestPersistence.getInstance().loadCases(attachment.getInputStream(), kb);
		}
		catch (Exception e) {
			// TODO error handling
			e.printStackTrace();
			return;
		}


		TestCase t = new TestCase();
		t.setKb(kb);
		t.setRepository(testcases);
		TestCaseAnalysis analysis = new TestCaseAnalysis();
		TestCaseAnalysisReport result = analysis.runAndAnalyze(t);

		ResourceBundle rb = D3webModule.getKwikiBundle_d3web(context);
		MessageFormat mf = new MessageFormat("");
		TestCaseRunAction.renderTestAnalysisResult(t, result, rb, mf);


	}

}
