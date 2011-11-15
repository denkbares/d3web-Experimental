package de.knowwe.d3web.proket.deploy;

import java.io.File;
import java.io.IOException;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.d3web.action.DownloadKnowledgeBase;

public class DeployAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String filename = context.getParameter(DownloadKnowledgeBase.PARAM_FILENAME);
		if (filename == null) filename = "knowledgebase.d3web";
		KnowledgeBase kb = D3webUtils.getKB(KnowWEEnvironment.DEFAULT_WEB, context.getWeb());
		PersistenceManager.getInstance().save(kb, new File("/EuraHS-Dialog/WEB-INF/classes/specs"));
	}

}
