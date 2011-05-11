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
import java.util.List;

import javax.xml.stream.XMLStreamException;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.empiricaltesting.caseAnalysis.functions.Diff;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysis;
import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.wikiConnector.ConnectorAttachment;
import de.d3web.we.wikiConnector.KnowWEWikiConnector;

/**
 * 
 * @author Florian Ziegler
 * @created 07.05.2011
 */
public class TestCaseExecutorRunTestcaseAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String testCase = context.getParameter("testcase");
		String fileName = context.getParameter("filename");
		String master = context.getParameter("master");
		String topic = context.getTopic();

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

			for (SequentialTestCase testcase : testcases) {
				if (testcase.getName().equals(testCase)) {
					TestCaseAnalysis analysis = TestCaseAnalysis.getInstance();
					Diff result = analysis.runAndAnalyze(
							testcase, kb);
					context.getWriter().write(createOutput(result));
				}

			}

		} // TODO handle exceptions
		catch (XMLStreamException e) {
			e.printStackTrace();
		}

	}


	private String createOutput(Diff result) {
		StringBuilder html = new StringBuilder();
		if (!result.hasDifferences()) {
			html.append(renderTestCasePassed(result));
		} else {
			html.append(renderTestCaseFailed(result));
		}
		return html.toString();

	}

	private String renderTestCasePassed(Diff result) {
		StringBuilder html = new StringBuilder();

		// TestCase passed text and green bulb
		html.append("<p>");
		html.append("<img src='KnowWEExtension/images/green_bulb.gif' width='16' height='16' /> ");
		html.append("<strong>");
		html.append("Test passed. No differences found!");
		html.append("</strong>");
		html.append("</p>");

		return html.toString();
	}

	private String renderTestCaseFailed(Diff result) {
		StringBuilder html = new StringBuilder();

		html.append("<p>");
		html.append("<img src='KnowWEExtension/images/red_bulb.gif' width='16' height='16' /> ");
		html.append("<strong>");
		html.append("Test failed!");
		html.append("</strong>");
		html.append("</p>");

		html.append("<strong>Findings:</strong>");
		for (RatedTestCase rtc : result.getCasesWithDifference()) {
			if (!rtc.getExpectedFindings().equals(rtc.getFindings())) {
				html.append("<br />Expected:<br />");
				html.append(rtc.getExpectedFindings());
				html.append("<br />but was:<br />");
				html.append(rtc.getFindings());
			}
		}

		html.append("<br /><br /><strong>Solutions:</strong>");
		for (RatedTestCase rtc : result.getCasesWithDifference()) {
			if (!rtc.getExpectedSolutions().equals(rtc.getDerivedSolutions())) {
				html.append("<br />Expected:<br />");
				html.append(rtc.getExpectedSolutions());
				html.append("<br />but was:<br />");
				html.append(rtc.getDerivedSolutions());
			}
		}

		return html.toString();
	}

}
