package de.knowwe.defi.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;

public class InsertAdditionalTableVersionAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String username = context.getUserName();
		String articleName = SubmitTableContentAction.getDataArticleNameForUser(username);
		KnowWEArticleManager articleManager = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB);
		KnowWEArticle knowWEArticle = articleManager.getArticle(
				articleName);

		// find table for tableid
		String tableid = context.getParameter(SubmitTableContentAction.TABLE_ID);
		Section<TableEntryContentType> contentSectionForTableID = SubmitTableContentAction.findContentSectionForTableID(
				tableid, knowWEArticle);

		// find version blocks
		List<Section<VersionEntry>> versionEntries = new ArrayList<Section<VersionEntry>>();
		Sections.findSuccessorsOfType(contentSectionForTableID, VersionEntry.class,
				versionEntries);
		int numberOfExistingVersions = versionEntries.size();

		// find out number of inputs to generate
		Section<VersionEntry> version0 = versionEntries.get(0);
		List<Section<InputContent>> inputEntries = new ArrayList<Section<InputContent>>();
		Sections.findSuccessorsOfType(version0, InputContent.class, inputEntries);
		int numberOfInputs = inputEntries.size();

		// generate empty inputs
		Map<Integer, String> emptyInputData = new HashMap<Integer, String>();
		for (int i = 0; i < numberOfInputs; i++) {
			emptyInputData.put(new Integer(i), "-");
		}

		// create new markup for empty input data
		String newContent = SubmitTableContentAction.renderMarkupForOneVersion(
				numberOfExistingVersions, emptyInputData);

		// insert new markup into page
		Map<String, String> nodesMap = new HashMap<String, String>();
		String oldText = contentSectionForTableID.getText();
		String completeNewText = oldText.substring(0,
				oldText.length() - 2)
				+ newContent + "\n-";
		nodesMap.put(contentSectionForTableID.getID(),
				completeNewText);
		articleManager.replaceKDOMNodesSaveAndBuild(context,
				articleName, nodesMap);
	}

}
