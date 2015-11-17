/*
 * Copyright (C) 2013 denkbares GmbH
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * Util methods for handling DEFI tables
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 03.11.14
 */
public class TableUtils {

	public static String getStoredContentForTable(Section<TableEntryType> table) {
		if (table != null) {
			List<Section<ContentEntry>> entries = Sections.successors(table, ContentEntry.class);
			if (entries != null && !entries.isEmpty()) {
				Section<ContentEntry> lastEntry = entries.get(entries.size() - 1);
				return ContentEntry.getContent(lastEntry);
			}
		}
		return null;
	}

	public static String getStoredContentForInput(Section<?> sec, int version, String username) {
		List<Section<InputFieldCellContent>> found = new ArrayList<>();
		Sections.successors(sec.getParent().getParent().getParent().getParent(), InputFieldCellContent.class, found);
		int number = found.indexOf(sec);
		Section<DefaultMarkupType> ancestorOfType = Sections.ancestor(sec, DefaultMarkupType.class);
		String tableid = DefaultMarkupType.getAnnotation(ancestorOfType, "id");
		return getStoredContentString(number, tableid, version, username);
	}

	public static String getStoredContentString(int number, String tableid, int version, String username) {
		Section<TableEntryType> contentTable = findTableEntry(tableid, username);
		String contentString = "";
		if (contentTable != null) {
			List<Section<VersionEntry>> versionBlocks = TableEntryType.getVersionBlocks(contentTable);
			if (versionBlocks.size() > 0) {
				Section<VersionEntry> versionBlock = versionBlocks.get(version);
				List<Section<ContentEntry>> entries = VersionEntry.getEntries(versionBlock);
				for (Section<ContentEntry> section : entries) {
					if (ContentEntry.getNumber(section) == number) {
						contentString = ContentEntry.getContent(section);
						break;
					}
				}
			}
		}
		return contentString;
	}

	public static Section<TableEntryType> findTableEntry(String id, String username) {
		String dataArticleNameForUser = SubmitTableContentAction.getDataArticleNameForUser(username);
		Article article = KnowWEUtils.getArticleManager(Environment.DEFAULT_WEB).getArticle(dataArticleNameForUser);
		if (article == null) return null;
		List<Section<TableEntryType>> tables = new ArrayList<>();
		Sections.successors(article.getRootSection(), TableEntryType.class, tables);
		for (Section<TableEntryType> table : tables) {
			String tableID = DefaultMarkupType.getAnnotation(table, "tableid");
			if (tableID != null && tableID.equals(id)) {
				return table;
			}
		}
		return null;
	}

	public static Section<DefineTableMarkup> findTableDefintion(String id) {
		Collection<Article> articles = KnowWEUtils.getArticleManager(Environment.DEFAULT_WEB).getArticles();
		for (Article article : articles) {
			List<Section<DefineTableMarkup>> tables = new ArrayList<>();
			Sections.successors(article.getRootSection(), DefineTableMarkup.class, tables);
			for (Section<DefineTableMarkup> table : tables) {
				String tableID = DefaultMarkupType.getAnnotation(table, "id");
				if (tableID != null && tableID.equals(id)) {
					return table;
				}
			}
		}
		return null;
	}

}
