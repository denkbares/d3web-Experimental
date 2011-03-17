/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.defi.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.StringSectionFinder;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class InputFieldCellContent extends AbstractType {

	public InputFieldCellContent() {
		this.setSectionFinder(new StringSectionFinder("INPUT"));
		this.setCustomRenderer(new InputRenderer());
	}

	static class InputRenderer extends KnowWEDomRenderer<InputFieldCellContent> {

		@Override
		public void render(KnowWEArticle article,
				Section<InputFieldCellContent> sec, UserContext user,
				StringBuilder string) {
			String contentString = getStoredContentForInput(sec);
			string.append(KnowWEUtils.maskHTML("<textarea rows='3' wrap='soft' type='text' id='"
					+ sec.getID()
					+ "'>" + contentString + "</textarea>"));

		}

		public static String getStoredContentForInput(Section<InputFieldCellContent> sec) {
			List<Section<InputFieldCellContent>> found = new ArrayList<Section<InputFieldCellContent>>();
			Sections.findSuccessorsOfType(sec.getFather().getFather().getFather(),
					InputFieldCellContent.class,
					found);
			int number = found.indexOf(sec);
			Section<DefaultMarkupType> ancestorOfType = Sections.findAncestorOfType(sec,
					DefaultMarkupType.class);
			String tableid = ancestorOfType.get().getAnnotation(ancestorOfType, "id");
			String contentString = getStoredContentString(number, tableid);
			return contentString;
		}

		private static String getStoredContentString(int number, String tableid) {
			String contentString = "-";
			Section<TableEntryType> contentTable = findTableToShow(tableid);
			if (contentTable != null) {
				List<Section<ContentEntry>> entries = contentTable.get().getEntries(
						contentTable);
				for (Section<ContentEntry> section : entries) {
					if (section.get().getNumber(section) == number) {
						contentString = section.get().getContent(section);
						break;
					}
				}
			}
			return contentString;
		}

		private static Section<TableEntryType> findTableToShow(String id) {
			Collection<KnowWEArticle> articles = KnowWEEnvironment.getInstance().getArticleManager(
					KnowWEEnvironment.DEFAULT_WEB).getArticles();
			for (KnowWEArticle knowWEArticle : articles) {
				List<Section<TableEntryType>> tables = new ArrayList<Section<TableEntryType>>();
				Sections.findSuccessorsOfType(knowWEArticle.getSection(),
						TableEntryType.class,
						tables);
				for (Section<TableEntryType> table : tables) {
					String tableID = table.get().getAnnotation(table, "tableid");
					if (tableID != null) {
						if (tableID.equals(id)) {
							return table;
						}
					}

				}
			}
			return null;
		}
	}

}
