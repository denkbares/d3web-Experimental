package de.knowwe.onte.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.manchester.compile.utils.ImportedOntologyManager;

public class OnteRemoveImportedOntologyAction extends AbstractAction {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(UserActionContext context) throws IOException {

		String sectionID = context.getParameter("section");

		if (sectionID != null && !sectionID.trim().isEmpty()) {
			Section<? extends AbstractType> section = (Section<? extends AbstractType>) Sections.getSection(sectionID);
			String articleName = section.getTitle();

			// ... lock the page to avoid changes by other users ...
			boolean isPageLocked = Environment.getInstance().getWikiConnector()
										.isPageLocked(articleName);

			if (isPageLocked) {
				context.sendError(
						403,
						"I am sorry. The page is being edited by another user. Please try again later.");
				return;
			}
			else {
				Environment.getInstance().getWikiConnector().setPageLocked(articleName,
							context.getUserName());

				// ArticleManager mgr =
				// Environment.getInstance().getArticleManager(
				// context.getWeb());

				Map<String, String> nodesMap = new HashMap<String, String>();
				nodesMap.put(section.getID(), "");
				Sections.replaceSections(context, nodesMap);
				// mgr.replaceKDOMNodesSaveAndBuild(context, articleName,
				// nodesMap);

				Environment.getInstance().getWikiConnector().undoPageLocked(articleName);
				context.getWriter().write("{msg : 'import deleted', success : 'true'}");
				ImportedOntologyManager.getInstance().removeOntology(section);
			}
		}
		else {
			context.sendError(403,
					"Could not find the ontology! The id of the section got lost. Sorry!");
			return;
		}
	}
}