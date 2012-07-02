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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.caseAnalysis.RTCDiff;
import de.d3web.empiricaltesting.caseAnalysis.ValueDiff;
import de.d3web.empiricaltesting.caseAnalysis.functions.Diff;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysis;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysisReport;
import de.d3web.we.testcase.util.Tuple;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.action.UserActionContext;

/**
 * 
 * @author Florian Ziegler
 * @created 07.05.2011
 */
public final class TestCaseExecutorUtils {

	public static String convertStreamToString(InputStream is)
			throws IOException {

		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			}
			finally {
				is.close();
			}
			return writer.toString();
		}
		else {
			return "";
		}
	}

	public static String createHiddenFilenameDiv(String filename) {
		return "<div id=\"filename\" style=\"display: none\">" + filename + "</div>";
	}

	public static String createHiddenMasterDiv(String master) {
		return "<div id=\"master\" style=\"display: none\">" + master + "</div>";
	}

	private static final DecimalFormat formatter = new DecimalFormat("0.00");

	private static String renderTestCasePassed(TestCase t, TestCaseAnalysisReport result, ResourceBundle rb, MessageFormat mf) {
		StringBuilder html = new StringBuilder();

		// TestCase passed text and green bulb
		html.append("<p>");
		html.append("<img src='KnowWEExtension/images/green_bulb.gif' width='16' height='16' /> ");
		html.append("<strong>");
		html.append(loadMessage("KnowWE.TestCase.passed",
				new Object[] { t.getRepository().size() }, rb, mf));
		html.append("</strong>");
		html.append("</p>");

		html.append("<p style='margin-left:22px'>");
		html.append("Precision: ");
		html.append(result.precision());
		html.append("<br />");
		html.append("Recall: ");
		html.append(result.recall());
		html.append("<br /><br />");
		html.append("</p>");

		return html.toString();
	}

	private static String loadMessage(String key, Object[] arguments, ResourceBundle rb, MessageFormat msgFormatter) {
		msgFormatter.applyPattern(rb.getString(key));
		return msgFormatter.format(arguments);
	}

	private static String renderTestCaseNotConsistent(TestCase t, TestCaseAnalysisReport result, ResourceBundle rb, MessageFormat mf) {
		StringBuilder html = new StringBuilder();

		// TestCase failed text and red bulb
		html.append("<p>");
		html.append("<img src='KnowWEExtension/images/red_bulb.gif' width='16' height='16' /> ");
		html.append("<strong>");
		html.append(loadMessage("KnowWE.TestCase.failed",
				new Object[] { t.getRepository().size() }, rb, mf));
		html.append("</strong>");
		html.append("</p>");

		// TestCase TestCaseAnalysisReport Details
		html.append("<p style='margin-left:22px'>");
		html.append("Precision: ");
		html.append(formatter.format(result.precision()));
		html.append("<br />");
		html.append("Recall: ");
		html.append(formatter.format(result.recall()));
		html.append("<br /><br />");
		html.append(rb.getString("KnowWE.TestCase.notconsistent"));
		html.append("</p>\n");

		html.append(renderNotConsistentDetails(t, rb));

		return html.toString();
	}

	private static String renderNotConsistentDetails(TestCase t, ResourceBundle rb) {
		StringBuilder html = new StringBuilder();

		// Pointer and Text
		html.append("<p id='testcase2-show-extend' class='show-extend pointer extend-panel-right'>");
		html.append(rb.getString("KnowWE.TestCase.detail"));
		html.append("</p>");

		// Div containing details
		html.append("<div id='testcase-detail-panel' class='hidden'>");
		html.append("<p style='margin-left:22px'>");
		html.append(findInconsistentRTC(t, rb));
		html.append("</p>");
		html.append("</div>\n");

		return html.toString();
	}

	private static String findInconsistentRTC(TestCase t, ResourceBundle rb) {

		StringBuilder message = new StringBuilder();

		for (SequentialTestCase stc1 : t.getRepository()) {
			for (SequentialTestCase stc2 : t.getRepository()) {
				for (int i = 0; i < stc1.getCases().size() && i < stc2.getCases().size(); i++) {
					RatedTestCase rtc1 = stc1.getCases().get(i);
					RatedTestCase rtc2 = stc2.getCases().get(i);

					// when the findings are equal...
					if (rtc1.getFindings().equals(rtc2.getFindings())) {
						// ...but not the solutions...
						if (!rtc1.getExpectedSolutions().equals(
								rtc2.getExpectedSolutions())) {
							// ...the TestCase is not consistent!
							message.append("Rated-Test-Case ");
							message.append(stc1.getCases().indexOf(rtc1));
							message.append(" in ");
							message.append(stc1.getName());
							message.append(" ");
							message.append(rb.getString("KnowWE.TestCase.and"));
							message.append(" ");
							message.append("Rated-Test-Case ");
							message.append(stc2.getCases().indexOf(rtc2));
							message.append(" in ");
							message.append(stc2.getName());
							message.append(" ");
							message.append(rb.getString("KnowWE.TestCase.havesamefindings"));
							message.append("<br />");

						}
					}
					else {
						break;
					}
				}
			}
		}

		// Not very nice but prevents double listing of RTCs
		return message.substring(0, message.length() / 2);
	}

	public static void renderTests(UserActionContext context, TestCase t) throws IOException {
		ResourceBundle rb = D3webUtils.getD3webBundle(context);
		MessageFormat mf = new MessageFormat("");

		if (t == null) {
			Logger.getLogger(TestCaseExecutorUtils.class.getName()).warning(
					"Test case was null. Unable to execute it.");
			context.getWriter().write(rb.getString("KnowWE.TestCase.loaderror"));
		}
		else {
			context.getWriter().write(renderTestCaseResult(t, rb, mf));
		}
	}

	public static String renderTestCaseResult(TestCase t, ResourceBundle rb, MessageFormat mf) {
		TestCaseAnalysis analysis = new TestCaseAnalysis();
		TestCaseAnalysisReport result = analysis.runAndAnalyze(t);
		return renderTestAnalysisResult(t, result, rb, mf);

	}

	/**
	 * 
	 * @created 16.09.2011
	 * @param t
	 * @param result
	 * @param rb
	 * @param mf
	 * @return
	 */
	public static String renderTestAnalysisResult(TestCase t, TestCaseAnalysisReport result, ResourceBundle rb, MessageFormat mf) {
		if (result.precision() == 1.0 && result.recall() == 1.0) {
			return renderTestCasePassed(t, result, rb, mf);

		}
		else if (!t.isConsistent()) {
			return renderTestCaseNotConsistent(t, result, rb, mf);

		}
		else {
			return renderTestCaseFailed(t, result, rb, mf);
		}
	}

	private static String renderTestCaseFailed(TestCase t, TestCaseAnalysisReport result, ResourceBundle rb, MessageFormat mf) {
		StringBuilder html = new StringBuilder();

		// TestCase failed text and red bulb
		html.append("<p>");
		html.append("<img src='KnowWEExtension/images/red_bulb.gif' width='16' height='16' /> ");
		html.append("<strong>");
		html.append(loadMessage("KnowWE.TestCase.failed",
				new Object[] { t.getRepository().size() }, rb, mf));
		html.append("</strong>");
		html.append("</p>");

		// TestCase TestCaseAnalysisReport Detais
		html.append("<p style='margin-left:22px'>");
		html.append("Precision: ");
		html.append(formatter.format(result.precision()));
		html.append("<br />");
		html.append("Recall: ");
		html.append(formatter.format(result.recall()));
		html.append("</p>\n");

		html.append(renderDifferenceDetails(t, result, rb));

		return html.toString();
	}

	private static String renderDifferenceDetails(TestCase t, TestCaseAnalysisReport result, ResourceBundle rb) {

		StringBuilder html = new StringBuilder();

		// Pointer and Text

		html.append("<p id='testcase-failed-extend' onclick='extendTestCaseFailed()'>");
		html.append("<img id='testcase-failed-extend-img' src='KnowWEExtension/images/arrow_right.png' ");
		html.append("align='absmiddle' /> ");
		html.append(rb.getString("KnowWE.TestCase.detail"));
		html.append("</p>");

		html.append("<div style='clear:both'></div>");

		// Table containing details
		html.append("<div id='testcase-detail-panel' style='display:none'>");
		html.append(renderDetailResultTable(t, result, rb));
		html.append("</div>\n");

		return html.toString();
	}

	private static String renderDetailResultTable(TestCase t, TestCaseAnalysisReport result, ResourceBundle rb) {

		StringBuilder html = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		temp.append("</table>");

		// HTML-Code
		for (SequentialTestCase stc : t.getRepository()) {
			Diff stcDiff = result.getDiffFor(stc);
			temp = new StringBuilder();
			for (RatedTestCase rtc : stc.getCases()) {
				if (stcDiff.hasDiff(rtc)) {
					temp.append("<tr>");
					temp.append("<th colspan='2' >");
					temp.append("Rated-Test-Case ");
					temp.append(stc.getCases().indexOf(rtc) + 1);
					temp.append("</th>");
					temp.append("</tr>");

					// get solution diffs
					RTCDiff rtcDiff = stcDiff.getDiff(rtc);
					Collection<TerminologyObject> diffObjects = rtcDiff.getDiffObjects();
					Collection<Tuple<TerminologyObject, ValueDiff>> solutionDiffs = new LinkedList<Tuple<TerminologyObject, ValueDiff>>();
					for (TerminologyObject diff : diffObjects) {
						if (diff instanceof Solution) {
							ValueDiff valueDiff = rtcDiff.getDiffFor(diff);
							if (valueDiff.differ()) {
								solutionDiffs.add(Tuple.createTuple(diff, valueDiff));
							}
						}

					}

					// render solution diffs
					if (!solutionDiffs.isEmpty()) {
						temp.append("<tr>");
						temp.append("<th>");
						temp.append(rb.getString("KnowWE.TestCase.expected"));
						temp.append("</th>");
						temp.append("<th>");
						temp.append(rb.getString("KnowWE.TestCase.derived"));
						temp.append("</th>");
						temp.append("</tr>");
						temp.append("<tr>");
						temp.append("<td>");
						temp.append("<ul>");

						// expected solutions
						for (Tuple<TerminologyObject, ValueDiff> diff : solutionDiffs) {
							temp.append("<li>");
							temp.append(diff.getFirst().toString());
							temp.append(" = ");
							temp.append(diff.getSecond().getExpected());
							temp.append("</li>");
						}
						temp.append("</ul>");
						temp.append("</td>");
						temp.append("<td>");
						temp.append("<ul>");

						// derived solutions
						for (Tuple<TerminologyObject, ValueDiff> diff : solutionDiffs) {
							temp.append("<li>");
							temp.append(diff.getFirst().toString());
							temp.append(" = ");
							temp.append(diff.getSecond().getDerived());
							temp.append("</li>");
						}
						temp.append("</ul>");
						temp.append("</td>");
						temp.append("</tr>");
					}

					// there are also diffs in regular findings
					if (diffObjects.size() > solutionDiffs.size()) {

						temp.append("<tr>");
						temp.append("<th>");
						temp.append(rb.getString("KnowWE.TestCase.expectedFindings"));
						temp.append("</th>");
						temp.append("<th>");
						temp.append(rb.getString("KnowWE.TestCase.derivedFindings"));
						temp.append("</th>");
						temp.append("</tr>");

						// expected findings
						temp.append("<tr>");
						temp.append("<td>");
						temp.append("<ul>");
						RTCDiff diff = stcDiff.getDiff(rtc);
						for (TerminologyObject obj : diff.getDiffObjects()) {
							if (!(obj instanceof Solution)) {
								temp.append("<li>");
								temp.append(obj.getName() + " = "
										+ diff.getDiffFor(obj).getExpected());
								temp.append("</li>");
							}
						}
						temp.append("</ul>");
						temp.append("</td>");

						// derived findings
						temp.append("<td>");
						temp.append("<ul>");
						for (TerminologyObject obj : diff.getDiffObjects()) {
							if (!(obj instanceof Solution)) {
								temp.append("<li>");
								temp.append(obj.getName() + " = "
										+ diff.getDiffFor(obj).getDerived());
								temp.append("</li>");
							}
						}
						temp.append("</ul>");
						temp.append("</td>");
						temp.append("</tr>");
					}
				}
			}

			if (temp.length() > 0) {
				temp.insert(0, "</tr>");
				temp.insert(0, "</th>");
				temp.insert(0, stc.getName());
				temp.insert(0, "Sequential-Test-Case ");
				temp.insert(0, "<th colspan='2'>");
				temp.insert(0, "<tr>");
				temp.insert(0, "<table class='wikitable' border='1'>");
				temp.append("</table>");
				html.append(temp);
			}
		}

		return html.toString();
	}
}
