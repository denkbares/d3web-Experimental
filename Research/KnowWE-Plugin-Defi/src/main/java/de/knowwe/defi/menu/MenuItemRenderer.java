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

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.defi.time.TimeTableUtilities;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.dashtree.DashTreeElementContent;
import de.knowwe.kdom.dashtree.DashTreeUtils;

public class MenuItemRenderer implements Renderer {

	private static final String CSS_CLASS_MENULINK = "menulink";
	private static final String CSS_CLASS_MENULINKROOT = "menulinkroot";
	private static final String CSS_CLASS_MENULINKROOTCLOSED = "menulinkrootclosed";
	private static final String CSS_CLASS_MENULINKROOTCURRENT = "menulinkrootcurrent";
	private static final String CSS_CLASS_MENULINKCURRENT = "menulinkcurrent";

	private static String getPageName(Section<?> sec) {
		String pagename = sec.getText().trim();
		if (sec.getText().contains("|")) {
			String[] split = sec.getText().split("\\|");
			pagename = split[1].trim();
		}

		return pagename;
	}

	private static String getLabel(Section<?> sec) {
		String pagename = sec.getText().trim();
		String label = pagename;
		if (sec.getText().contains("|")) {
			String[] split = sec.getText().split("\\|");
			label = split[0].trim();
		}

		return label;
	}

	@Override
	public void render(Section<?> sec, UserContext user, RenderResult string) {

		int dashLevel = DashTreeUtils.getDashLevel(Sections.findAncestorOfType(sec,
				DashTreeElement.class));
		boolean isRoot = false;
		if (dashLevel == 0) {
			isRoot = true;
		}

		// check for sub-pages
		if (dashLevel == 2) {
			return; // sub-pages are not displayed in Menu at all
			// (if a sub-page is opened in the main view, then super-page is
			// highlighted)
		}

		String currentPage = user.getParameter("page");

		String className = CSS_CLASS_MENULINK;

		String pagename = getPageName(sec);
		String label = getLabel(sec);

		boolean hidden = true;

		if (isRoot) {
			if (isFree(sec, user.getUserName())) {
				className = CSS_CLASS_MENULINKROOT;
			}
			else {
				className = CSS_CLASS_MENULINKROOTCLOSED;
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
				if (pagename.equals(currentPage) || isSubpageOf(currentPage, section)) {
					hidden = false;
				}
			}

		}

		// do not show hidden elements at all
		if (hidden) return;

		// if this page or one of its subpages is opened, this menu item is
		// highlighted
		if (pagename.equals(currentPage) || (!isRoot && isSubpageOf(currentPage, sec))) {

			className = CSS_CLASS_MENULINKCURRENT;
			if (isRoot) className = CSS_CLASS_MENULINKROOTCURRENT;
		}

		string.append("<tr><td class=" + className + ">");
		// Link nur wenn freigeschaltet
		if (!className.equals(CSS_CLASS_MENULINKROOTCLOSED)) {
			string.append("<a  href='"
					+ Environment.getInstance().getWikiConnector().getBaseUrl()
					+ "Wiki.jsp?page=" + pagename + "'>");
		}

		string.append("<div>");
		string.append(label);
		string.append("</div>");

		// link nur wenn freigeschaltet
		if (!className.equals(CSS_CLASS_MENULINKROOTCLOSED)) {
			string.append("</a>");
		}
		string.append("</td></tr>");
	}

	private boolean isSubpageOf(String currentPage, Section<?> section) {
		Section<DashTreeElement> dtElement = Sections.findAncestorOfType(section,
				DashTreeElement.class);
		List<Section<DashTreeElementContent>> found = new ArrayList<Section<DashTreeElementContent>>();
		Sections.findSuccessorsOfType(dtElement.getFather(),
				DashTreeElementContent.class, found);

		for (Section<DashTreeElementContent> section2 : found) {
			if (getPageName(section2).equals(currentPage)) {
				return true;
			}
		}

		return false;
	}

	private boolean isFree(Section<?> sec, String user) {
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

		List<Date> dates = TimeTableUtilities.getTimeTable(user);
		Date current = new Date();
		Date unitDate = null;
		if (dates.size() > unitNumber) {
			unitDate = dates.get(unitNumber);
			if (current.after(unitDate)) {
				return true;
			}
		}

		return false;
	}

}
