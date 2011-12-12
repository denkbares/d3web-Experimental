package de.knowwe.defi.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.core.KnowWEArticleManager;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;

public class SubmitTableContentAction extends AbstractAction {

	public static final String TABLE_ID = "tableid";

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
			newContent.append(renderMarkupForOneVersion(versionCounter, map));
			versionCounter++;
		}
		newContent.append("-\n");
		return newContent.toString();
	}

	public static String renderMarkupForOneVersion(int versionCounter, Map<Integer, String> map) {
		StringBuffer buffy = new StringBuffer();
		buffy.append("VERSION" + versionCounter + "\n");
		for (Integer i : map.keySet()) {
			String text = map.get(i);
			buffy.append("INPUT" + i + ":" + text + "\n");
		}
		buffy.append("\n");
		return buffy.toString();
	}

	@Override
	public void execute(UserActionContext context) throws IOException {
		String data = context.getParameter("daten");
		String tableid = context.getParameter(TABLE_ID);

		String versionNumber = data.substring(0, data.indexOf('#'));
		int versions = 1;
		// read out leading version number
		try {
			versions = Integer.parseInt(versionNumber);
			if (versions == 0) versions = 1;
		}
		catch (Exception e) {
		}
		data = data.substring(data.indexOf('#') + 1); // cut away version number
														// in front

		// make up map for input data
		List<Map<Integer, String>> inputDataAll = buildInputData(data, versions);


		String username = context.getParameter("user");
		String defaultWeb = KnowWEEnvironment.DEFAULT_WEB;
		KnowWEArticleManager articleManager = KnowWEEnvironment.getInstance().getArticleManager(
				defaultWeb);
		String articleNameForData = getDataArticleNameForUser(username);
		KnowWEArticle knowWEArticle = articleManager.getArticle(
				articleNameForData);
		if (knowWEArticle == null) {
			// create new article
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
			Section<TableEntryContentType> contentSection = findContentSectionForTableID(
					tableid, knowWEArticle);
			Map<String, String> nodesMap = new HashMap<String, String>();
			if (contentSection == null) {
				// append entire block to page
				nodesMap.put(knowWEArticle.getSection().getID(),
						knowWEArticle.getSection().getText() + "\n"
								+ createNewMarkupString(
						tableid, inputDataAll));
			}
			else {
				// override content block
				nodesMap.put(contentSection.getID(), createMarkupContent(inputDataAll));
			}

			// submit change
			Sections.replaceSections(context,
					nodesMap);
		}

		context.getOutputStream().write(" (Wurde gespeichert)".getBytes());
	}

	private List<Map<Integer, String>> buildInputData(String data, int versions) {
		String[] inputs = data.split(";;");
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
		return inputDataAll;
	}

	public static Section<TableEntryContentType> findContentSectionForTableID(String tableid, KnowWEArticle knowWEArticle) {
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
		return contentSection;
	}

	public static String getDataArticleNameForUser(String username) {
		String articleNameForData = username + "_data";
		return articleNameForData;
	}
}
