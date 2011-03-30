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
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class InputFieldCellContent extends AbstractType {

	public InputFieldCellContent() {
		this.setSectionFinder(new RegexSectionFinder("INPUT(\\(.*?\\))?", Pattern.DOTALL));
		this.setCustomRenderer(new InputRenderer());
	}

	public static int getWidth(Section<InputFieldCellContent> sec) {
		String originalText = sec.getOriginalText();
		int width;
		width = readParamterNo(originalText, 0);
		if (width == -1) width = 20; // set default
		return width;
	}

	public static int getHeight(Section<InputFieldCellContent> sec) {
		String originalText = sec.getOriginalText();
		int height;
		height = readParamterNo(originalText, 1);
		if (height == -1) height = 3; // set default
		return height;
	}

	private static int readParamterNo(String originalText, int no) {
		if (originalText.contains("(") && originalText.endsWith(")")) {
			String content = originalText.substring(originalText.indexOf("(") + 1,
					originalText.length() - 1);
			String[] split = content.trim().split(";");
			if (split.length > no) {
			try {
					return Integer.parseInt(split[no].trim());
			}
			catch (Exception e) {

			}
			}
		}
		return -1;
	}

	static class InputRenderer extends KnowWEDomRenderer<InputFieldCellContent> {

		@Override
		public void render(KnowWEArticle article,
				Section<InputFieldCellContent> sec, UserContext user,
				StringBuilder string) {
			String versionString = user.getParameter(ShowTableTagHandler.VERSION_KEY);
			int version = 0;
			if (versionString != null) {
				version = Integer.parseInt(versionString);
			}
			String contentString = getStoredContentForInput(sec, version,
					user.getUserName());
			int rows = InputFieldCellContent.getHeight(sec);
			int cols = InputFieldCellContent.getWidth(sec);
			string.append(KnowWEUtils.maskHTML("<textarea rows='" + rows + "' cols='"
					+ cols + "' wrap='soft' type='text' id='"
					+ sec.getID()
					+ "_" + version + "'>" + contentString + "</textarea>"));
		}

		public static String getStoredContentForInput(Section<InputFieldCellContent> sec, int version, String username) {
			List<Section<InputFieldCellContent>> found = new ArrayList<Section<InputFieldCellContent>>();
			Sections.findSuccessorsOfType(sec.getFather().getFather().getFather(),
					InputFieldCellContent.class,
					found);
			int number = found.indexOf(sec);
			Section<DefaultMarkupType> ancestorOfType = Sections.findAncestorOfType(sec,
					DefaultMarkupType.class);
			String tableid = ancestorOfType.get().getAnnotation(ancestorOfType, "id");
			String contentString = getStoredContentString(number, tableid, version,
					username);
			return contentString;
		}

		private static String getStoredContentString(int number, String tableid, int version, String username) {
			String contentString = "-";

			Section<TableEntryType> contentTable = findTableToShow(tableid, username);

			if (contentTable != null) {
				List<Section<VersionEntry>> versionBlocks = TableEntryType.getVersionBlocks(contentTable);
				if (versionBlocks.size() > 0) {
					Section<VersionEntry> versionBlock = versionBlocks.get(version);
					List<Section<ContentEntry>> entries = VersionEntry.getEntries(
							versionBlock);
					for (Section<ContentEntry> section : entries) {
						if (section.get().getNumber(section) == number) {
							contentString = section.get().getContent(section);
							break;
						}
					}
				}
			}
			return contentString;
		}

		public static Section<TableEntryType> findTableToShow(String id, String username) {
			String dataArticleNameForUser = SubmitTableContentAction.getDataArticleNameForUser(username);
			KnowWEArticle knowWEArticle = KnowWEEnvironment.getInstance().getArticleManager(
					KnowWEEnvironment.DEFAULT_WEB).getArticle(dataArticleNameForUser);
			if (knowWEArticle == null) return null;
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
			return null;
		}
	}

}
