/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
import java.util.regex.Pattern;

import de.knowwe.core.Environment;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class InputFieldCellContent extends AbstractType {

	public InputFieldCellContent() {
		this.setSectionFinder(new RegexSectionFinder("INPUT(\\(.*?\\))?", Pattern.DOTALL));
		this.setRenderer(new InputRenderer());
	}

	public static int getWidth(Section<?> sec) {
		String originalText = sec.getText();
		int width;
		width = readParamterNo(originalText, 0);
		if (width == -1) width = 20; // set default
		return width;
	}

	public static int getHeight(Section<?> sec) {
		String originalText = sec.getText();
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

	static class InputRenderer implements Renderer {

		@Override
		public void render(Section<?> sec,
				UserContext user, RenderResult string) {
			String versionString = user.getParameter(ShowTableTagHandler.VERSION_KEY);
			int version = 0;
			if (versionString != null) {
				version = Integer.parseInt(versionString);
			}
			String contentString = getStoredContentForInput(sec, version,
					user.getUserName());
			int rows = InputFieldCellContent.getHeight(sec);
			int cols = InputFieldCellContent.getWidth(sec);
			string.appendHtml("<textarea rows='" + rows + "' cols='"
					+ cols + "' wrap='soft' type='text' id='"
					+ sec.getID()
					+ "_" + version + "'>");
			string.append(contentString);
			string.appendHtml("</textarea>");
		}

		public static String getStoredContentForInput(Section<?> sec, int version, String username) {
			List<Section<InputFieldCellContent>> found = new ArrayList<Section<InputFieldCellContent>>();
			Sections.findSuccessorsOfType(sec.getParent().getParent().getParent().getParent(),
					InputFieldCellContent.class,
					found);
			int number = found.indexOf(sec);
			Section<DefaultMarkupType> ancestorOfType = Sections.findAncestorOfType(sec,
					DefaultMarkupType.class);
			String tableid = DefaultMarkupType.getAnnotation(ancestorOfType, "id");
			String contentString = getStoredContentString(number, tableid, version,
					username);
			return contentString;
		}

		private static String getStoredContentString(int number, String tableid, int version, String username) {
			String contentString = "";

			Section<TableEntryType> contentTable = findTableToShow(tableid, username);

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
			Article article = Compilers.getDefaultArticleManager(
					Environment.DEFAULT_WEB).getArticle(dataArticleNameForUser);
			if (article == null) return null;
			List<Section<TableEntryType>> tables = new ArrayList<Section<TableEntryType>>();
			Sections.findSuccessorsOfType(article.getRootSection(),
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

}
