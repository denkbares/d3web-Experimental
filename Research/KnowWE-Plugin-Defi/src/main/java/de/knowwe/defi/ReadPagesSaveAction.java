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
package de.knowwe.defi;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author dupke
 * @created 23.03.2011
 */
public class ReadPagesSaveAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String username = context.getUserName();
		String title = username + "_data";
		String pagename = context.getTopic();
		String web = context.getWeb();
		String value = context.getParameter("value");

		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);
		Section<?> section = mgr.getArticle(title).getSection();
		Section<DataMarkup> child = Sections.findSuccessor(section, DataMarkup.class);
		String readpages = DefaultMarkupType.getAnnotation(child, "readpages");
		boolean add = true;

		if (readpages == null) {
			readpages = "";
		}

		String[] pages = readpages.split(";");

		for (String s : pages) {
			if (s.toLowerCase().equals(pagename.toLowerCase())) {
				add = false;
			}
		}

		if (add) {
			HashMap<String, String> nodesMap = new HashMap<String, String>();
			readpages += pagename + "," + value + ";";
			nodesMap.put(child.getID(), "%%data\r\n@readpages: " + readpages + "\r\n%\r\n");
			mgr.replaceKDOMNodesSaveAndBuild(context, title, nodesMap);
		}

		HttpServletResponse response = context.getResponse();
		response.sendRedirect("Wiki.jsp?page=" + username + "_data");
	}

}
