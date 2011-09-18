package de.d3web.we.testcaseexecutor;

import java.util.Collection;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.D3webUtils;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.ConnectorAttachment;
import de.d3web.we.wikiConnector.KnowWEWikiConnector;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 11.05.2011
 */
public class TestCaseExecutorRender extends DefaultMarkupRenderer<TestCaseExecutorType> {

	public static final String TESTRESULT = "testcaseexecutorresult";

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

		
		String file = DefaultMarkupType.getAnnotation(section, TestCaseExecutorType.ANNOTATION_FILE);
		
		if (file == null) {
			string.append(renderSelection(section, master));
		} else {
			string.append(renderAutomated(section, master, file));
		}

	}

	/**
	 * 
	 * @created 16.09.2011
	 * @param section
	 * @param string
	 * @param master
	 * @param file TODO
	 */
	private String renderAutomated(Section<TestCaseExecutorType> section, String master, String file) {

		KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();
		ConnectorAttachment attachment = connector.getAttachment(section.getArticle().getTitle()
				+ "/" + file);

		if (attachment == null) {
			return KnowWEUtils.maskHTML("Testcase not found '" + file + "'.");
		}


		StringBuilder html = new StringBuilder();
		html.append("<div id=\"testcases\">Run testcases in file '" + file + "'.</div>");
		html.append("<div class=\"runCasesButton\" onclick=\"return TestCaseExecutor.runTestcaseFromSection('"
				+ section.getID() + "')\"></div>");
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
