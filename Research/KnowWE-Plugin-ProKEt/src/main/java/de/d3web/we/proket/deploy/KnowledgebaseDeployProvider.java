package de.d3web.we.proket.deploy;

import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.d3web.action.DownloadKnowledgeBase;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

public class KnowledgebaseDeployProvider implements ToolProvider {

	@Override
	public Tool[] getTools(KnowWEArticle article, Section<?> section, UserContext userContext) {
		// and provide both download and refresh as tools
		Tool deploy = getDeployTool(article, section, userContext);
		return new Tool[] { deploy };
	}

	protected Tool getDeployTool(KnowWEArticle article, Section<?> section, UserContext userContext) {
		// tool to provide download capability
		String kbName = DefaultMarkupType.getContent(section).trim();
		if (kbName.isEmpty()) {
			kbName = "knowledgebase";
		}
		String jsAction = "window.location='action/DeployAction" +
				"?" + KnowWEAttributes.TOPIC + "=" + article.getTitle() +
				"&" + KnowWEAttributes.WEB + "=" + article.getWeb() +
				"&" + DownloadKnowledgeBase.PARAM_FILENAME + "=" + kbName + ".d3web'";
		return new DefaultTool(
				"KnowWEExtension/images/export_wiz.gif",
				"Deploy",
				"Deploys the current knowledge base to the EuraHS-Dialog.",
				jsAction);
	}

}
