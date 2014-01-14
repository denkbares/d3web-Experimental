/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
import java.util.LinkedList;
import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.defi.time.TimeTableUtilities;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.dashtree.DashTreeUtils;

/**
 * 
 * @author dupke
 * @created 05.02.2013
 */
public class MenuUtilities {

	/**
	 * Get all units.
	 */
	public static List<Section<DashTreeElement>> getAllUnits() {
		List<Section<DashTreeElement>> units = new LinkedList<Section<DashTreeElement>>();
		Article leftMenu = KnowWEUtils.getArticleManager(
				Environment.DEFAULT_WEB).getArticle("LeftMenu");

		if (leftMenu != null) {
			Section<DynamicMenuMarkup> menu = Sections.findSuccessor(
					leftMenu.getRootSection(),
					DynamicMenuMarkup.class);
			if (menu != null) Sections.findSuccessorsOfType(menu, DashTreeElement.class, units);
		}

		return units;
	}

	/**
	 * Get all root units.
	 */
	public static List<Section<DashTreeElement>> getRootUnits() {
		List<Section<DashTreeElement>> units = new LinkedList<Section<DashTreeElement>>();
		List<Section<DashTreeElement>> rootUnits = new LinkedList<Section<DashTreeElement>>();
		Article leftMenu = KnowWEUtils.getArticleManager(
				Environment.DEFAULT_WEB).getArticle("LeftMenu");

		if (leftMenu != null) {
			Section<DynamicMenuMarkup> menu = Sections.findSuccessor(
					leftMenu.getRootSection(),
					DynamicMenuMarkup.class);
			Sections.findSuccessorsOfType(menu, DashTreeElement.class, units);
		}

		for (Section<DashTreeElement> unit : units) {
			if (DashTreeUtils.getDashLevel(unit) == 0) rootUnits.add(unit);
		}

		return rootUnits;
	}

	/**
	 * Get all subunits of a root unit.
	 */
	public static List<Section<DashTreeElement>> getSubUnits(Section<DashTreeElement> rootUnit, List<Section<DashTreeElement>> units) {
		List<Section<DashTreeElement>> subUnits = new LinkedList<Section<DashTreeElement>>();
		Sections.findSuccessorsOfType(rootUnit, DashTreeElement.class, subUnits);
		boolean add = false;

		for (Section<DashTreeElement> sec : units) {
			if (sec.equals(rootUnit)) {
				add = true;
			}
			else if (DashTreeUtils.getDashLevel(sec) == 0) {
				add = false;
			}

			if (add && !subUnits.contains(sec)) subUnits.add(sec);
		}

		return subUnits;
	}

	public static List<Section<DashTreeElement>> getSubUnits(Section<DashTreeElement> rootUnit) {
		return getSubUnits(rootUnit, getAllUnits());
	}

	/**
	 * Get the unit's pagename.
	 */
	public static String getUnitPagename(Section<DashTreeElement> sec) {
		String title = sec.getText().trim();
		if (DashTreeUtils.getDashLevel(sec) == 1) {
			title = title.substring(2);
		}
		else if (DashTreeUtils.getDashLevel(sec) == 2) {
			title = title.substring(3);
		}
		if (sec.getText().contains("|")) {
			String[] split = sec.getText().split("\\|");
			title = split[1].trim();
		}

		return title;
	}

	/**
	 * Is the unit open?
	 */
	public static boolean isUnitOpen(Section<DashTreeElement> sec, String user) {
		Section<? extends Type> dashtree = sec.getParent().getParent().getParent();
		List<Section<DashTreeElement>> found = new ArrayList<Section<DashTreeElement>>();
		Sections.findSuccessorsOfType(dashtree, DashTreeElement.class, 3, found);

		int unitNumber = -1;
		for (int i = 0; i < found.size(); i++) {
			if (found.get(i).getID().equals(
					sec.getID())) {
				unitNumber = i;
				break;
			}
		}

		List<Date> dates = TimeTableUtilities.getPersonalTimeTable(user);
		Date current = new Date();
		Date unitDate = null;
		if (unitNumber < dates.size()) {
			unitDate = dates.get(unitNumber);
			if (current.after(unitDate)) {
				return true;
			}
		}

		return false;
	}
}
