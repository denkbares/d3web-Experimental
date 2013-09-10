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
package de.knowwe.defi.readon;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.defi.logger.DefiPageEventLogger;
import de.knowwe.defi.logger.DefiPageLogLine;
import de.knowwe.defi.menu.MenuUtilities;
import de.knowwe.kdom.dashtree.DashTreeElement;

/**
 * @author dupke
 */
public class ReadOnTagHandler extends AbstractTagHandler {

	private static final String[] ADDITIONAL_PAGES = {
			"Gebrauchsanweisung", "Hilfe" };

	/**
	 * @param name
	 */
	public ReadOnTagHandler() {
		super("readon");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {
		StringBuilder readon = new StringBuilder();
		String title = "";

		// Füge Lektionenseiten hinzu
		List<Section<DashTreeElement>> units = MenuUtilities.getAllUnits();
		List<String> unitTitles = new LinkedList<String>();
		for (Section<DashTreeElement> unit : units) {
			unitTitles.add(MenuUtilities.getUnitPagename(unit));
		}

		// Füge zusätzliche Seiten hinzu
		for (String s : ADDITIONAL_PAGES) {
			unitTitles.add(s);
		}

		// Durchsuche den Log nach letzter besuchter Seite
		for (DefiPageLogLine logLine : DefiPageEventLogger.getLogLines()) {
			String userName = logLine.getUser();
			String page = logLine.getPage();
			if (userContext.getUserName().equals(userName) && unitTitles.contains(page)) title = page;
		}

		// Gebe letzte besuchte Seite aus oder - für keine Seite
		if (title.equals("")) readon.append("<p>-</p>");
		else readon.append("<p><a href='Wiki.jsp?page=" + title + "'>" + title
				+ "</a></p>");

		result.appendHtml(readon.toString());
	}
}
