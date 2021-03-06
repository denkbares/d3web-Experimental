/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.defi.table;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.defi.utils.ReplaceSectionUtils;
import de.knowwe.kdom.defaultMarkup.AnnotationContentType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class SubmitTableContentAction extends AbstractAction {

	public static final String TABLE_ID = "tableid";

	private String createNewMarkupString(String tableid, List<Map<Integer, String>> inputData) {
		StringBuilder newContent = new StringBuilder();
		String date = (new SimpleDateFormat("dd.MM.yyy, HH:mm")).format(new Date());
		newContent.append("%%Tabellendaten\n");
		newContent.append(createMarkupContent(inputData)).append("\n");
		newContent.append("@date:").append(date).append("\n");
		newContent.append("@tableid:").append(tableid).append("\n");
		newContent.append("%\n");
		return newContent.toString();
	}

	private String createMarkupContent(List<Map<Integer, String>> inputData) {
		StringBuilder newContent = new StringBuilder();
		int versionCounter = 0;
		for (Map<Integer, String> map : inputData) {
			newContent.append(renderMarkupForOneVersion(versionCounter, map));
			versionCounter++;
		}
		newContent.append("-\n");
		return newContent.toString();
	}

	public static String renderMarkupForOneVersion(int versionCounter, Map<Integer, String> map) {
		StringBuilder buffy = new StringBuilder();
		buffy.append("VERSION").append(versionCounter).append("\n");
		for (Integer i : map.keySet()) {
			String text = map.get(i);
			buffy.append("INPUT").append(i).append(":").append(text).append("\n");
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
		String defaultWeb = Environment.DEFAULT_WEB;
		ArticleManager articleManager = KnowWEUtils.getArticleManager(
				defaultWeb);
		String articleNameForData = getDataArticleNameForUser(username);
		Article dataArticle = articleManager.getArticle(
				articleNameForData);
		if (dataArticle == null) {
			// create new article
			String newContent = createNewMarkupString(tableid, inputDataAll);
			newContent = "[{ALLOW view admin}]\n\n" + newContent;
			Environment.getInstance().getWikiConnector().createArticle(
					articleNameForData, "Defi-system", newContent);
			Article article = Article.createArticle(newContent,
					articleNameForData, defaultWeb, true);

			KnowWEUtils.getArticleManager(
					defaultWeb)
					.registerArticle(article);
			dataArticle = articleManager.getArticle(
					articleNameForData);
		}
		else {
			Section<TableEntryContentType> contentSection = findContentSectionForTableID(
					tableid, dataArticle);
			Map<String, String> nodesMap = new HashMap<>();
			if (contentSection == null) {
				// append entire block to page
				nodesMap.put(dataArticle.getRootSection().getID(),
						dataArticle.getRootSection().getText() + "\n"
								+ createNewMarkupString(
										tableid, inputDataAll));
			}
			else {
				// override content block
				nodesMap.put(contentSection.getID(), createMarkupContent(inputDataAll));
				// override date
				String date = (new SimpleDateFormat("dd.MM.yyy, HH:mm")).format(new Date());
				Section<? extends AnnotationContentType> dateSec = DefaultMarkupType.getAnnotationContentSection(
						contentSection.getParent().getParent(), "date");
				nodesMap.put(dateSec.getID(), date);
			}

			// submit change
			ReplaceSectionUtils.replaceSections(context, nodesMap);
			// Sections.replace(context, nodesMap);
		}

		context.getOutputStream().write(" (Wurde gespeichert)".getBytes());
	}

	private List<Map<Integer, String>> buildInputData(String data, int versions) {
		String[] inputs = data.split(";;");
		int inputsByVersion = inputs.length / versions;
		List<Map<Integer, String>> inputDataAll = new ArrayList<>();
		for (int i = 0; i < versions; i++) {
			Map<Integer, String> inputDataOneVersion = new HashMap<>();
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

	public static Section<TableEntryContentType> findContentSectionForTableID(String tableid, Article article) {
		List<Section<TableEntryType>> tables = new ArrayList<>();
		Sections.successors(article.getRootSection(),
				TableEntryType.class, tables);
		Section<TableEntryContentType> contentSection = null;
		for (Section<TableEntryType> section : tables) {
			String id = DefaultMarkupType.getAnnotation(section, "tableid");
			if (id.equals(tableid)) {
				contentSection = Sections.successor(section,
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
