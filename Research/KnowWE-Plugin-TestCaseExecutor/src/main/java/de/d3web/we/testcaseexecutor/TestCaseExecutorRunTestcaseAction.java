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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.Rating;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.StateRating;
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

	private static final String MC_ANSWER_SEPARATOR = "#####";

	@Override
	public void execute(UserActionContext context) throws IOException {

		String testCase = context.getParameter("testcase");
		String fileName = context.getParameter("filename");
		String topic = context.getTopic();

		KnowledgeBase kb = D3webModule.getAD3webKnowledgeServiceInTopic(
				context.getWeb(), topic);


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
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(selectedAttachment.getInputStream());

			List<SequentialTestCase> testcases = new LinkedList<SequentialTestCase>();
			extractTestcasesFromXML(parser, testcases, kb);

			for (SequentialTestCase testcase : testcases) {
				if (testcase.getName().equals(testCase)) {
					TestCaseAnalysis analysis = TestCaseAnalysis.getInstance();
					Diff result = analysis.runAndAnalyze(
							testcase, kb);
					context.getWriter().write(createOutput(result));
				}

			}

		}
		catch (XMLStreamException e) {
			e.printStackTrace();
		}

	}

	/**
	 * extracts the information from each xml element and builds TestCases
	 * according to the information from the xml.
	 */
	private void extractTestcasesFromXML(XMLStreamReader parser, List<SequentialTestCase> testcases, KnowledgeBase kb) {
		try {
			while (parser.hasNext()) {

				switch (parser.getEventType()) {
				case XMLStreamConstants.START_DOCUMENT:
					break;

				case XMLStreamConstants.END_DOCUMENT:
					parser.close();
					break;

				case XMLStreamConstants.NAMESPACE:
					break;

				case XMLStreamConstants.START_ELEMENT:
					if (parser.getLocalName().equals("SeqTestCaseRepository")) {

					}
					else if (parser.getLocalName().equals("STestCase")) {
						SequentialTestCase testcase = new SequentialTestCase();
						for (int i = 0; i < parser.getAttributeCount(); i++) {
							if (parser.getAttributeLocalName(i).equals("Name")) {
								testcase.setName(parser.getAttributeValue(i));
							}
						}
						testcases.add(testcase);
					}
					else if (parser.getLocalName().equals("RatedTestCase")) {
						RatedTestCase ratedTestCase = new RatedTestCase();
						for (int i = 0; i < parser.getAttributeCount(); i++) {
							if (parser.getAttributeLocalName(i).equals("Name")) {
								ratedTestCase.setName(parser.getAttributeValue(i));
							}
							else if (parser.getAttributeLocalName(i).equals("LastTested")) {
								ratedTestCase.setTestingDate(parser.getAttributeValue(i));
							}
						}
						testcases.get(testcases.size() - 1).add(ratedTestCase);
					}
					else if (parser.getLocalName().equals("Findings")) {

					}
					else if (parser.getLocalName().equals("Finding")) {
						String question = "";
						String answer = "";
						for (int i = 0; i < parser.getAttributeCount(); i++) {
							if (parser.getAttributeLocalName(i).equals("Question")) {
								question = (parser.getAttributeValue(i));
							}
							else if (parser.getAttributeLocalName(i).equals("Answer")) {
								answer = parser.getAttributeValue(i);
							}
						}

						List<Finding> findings = new LinkedList<Finding>();
						if (answer.contains(MC_ANSWER_SEPARATOR)) {
							String[] answers = answer.split(MC_ANSWER_SEPARATOR);
							for (int i = 1; i < answers.length; i++) {
								try {
									Finding f = Finding.createFinding(kb, question, answers[i]);
									if (f != null) {
										findings.add(f);
									}
								}
								catch (Exception e) {
									// question not found
								}
							}
						}
						else {
							try {
								Finding f = Finding.createFinding(kb, question, answer);
								if (f != null) {
									findings.add(f);
								}
							}
							catch (Exception e) {
								// question not found
							}
						}

						SequentialTestCase lastTestCase = testcases.get(testcases.size() - 1);
						RatedTestCase lastRatedTestCase = lastTestCase.getCases().get(
								lastTestCase.getCases().size() - 1);

						for (Finding f : findings) {
							lastRatedTestCase.add(f);
						}

					}
					else if (parser.getLocalName().equals("Solutions")) {

					}
					else if (parser.getLocalName().equals("Solution")) {
						Solution solution = null;
						Rating rating = null;

						for (int i = 0; i < parser.getAttributeCount(); i++) {
							if (parser.getAttributeLocalName(i).equals("Name")) {
								solution = findMatchingSolution(parser.getAttributeValue(i), kb);
							}
							else if (parser.getAttributeLocalName(i).equals("Rating")) {
								rating = new StateRating(parser.getAttributeValue(i));
							}
						}

						if (solution != null && rating != null) {
							RatedSolution ratedSolution = new RatedSolution(solution, rating);
							SequentialTestCase lastTestCase = testcases.get(testcases.size() - 1);
							RatedTestCase lastRatedTestCase = lastTestCase.getCases().get(
									lastTestCase.getCases().size() - 1);

							lastRatedTestCase.getExpectedSolutions().add(ratedSolution);
						}
					}

					break;

				case XMLStreamConstants.CHARACTERS:
					break;

				case XMLStreamConstants.END_ELEMENT:
					break;

				default:
					break;
				}

				parser.next();
			}
		}
		catch (XMLStreamException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Solution findMatchingSolution(String solution, KnowledgeBase kb) {
		List<Solution> solutions = kb.getManager().getSolutions();
		for (Solution s : solutions) {
			if (s.getName().equals(solution)) {
				return s;
			}
		}
		return null;
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
