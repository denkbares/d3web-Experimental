package de.d3web.we.testcaseexecutor;

import java.util.Collection;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
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

		String articleName = user.getParameter("page");

		String master = TestCaseExecutorType.getMaster(sec);

		if (master == null) {
			master = user.getParameter("page");
		}

		KnowWEWikiConnector connector = KnowWEEnvironment.getInstance().getWikiConnector();

		Collection<ConnectorAttachment> attachments = connector.getAttachments();

		StringBuilder html = new StringBuilder();
		html.append("<h3 class=\"testExecutor\"> TestCase Executor </h3>");
		html.append("<br /><div id=\"testcases\"><strong>Available Files with Testcases:</strong><br />");

		for (ConnectorAttachment attachment : attachments) {
			if (attachment.getParentName().equals(articleName)
					&& attachment.getFileName().endsWith(".xml")) {
				String name = attachment.getFileName();
				html.append("<div class=\"selectXMLFile\" onclick=\"return TestCaseExecutor.getTestcases('"
						+ name + "','" + master + "')\">"
						+ name + "</div>");
			}
		}

		html.append("</div>");
		html.append("<br />");
		// html.append("<div>@master ");
		// html.append(master);
		// html.append("</div>");

		string.append(KnowWEUtils.maskHTML(html.toString()));

	}

}
