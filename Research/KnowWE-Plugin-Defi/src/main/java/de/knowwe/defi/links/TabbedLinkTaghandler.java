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
package de.knowwe.defi.links;

import java.util.Map;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * The TabbedLinkTaghandler simplifies the creation of links to tabbed sections
 * in a wiki article. Please use the following syntax:
 * 
 * <blockquote> [{KnowWEPlugin tabbedlinktag , tab=NameOfTab , page=PageName ,
 * title=LinkName}] </blockquote>
 *
 * The title attribute is optional. If not given a combination of page and tab
 * value is used as the name of the link. (e.g.: PagenName &raquo; NameOfTab)
 *
 * @author smark
 * @created 28.02.2011
 */
public class TabbedLinkTaghandler extends AbstractTagHandler {

	public TabbedLinkTaghandler() {
		super("tabbedlinktag");
	}


	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {

		StringBuilder tabbedLink = new StringBuilder();

		String tab = parameters.get("tab");
		String page = parameters.get("page");
		String title = parameters.get("title");

		if (tab != null && page != null && tab != "" && page != "") {

			if (title == null || title == "") {
				title = page + " &raquo; " + tab;
			}

			tabbedLink.append("<a href=\"Wiki.jsp?page=");
			tabbedLink.append(KnowWEUtils.urlencode(page.trim()));
			tabbedLink.append("&amp;tab=");
			tabbedLink.append(KnowWEUtils.urlencode(tab.trim()));
			tabbedLink.append("\" title=\"Title:");
			tabbedLink.append(title);
			tabbedLink.append("\" rel=\"nofollow\">");
			tabbedLink.append(title);
			tabbedLink.append("</a>");
		}
		return KnowWEUtils.maskHTML(tabbedLink.toString());
	}
}
