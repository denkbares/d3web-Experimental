package de.d3web.we.testcaseexecutor;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.ResourceBundle;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.caseAnalysis.functions.TestCaseAnalysisReport;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.testcase.action.TestCaseRunAction;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.wikiConnector.ConnectorAttachment;
import de.knowwe.core.wikiConnector.KnowWEWikiConnector;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 11.05.2011
 */
public class TestCaseExecutorRender extends DefaultMarkupRenderer<TestCaseExecutorType> {


	@Override
	protected void renderContents(KnowWEArticle article, Section<TestCaseExecutorType> section, UserContext user, StringBuilder string) {

		String master = TestCaseExecutorType.getMaster(section);
		// no kb would cause massive amount of nullpointers
		KnowledgeBase kb = D3webUtils.getKB(user.getWeb(), master);
		if (kb == null) {
			string.append(KnowWEUtils.maskHTML("<div id=\"testcases\">No Knowledgebase found on "
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
	private void renderResult(TestCaseAnalysisReport report, StringBuilder string, Section<TestCaseExecutorType> section, UserContext context) {
		TestCaseAnalysisReport result = (TestCaseAnalysisReport) section.getSectionStore().getObject(
				TestCaseExecutorType.TEST_RESULT_KEY);
		TestCase t = (TestCase) section.getSectionStore().getObject(
				TestCaseExecutorType.TESTCASE_KEY);

		ResourceBundle rb = D3webModule.getKwikiBundle_d3web(context);
		MessageFormat mf = new MessageFormat("");
		String analysisResult = TestCaseRunAction.renderTestAnalysisResult(t, result, rb, mf);
		string.append(KnowWEUtils.maskHTML(analysisResult));

	}

	/**
	 * 
	 * @created 16.09.2011
	 * @param section
	 * @param string
	 * @param master
	 * @param file
	 */
	private String renderAutomated(Section<TestCaseExecutorType> section, String master, String[] files) {

		StringBuilder html = new StringBuilder();

		for (String file : files) {

			KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();
			ConnectorAttachment attachment = connector.getAttachment(section.getArticle().getTitle()
					+ "/" + file);

			if (attachment != null) {
				html.append("<div>Run testcases in file '" + file + "'.</div>");
			}

		}

		html.append("<div id='testcases'>");
		html.append("<div class=\"runCasesButton\" onclick=\"return TestCaseExecutor.runTestcaseFromSection('"
				+ section.getID() + "')\"></div>");
		html.append("</div>");
		return KnowWEUtils.maskHTML(html.toString());
	}

	/**
	 * 
	 * @created 16.09.2011
	 * @param section
	 * @param string
	 * @param master
	 * @return
	 */
	private String renderSelection(Section<TestCaseExecutorType> section, String master) {
		KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();

		Collection<String> attachments = connector.getAttachmentFilenamesForPage(section.getArticle().getTitle());

		StringBuilder html = new StringBuilder();
		// html.append("<h2 class=\"testExecutor\"> TestCase Executor </h2>");
		html.append("<p></p><div id=\"testcases\"><strong>Available Files with Testcases:</strong><br/><br/>");

		html.append("<select onChange=\"return TestCaseExecutor.getTestcases(" + null + ",'"
				+ master + "')\">");
		html.append("<option value=\"\">-- Choose file --</option>");
		for (String attachment : attachments) {
			if (attachment.matches("stc.*\\.xml")) {
				html.append("<option value=\"" + attachment + "\">" + attachment + "</option>");

			}
		}

		html.append("</select>");
		html.append("</div>");
		html.append("<br />");

		return KnowWEUtils.maskHTML(html.toString());
	}

}
