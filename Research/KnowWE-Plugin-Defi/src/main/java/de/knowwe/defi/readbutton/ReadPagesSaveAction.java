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
package de.knowwe.defi.readbutton;

import java.io.IOException;
import java.util.HashMap;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.defi.utils.ReplaceSectionUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * Saves the rated page, the rating and a "show-or-hide"-value in the
 * DataMarkup.
 * 
 * @author dupke
 * @created 23.03.2011
 */
public class ReadPagesSaveAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String username = context.getUserName();
		String title = username + "_data";
		String pagename = context.getTitle();
		String web = context.getWeb();
		String value = context.getParameter("value");
		String id = context.getParameter("id");

		// Get the readpages-annotation
		ArticleManager mgr = Environment.getInstance().getArticleManager(web);
		Section<?> section = mgr.getArticle(title).getSection();
		Section<DataMarkup> child = Sections.findSuccessor(section, DataMarkup.class);

		String readpages = DefaultMarkupType.getAnnotation(child, "readpages");

		if (readpages == null) {
			readpages = "";
		}

		// value = 0 => Set HideButtonValue in DataMarkup on 1...
		if (value.equals("0")) {
			String[] pages = readpages.split(";");

			// Get the entry and change the fourth value to 1
			for (String s : pages) {
				if (s.toLowerCase().split("::")[0].equals(pagename.toLowerCase())
						&& s.split("::")[1].equals(id)) {
					readpages = readpages.replace(s, s.split("::")[0] + "::" + s.split("::")[1]
							+ "::" + s.split("::")[2] + "::" + 1);
				}
			}
			HashMap<String, String> nodesMap = new HashMap<String, String>();
			nodesMap.put(child.getID(), "%%data\r\n@readpages: " + readpages + "\r\n%");
			ReplaceSectionUtils.replaceSections(context, nodesMap);
			// Sections.replaceSections(context, nodesMap);
		}
		// ...else try to add a new entry
		else {
			boolean add = true;
			String[] pages = readpages.split(";");

			// Is the entry already written?
			for (String s : pages) {
				if (s.toLowerCase().split("::")[0].equals(pagename.toLowerCase())
						&& s.split("::")[1].equals(id)) {
					add = false;
				}
			}

			if (add) {
				HashMap<String, String> nodesMap = new HashMap<String, String>();
				readpages += pagename + "::" + id + "::" + value + "::" + 0 + ";";
				nodesMap.put(child.getID(), "%%data\r\n@readpages: " + readpages + "\r\n%");
				ReplaceSectionUtils.replaceSections(context, nodesMap);
				// Sections.replaceSections(context, nodesMap);
			}
		}

	}

}
