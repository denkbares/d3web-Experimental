package de.knowwe.d3web.proket.deploy;

import java.io.File;
import java.io.IOException;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.d3web.action.DownloadKnowledgeBase;

public class DeployAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String filename = context.getParameter(DownloadKnowledgeBase.PARAM_FILENAME);
		String title = context.getParameter(Attributes.TOPIC);
		if (filename == null) filename = "knowledgebase.d3web";
		if (title == null) title = "Main";
		KnowledgeBase kb = D3webUtils.getKnowledgeBase(Environment.DEFAULT_WEB, title);
		PersistenceManager.getInstance().save(kb,
				new File("webapps/EuraHS-Dialog/WEB-INF/classes/specs/d3web/" + filename));
		context.sendRedirect("/KnowWE/");
	}
}
