package de.knowwe.defi.table;

import java.util.HashMap;
import java.util.Map;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;

public class SubmitTableContentAction extends AbstractAction {

	public void execute(UserActionContext context) {
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
			String newContent = createNewContentString(tableid, inputData);
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
		nodesMap.put(knowWEArticle.getSection().getID(), createNewContentString(tableid, inputData));
		articleManager.replaceKDOMNodesSaveAndBuild(context.getKnowWEParameterMap(),
				articleNameForData, nodesMap);
		}

	}

	private String createNewContentString(String tableid, Map<Integer, String> inputData) {
		StringBuffer newContent = new StringBuffer();
		newContent.append("%%Tabellendaten\n");
		for (Integer i : inputData.keySet()) {
			String text = inputData.get(i);
			newContent.append("@input" + i + ":" + text + "\n");

		}
		newContent.append("@tableid:" + tableid + "\n");
		newContent.append("%\n");
		return newContent.toString();
	}

}
