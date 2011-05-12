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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.knowwe.core.dashtree.DashTreeElement;
import de.knowwe.core.dashtree.DashTreeElementContent;
import de.knowwe.core.dashtree.DashTreeUtils;
import de.knowwe.defi.time.TimeTableMarkup;

public class MenuItemRenderer extends KnowWEDomRenderer<DynamicMenuItem> {

	private static String getPageName(Section<? extends DashTreeElementContent> sec) {
		String pagename = sec.getOriginalText().trim();
		if (sec.getOriginalText().contains("|")) {
			String[] split = sec.getOriginalText().split("\\|");
			pagename = split[1].trim();
		}

		return pagename;
	}

	private static String getLabel(Section<DynamicMenuItem> sec) {
		String pagename = sec.getOriginalText().trim();
		String label = pagename;
		if (sec.getOriginalText().contains("|")) {
			String[] split = sec.getOriginalText().split("\\|");
			label = split[0].trim();
		}

		return label;
	}

	@Override
	public void render(KnowWEArticle article, Section<DynamicMenuItem> sec, UserContext user, StringBuilder string) {
		// TODO: add rendering logics here
		int dashLevel = DashTreeUtils.getDashLevel(Sections.findAncestorOfType(sec,
				DashTreeElement.class));
		boolean isRoot = false;
		if (dashLevel == 0) {
			isRoot = true;
		}


		String currentPage = user.getParameter("page");

		String className = "menulink";

		String pagename = getPageName(sec);
		String label = getLabel(sec);


		boolean hidden = true;

		if (isRoot) {
			if (isFree(sec)) {
				className = "menulinkroot";
			}
			else {
				className = "menulinkrootclosed";
			}
			hidden = false;
		}
		else {
			// checken ob Einheit gewählt ist
			Section<? extends DashTreeElement> root = DashTreeUtils.getFatherDashTreeElement(sec);
			Section<DashTreeElementContent> rootContent = Sections.findChildOfType(root,
					DashTreeElementContent.class);
			if (getPageName(rootContent).equals(currentPage)) {
				hidden = false;
			}
			List<Section<DynamicMenuItem>> found = new ArrayList<Section<DynamicMenuItem>>();
			Sections.findSuccessorsOfType(root.getFather(), DynamicMenuItem.class, found);
			for (Section<DynamicMenuItem> section : found) {
				if (getPageName(section).equals(currentPage)) {
					hidden = false;
				}
			}

		}

		if (hidden) return;

		if (pagename.equals(currentPage)) {
			className = "menulinkcurrent";
		}

		// Link nur wenn freigeschaltet
		if (!(isRoot && (!isFree(sec)))) {
			string.append("<a  href='"
					+ KnowWEEnvironment.getInstance().getWikiConnector().getBaseUrl()
					+ "Wiki.jsp?page=" + pagename + "'>");
		}

		string.append("<div class="
				+ className
				+ " style='padding-top: 0.25em;padding-right: 0.5em;padding-left: 0.5em; padding-bottom: 0.25em;"
				+ "; border-style:solid; border-color: #000000;border-left-width:1px;border-right-width:1px;border-bottom-width:0px;border-top-width:1px;'>");
		string.append(label);
		string.append("</div>");

		// link nur wenn freigeschaltet
		if (!(isRoot && (!isFree(sec)))) {
			string.append("</a>");
		}
		// string.append("\\\\");

	}

	private boolean isFree(Section<DynamicMenuItem> sec) {
		Section<? extends Type> dashtree = sec.getFather().getFather().getFather();
		List<Section<DynamicMenuItem>> found = new ArrayList<Section<DynamicMenuItem>>();
		Sections.findSuccessorsOfType(dashtree, DynamicMenuItem.class, 3, found);

		int unitNumber = -1;
		for (int i = 0; i < found.size(); i++) {
			if (found.get(i).getID().equals(
					sec.getID())) {
				unitNumber = i;
				break;
			}
		}

		KnowWEArticle zeitplanArticle = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB).getArticle(
				"Zeitplan");
		if (zeitplanArticle != null) {
			Section<TimeTableMarkup> timetable = Sections.findSuccessor(
					zeitplanArticle.getSection(), TimeTableMarkup.class);
			if (timetable != null) {
				List<Date> dates = TimeTableMarkup.getDates(timetable);
				Date current = new Date();
				Date unitDate = null;
				if (dates.size() > unitNumber) {
					unitDate = dates.get(unitNumber);
					if (current.after(unitDate)) {
						return true;
					}
				}
			}

		}

		return false;
	}

}
