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
import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * @author Sebastian Furth (denkbares GmbH)
 * @created 03.11.14
 */
public class TableUtils {

	public static String getStoredContentForInput(Section<?> sec, int version, String username) {
		List<Section<InputFieldCellContent>> found = new ArrayList<Section<InputFieldCellContent>>();
		Sections.successors(sec.getParent().getParent().getParent().getParent(),
				InputFieldCellContent.class,
				found);
		int number = found.indexOf(sec);
		Section<DefaultMarkupType> ancestorOfType = Sections.ancestor(sec,
				DefaultMarkupType.class);
		String tableid = DefaultMarkupType.getAnnotation(ancestorOfType, "id");
		String contentString = getStoredContentString(number, tableid, version,
				username);
		return contentString;
	}

	public static String getStoredContentString(int number, String tableid, int version, String username) {

		Section<TableEntryType> contentTable = findTableToShow(tableid, username);

		String contentString = "";
		if (contentTable != null) {
			List<Section<VersionEntry>> versionBlocks = TableEntryType.getVersionBlocks(contentTable);
			if (versionBlocks.size() > 0) {
				Section<VersionEntry> versionBlock = versionBlocks.get(version);
				List<Section<ContentEntry>> entries = VersionEntry.getEntries(
						versionBlock);
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

	public static Section<TableEntryType> findTableToShow(String id, String username) {
		String dataArticleNameForUser = SubmitTableContentAction.getDataArticleNameForUser(username);
		Article article = KnowWEUtils.getArticleManager(
				Environment.DEFAULT_WEB).getArticle(dataArticleNameForUser);
		if (article == null) return null;
		List<Section<TableEntryType>> tables = new ArrayList<Section<TableEntryType>>();
		Sections.successors(article.getRootSection(),
				TableEntryType.class,
				tables);
		for (Section<TableEntryType> table : tables) {
			String tableID = DefaultMarkupType.getAnnotation(table, "tableid");
			if (tableID != null) {
				if (tableID.equals(id)) {
					return table;
				}
			}

		}
		return null;
	}

}
