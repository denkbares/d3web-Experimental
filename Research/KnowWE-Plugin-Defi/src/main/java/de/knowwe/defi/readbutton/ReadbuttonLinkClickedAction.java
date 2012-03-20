/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.readbutton;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.defi.utils.ReplaceSectionUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * Updates the 'discussed'-annotation for a readbutton.
 * 
 * @author dupke
 */
public class ReadbuttonLinkClickedAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String username = context.getUserName();
		String id = context.getParameter("id");
		String discussed = context.getParameter("discussed");
		String dataPagename = username + "_data";

		// search on "_data"-page for the button
		dataPagename = context.getUserName() + "_data";
		ArticleManager mgr = Environment.getInstance().getArticleManager(context.getWeb());
		if (Environment.getInstance().getWikiConnector().doesPageExist(dataPagename)) {
			Map<String, String> nodesMap = new HashMap<String, String>();
			Section<ReadbuttonType> sec = findContentSectionForReadbuttonID(id,
					mgr.getArticle(dataPagename));
			if (sec != null) {
				nodesMap.put(sec.getID(), createNewMarkupString(sec, discussed));
				// submit change
				ReplaceSectionUtils.replaceSections(context, nodesMap);
			}
		}
	}

	private static Section<ReadbuttonType> findContentSectionForReadbuttonID(String id, Article article) {
		Sections.findSuccessorsOfType(article.getSection(), ReadbuttonType.class);
		for (Section<ReadbuttonType> section : Sections.findSuccessorsOfType(article.getSection(),
				ReadbuttonType.class)) {
			if (id.equals(DefaultMarkupType.getAnnotation(section, "id"))) return section;
		}

		return null;
	}

	private String createNewMarkupString(Section<ReadbuttonType> sec, String discussed) {
		String id = DefaultMarkupType.getAnnotation(sec, "id");
		String realValue = DefaultMarkupType.getAnnotation(sec, "realvalue");
		String value = DefaultMarkupType.getAnnotation(sec, "value");
		String label = DefaultMarkupType.getAnnotation(sec, "label");
		String discussed_old = DefaultMarkupType.getAnnotation(sec, "discussed");
		String closed = "Nein";
		String date = DefaultMarkupType.getAnnotation(sec, "date");

		if (discussed.equals("Nein")) closed = "Ja";
		if (discussed_old.equals("Ja")) discussed = "Ja";

		StringBuffer newContent = new StringBuffer();
		newContent.append("%%readbutton\n");
		newContent.append("@id:" + id + "\n");
		newContent.append("@realvalue:" + realValue + "\n");
		newContent.append("@value:" + value + "\n");
		newContent.append("@label:" + label + "\n");
		newContent.append("@discussed:" + discussed + "\n");
		newContent.append("@closed:" + closed + "\n");
		newContent.append("@date:" + date + "\n");
		newContent.append("%\n");

		return newContent.toString();
	}

}
