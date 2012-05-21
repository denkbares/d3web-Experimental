package de.d3web.we.testcaseexecutor;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysisReport;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
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
	protected void renderContents(Section<?> section, UserContext user, StringBuilder string) {

		String master = TestCaseExecutorType.getMaster(section);
		// no kb would cause massive amount of nullpointers
		KnowledgeBase kb = D3webUtils.getKnowledgeBase(user.getWeb(), master);
		if (kb == null) {
			string.append(Strings.maskHTML("<div id=\"testcases\">No Knowledgebase found on "
					+ master + "</div>"));
			return;
		}

		TestCaseAnalysisReport report = (TestCaseAnalysisReport) section.getSectionStore().getObject(
				TestCaseExecutorType.TEST_RESULT_KEY);
		if (report != null) { // test case has already been executed
			renderResult(report, string, section, user);
		}
		else {

			String[] files = DefaultMarkupType.getAnnotations(section,
					TestCaseExecutorType.ANNOTATION_FILE);

			if (files.length == 0) {
				string.append(renderSelection(section, master));
			}
			else {
				string.append(renderAutomated(section, master, files));
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
	private void renderResult(TestCaseAnalysisReport report, StringBuilder string, Section<?> section, UserContext context) {
		TestCaseAnalysisReport result = (TestCaseAnalysisReport) section.getSectionStore().getObject(
				TestCaseExecutorType.TEST_RESULT_KEY);
		TestCase t = (TestCase) section.getSectionStore().getObject(
				TestCaseExecutorType.TESTCASE_KEY);

		ResourceBundle rb = D3webUtils.getD3webBundle(context);
		MessageFormat mf = new MessageFormat("");
		String analysisResult = TestCaseExecutorUtils.renderTestAnalysisResult(t, result, rb, mf);
		string.append(Strings.maskHTML(analysisResult));

	}

	/**
	 * 
	 * @created 16.09.2011
	 * @param section
	 * @param string
	 * @param master
	 * @param file
	 */
	private String renderAutomated(Section<?> section, String master, String[] files) {

		StringBuilder html = new StringBuilder();

		for (String file : files) {

			try {
				WikiConnector connector = Environment.getInstance().getWikiConnector();
				WikiAttachment attachment = connector.getAttachment(section.getArticle().getTitle()
						+ "/" + file);

				if (attachment != null) {
					html.append("<div>Run testcases in file '" + file + "'.</div>");
				}
			}
			catch (IOException e) {
				// ignore file
			}

		}

		html.append("<div id='testcases'>");
		html.append("<div class=\"runCasesButton\" onclick=\"return TestCaseExecutor.runTestcaseFromSection('"
				+ section.getID() + "')\"></div>");
		html.append("</div>");
		return Strings.maskHTML(html.toString());
	}

	/**
	 * 
	 * @created 16.09.2011
	 * @param section
	 * @param string
	 * @param master
	 * @return
	 */
	private String renderSelection(Section<?> section, String master) {
		WikiConnector connector = Environment.getInstance().getWikiConnector();

		Collection<WikiAttachment> attachments;
		try {
			attachments = connector.getAttachments(section.getArticle().getTitle());
		}
		catch (IOException e) {
			attachments = Collections.emptyList();
		}

		StringBuilder html = new StringBuilder();
		// html.append("<h2 class=\"testExecutor\"> TestCase Executor </h2>");
		html.append("<p></p><div id=\"testcases\"><strong>Available Files with Testcases:</strong><br/><br/>");

		html.append("<select onChange=\"return TestCaseExecutor.getTestcases(" + null + ",'"
				+ master + "')\">");
		html.append("<option value=\"\">-- Choose file --</option>");
		for (WikiAttachment attachment : attachments) {
			if (attachment.getFileName().matches("stc.*\\.xml")) {
				html.append("<option value=\"" + attachment + "\">" + attachment + "</option>");

			}
		}

		html.append("</select>");
		html.append("</div>");
		html.append("<br />");

		return Strings.maskHTML(html.toString());
	}

}
