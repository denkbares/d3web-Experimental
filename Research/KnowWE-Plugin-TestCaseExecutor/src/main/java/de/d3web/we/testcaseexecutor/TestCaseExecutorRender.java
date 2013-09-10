package de.d3web.we.testcaseexecutor;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysisReport;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.wikiConnector.WikiAttachment;
import de.knowwe.core.wikiConnector.WikiConnector;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 11.05.2011
 */
public class TestCaseExecutorRender extends DefaultMarkupRenderer {

	@Override
	protected void renderContents(Section<?> section, UserContext user, RenderResult string) {

		String master = TestCaseExecutorType.getMaster(section);
		// no kb would cause massive amount of nullpointers
		/* KnowledgeBase kb = */D3webUtils.getKnowledgeBase(user.getWeb(), master);

		TestCaseAnalysisReport report = (TestCaseAnalysisReport) section.getSectionStore().getObject(
				TestCaseExecutorType.TEST_RESULT_KEY);
		if (report != null) { // test case has already been executed
			renderResult(report, string, section, user);
		}
		else {

			String[] files = DefaultMarkupType.getAnnotations(section,
					TestCaseExecutorType.ANNOTATION_FILE);

			if (files.length == 0) {
				appendSelection(section, master, string);
			}
			else {
				renderAutomated(section, master, files, string);
			}

		}
	}

	/**
	 * 
	 * @created 12.10.2011
	 * @param report
	 * @param string
	 * @param section
	 * @param context
	 */
	private void renderResult(TestCaseAnalysisReport report, RenderResult string, Section<?> section, UserContext context) {
		TestCaseAnalysisReport result = (TestCaseAnalysisReport) section.getSectionStore().getObject(
				TestCaseExecutorType.TEST_RESULT_KEY);
		TestCase t = (TestCase) section.getSectionStore().getObject(
				TestCaseExecutorType.TESTCASE_KEY);

		ResourceBundle rb = D3webUtils.getD3webBundle(context);
		MessageFormat mf = new MessageFormat("");
		String analysisResult = TestCaseExecutorUtils.renderTestAnalysisResult(t, result, rb, mf);
		string.appendHtml(analysisResult);

	}

	private void renderAutomated(Section<?> section, String master, String[] files, RenderResult html) {

		for (String file : files) {

			try {
				WikiConnector connector = Environment.getInstance().getWikiConnector();
				WikiAttachment attachment = connector.getAttachment(section.getArticle().getTitle()
						+ "/" + file);

				if (attachment != null) {
					html.appendHtml("<div>");
					html.append("Run testcases in file '" + file + "'.");
					html.appendHtml("</div>");
				}
			}
			catch (IOException e) {
				// ignore file
			}

		}

		html.appendHtml("<div id='testcases'>");
		html.appendHtml("<div class=\"runCasesButton\" onclick=\"return TestCaseExecutor.runTestcaseFromSection('"
				+ section.getID() + "')\"></div>");
		html.appendHtml("</div>");
	}

	/**
	 * 
	 * @created 16.09.2011
	 * @param section
	 * @param master
	 * @param result TODO
	 * @param string
	 */
	private void appendSelection(Section<?> section, String master, RenderResult html) {
		WikiConnector connector = Environment.getInstance().getWikiConnector();

		Collection<WikiAttachment> attachments;
		try {
			attachments = connector.getAttachments(section.getArticle().getTitle());
		}
		catch (IOException e) {
			attachments = Collections.emptyList();
		}

		// html.append("<h2 class=\"testExecutor\"> TestCase Executor </h2>");
		html.appendHtml("<p></p><div id=\"testcases\"><strong>Available Files with Testcases:</strong><br/><br/>");

		html.appendHtml("<select onChange=\"return TestCaseExecutor.getTestcases(" + null + ",'"
				+ master + "')\">");
		html.appendHtml("<option value=\"\">-- Choose file --</option>");
		for (WikiAttachment attachment : attachments) {
			if (attachment.getFileName().matches("stc.*\\.xml")) {
				html.appendHtml("<option value=\"" + attachment + "\">");
				html.append(attachment);
				html.appendHtml("</option>");

			}
		}

		html.appendHtml("</select>");
		html.appendHtml("</div>");
		html.appendHtml("<br />");
	}

}
