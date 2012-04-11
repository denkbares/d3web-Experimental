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
package de.knowwe.defi.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.wikiConnector.WikiConnector;

/**
 * 
 * @author Jochen
 * @created 08.03.2012
 */
public class ReplaceSectionUtils {

	public static void replaceSections(UserActionContext context, Map<String, String> sectionsMap) throws IOException {

		Map<String, Collection<String>> idsByTitle = getIdsByTitle(sectionsMap.keySet());

		for (String title : idsByTitle.keySet()) {
			Collection<String> ids = idsByTitle.get(title);
			replaceSectionsForTitle(title, getSectionsMapForCurrentTitle(ids,
					sectionsMap), context);
		}

	}

	private static Map<String, String> getSectionsMapForCurrentTitle(
			Collection<String> ids,
			Map<String, String> sectionsMap) {

		Map<String, String> sectionsMapForCurrentTitle = new HashMap<String, String>();
		for (String id : ids) {
			sectionsMapForCurrentTitle.put(id, sectionsMap.get(id));
		}
		return sectionsMapForCurrentTitle;
	}

	private static void replaceSectionsForTitle(String title,
			Map<String, String> sectionsMapForCurrentTitle,
			UserActionContext context) {

		WikiConnector wikiConnector = Environment.getInstance().getWikiConnector();

		String newArticleText = getNewArticleText(title, sectionsMapForCurrentTitle,
				context);
		wikiConnector.writeArticleToWikiEnginePersistence(title, newArticleText, context);

	}

	private static String getNewArticleText(
			String title,
			Map<String, String> sectionsMapForCurrentTitle,
			UserActionContext context) {

		StringBuffer newText = new StringBuffer();
		Article article = Environment.getInstance().getArticle(
				context.getWeb(),
				title);
		collectTextAndReplaceNode(article.getRootSection(), sectionsMapForCurrentTitle,
				newText);
		return newText.toString();
	}

	private static void collectTextAndReplaceNode(Section<?> sec,
			Map<String, String> nodesMap, StringBuffer newText) {

		String text = nodesMap.get(sec.getID());
		if (text != null) {
			newText.append(text);
			return;
		}

		List<Section<?>> children = sec.getChildren();
		if (children == null || children.isEmpty()
				|| sec.hasSharedChildren()) {
			newText.append(sec.getText());
			return;
		}
		for (Section<?> section : children) {
			collectTextAndReplaceNode(section, nodesMap, newText);
		}
	}

	private static Map<String, Collection<String>> getIdsByTitle(Collection<String> allIds) {
		Map<String, Collection<String>> idsByTitle = new HashMap<String, Collection<String>>();
		for (String id : allIds) {
			Section<?> section = Sections.getSection(id);
			String title = section == null ? null : section.getTitle();
			Collection<String> ids = idsByTitle.get(title);
			if (ids == null) {
				ids = new ArrayList<String>();
				idsByTitle.put(title, ids);
			}
			ids.add(id);
		}
		return idsByTitle;
	}

}
