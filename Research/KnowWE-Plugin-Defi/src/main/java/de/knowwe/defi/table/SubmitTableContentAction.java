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

public class SubmitTableContentAction extends AbstractAction {

	private String createNewMarkupString(String tableid, Map<Integer, String> inputData) {
		StringBuffer newContent = new StringBuffer();
		newContent.append("%%Tabellendaten\n");
		newContent.append(createMarkupContent(inputData));
		newContent.append("@tableid:" + tableid + "\n");
		newContent.append("%\n");
		return newContent.toString();
	}

	private String createMarkupContent(Map<Integer, String> inputData) {
		StringBuffer newContent = new StringBuffer();
		for (Integer i : inputData.keySet()) {
			String text = inputData.get(i);
			newContent.append("INPUT" + i + ":" + text + "\n");
		}
		return newContent.toString();
	}

	@Override
	public void execute(UserActionContext context) throws IOException {
		String username = context.getParameter("user");
		String data = context.getParameter("data");
		String tableid = context.getParameter("tableid");

		// make up map for input data
		String[] inputs = data.split(";");
		Map<Integer, String> inputData = new HashMap<Integer, String>();
		for (String string : inputs) {
			String number = string.substring(5, string.indexOf(':'));
			Integer i = Integer.parseInt(number.trim());
			String text = string.substring(string.indexOf(':') + 1);
			inputData.put(i, text);
		}

		String defaultWeb = KnowWEEnvironment.DEFAULT_WEB;
		KnowWEArticleManager articleManager = KnowWEEnvironment.getInstance().getArticleManager(
				defaultWeb);
		String articleNameForData = username + "_data";
		KnowWEArticle knowWEArticle = articleManager.getArticle(
				articleNameForData);
		if (knowWEArticle == null) {
			String newContent = createNewMarkupString(tableid, inputData);
			KnowWEEnvironment.getInstance().getWikiConnector().createWikiPage(
					articleNameForData, newContent.toString(), "Defi-system");
			KnowWEArticle article = KnowWEArticle.createArticle(newContent.toString(),
					articleNameForData, KnowWEEnvironment.getInstance().getRootType(),
					defaultWeb, true);

			KnowWEEnvironment.getInstance().getArticleManager(
					defaultWeb)
					.registerArticle(article);
			knowWEArticle = articleManager.getArticle(
					articleNameForData);
		}
		else {
			Map<String, String> nodesMap = new HashMap<String, String>();
			List<Section<TableEntryType>> tables = new ArrayList<Section<TableEntryType>>();
			Sections.findSuccessorsOfType(knowWEArticle.getSection(),
					TableEntryType.class, tables);
			Section<TableEntryContentType> contentSection = null;
			for (Section<TableEntryType> section : tables) {
				String id = section.get().getAnnotation(section, "tableid");
				if (id.equals(tableid)) {
					contentSection = Sections.findSuccessor(section,
							TableEntryContentType.class);
				}
			}
			if (contentSection == null) {
				nodesMap.put(knowWEArticle.getSection().getID(), createNewMarkupString(
						tableid, inputData));
			}
			else {
				nodesMap.put(contentSection.getID(), createMarkupContent(inputData));
			}

			articleManager.replaceKDOMNodesSaveAndBuild(context,
					articleNameForData, nodesMap);
		}

	}
}
