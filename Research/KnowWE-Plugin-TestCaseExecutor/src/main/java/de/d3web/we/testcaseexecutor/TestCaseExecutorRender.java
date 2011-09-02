package de.d3web.we.testcaseexecutor;

import java.util.Collection;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
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
public class TestCaseExecutorRender extends KnowWEDomRenderer<TestCaseExecutorType> {

	@Override
	public void render(KnowWEArticle article, Section<TestCaseExecutorType> sec, UserContext user, StringBuilder string) {

		String articleName = user.getTopic();

		String master = TestCaseExecutorType.getMaster(sec);

		if (master == null) {
			master = user.getTopic();
		}

		// no kb would cause massive amount of nullpointers
		KnowledgeBase kb = D3webUtils.getKB(user.getWeb(), master);
		if (kb == null) {
			string.append(KnowWEUtils.maskHTML("<div id=\"testcases\">No Knowledgebase found on "
					+ master + "</div>"));
			return;
		}

		KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();

		Collection<ConnectorAttachment> attachments = connector.getAttachments();

		StringBuilder html = new StringBuilder();
		html.append("<h2 class=\"testExecutor\"> TestCase Executor </h2>");
		html.append("<p></p><div id=\"testcases\"><strong>Available Files with Testcases:</strong><br /><br />");

		html.append("<select onChange=\"return TestCaseExecutor.getTestcases(" + null + ",'"
				+ master + "')\">");
		html.append("<option value=\"\">-- Choose file --</option>");
		for (ConnectorAttachment attachment : attachments) {
			if (attachment.getParentName().equals(articleName)
					&& attachment.getFileName().matches("stc.*.xml")) {
				String name = attachment.getFileName();

				html.append("<option value=\"" + name + "\">" + name + "</option>");

			}
		}
		html.append("</select>");
		html.append("</div>");
		html.append("<br />");


		string.append(KnowWEUtils.maskHTML(html.toString()));

	}

}
