/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.defi.menu;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.knowwe.core.dashtree.DashTreeElement;
import de.knowwe.core.dashtree.DashTreeUtils;

public class MenuItemRenderer extends KnowWEDomRenderer<DynamicMenuItem> {

	@Override
	public void render(KnowWEArticle article, Section<DynamicMenuItem> sec, UserContext user, StringBuilder string) {
		// TODO: add rendering logics here
		int dashLevel = DashTreeUtils.getDashLevel(Sections.findAncestorOfType(sec,
				DashTreeElement.class));
		boolean isRoot = false;
		if(dashLevel == 0) {
			isRoot = true;
		}

		String currentPage = user.getParameter("page");

		String color = "#CCCCCC";
		String weight = "normal";

		String pagename = sec.getOriginalText();
		String label = sec.getOriginalText();
		if (sec.getOriginalText().contains("|")) {
			String[] split = sec.getOriginalText().split("\\|");
			label = split[0].trim();
			pagename = split[1].trim();
		}
		string.append("<a href='"
				+ KnowWEEnvironment.getInstance().getWikiConnector().getBaseUrl()
				+ "Wiki.jsp?page=" + pagename + "'>");

		if (isRoot) {
			color = "#FF6633";
			weight = "bold";
		}

		if (pagename.equals(currentPage)) {
			color = "white";
		}

		string.append("<div style='font-weight:"
				+ weight
				+ ";padding-top: 0.25em;padding-right: 0.5em;padding-left: 0.5em; padding-bottom: 0.25em;background-color:"
				+ color
				+ "; border-style:solid; border-color: #000000;border-left-width:1px;border-right-width:1px;border-bottom-width:0px;border-top-width:1px;'>");
		string.append(label);
		string.append("</div>");
		string.append("</a>");
		// string.append("\\\\");

	}

}
