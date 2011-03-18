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

	private String createNewMarkupString(String tableid, List<Map<Integer, String>> inputData) {
		StringBuffer newContent = new StringBuffer();
		newContent.append("%%Tabellendaten\n");
		newContent.append(createMarkupContent(inputData));
		newContent.append("@tableid:" + tableid + "\n");
		newContent.append("%\n");
		return newContent.toString();
	}

	private String createMarkupContent(List<Map<Integer, String>> inputData) {
		StringBuffer newContent = new StringBuffer();
		int versionCounter = 0;
		for (Map<Integer, String> map : inputData) {
			newContent.append("VERSION" + versionCounter + "\n");
			for (Integer i : map.keySet()) {
				String text = map.get(i);
				newContent.append("INPUT" + i + ":" + text + "\n");
			}
			versionCounter++;
			newContent.append("\n");
		}
		newContent.append("-\n");
		return newContent.toString();
	}

	@Override
	public void execute(UserActionContext context) throws IOException {
		String username = context.getParameter("user");
		String data = context.getParameter("data");
		String tableid = context.getParameter("tableid");

		String versionNumber = data.substring(0, data.indexOf('#'));
		int versions = 1;
		// read out leading version number
		try {
			versions = Integer.parseInt(versionNumber);
		}
		catch (Exception e) {
		}
		data = data.substring(data.indexOf('#') + 1); // cut away version number
														// in front

		// make up map for input data
		String[] inputs = data.split(";");
		int inputsByVersion = inputs.length / versions;
		List<Map<Integer, String>> inputDataAll = new ArrayList<Map<Integer, String>>();
		for (int i = 0; i < versions; i++) {
			Map<Integer, String> inputDataOneVersion = new HashMap<Integer, String>();
			for (int k = 0; k < inputsByVersion; k++) {
				String string = inputs[i * inputsByVersion + k];
				String number = string.substring(5, string.indexOf(':'));
				Integer inputNumber = Integer.parseInt(number.trim()) % inputsByVersion;
				String text = string.substring(string.indexOf(':') + 1);
				inputDataOneVersion.put(inputNumber, text);
			}
			inputDataAll.add(inputDataOneVersion);
		}

		String defaultWeb = KnowWEEnvironment.DEFAULT_WEB;
		KnowWEArticleManager articleManager = KnowWEEnvironment.getInstance().getArticleManager(
				defaultWeb);
		String articleNameForData = username + "_data";
		KnowWEArticle knowWEArticle = articleManager.getArticle(
				articleNameForData);
		if (knowWEArticle == null) {
			String newContent = createNewMarkupString(tableid, inputDataAll);
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
						tableid, inputDataAll));
			}
			else {
				nodesMap.put(contentSection.getID(), createMarkupContent(inputDataAll));
			}

			articleManager.replaceKDOMNodesSaveAndBuild(context,
					articleNameForData, nodesMap);
		}

		context.getOutputStream().write(" (Wurde gespeichert)".getBytes());
	}
}
