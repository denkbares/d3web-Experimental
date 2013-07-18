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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.defi.logger.PageLoggerHandler;
import de.knowwe.defi.menu.MenuUtilities;
import de.knowwe.kdom.dashtree.DashTreeElement;

/**
 * 
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
		String path = PageLoggerHandler.getPath();
		String line;
		String page = "";
		// Hole Lektionen aus dem Left Menu
		List<Section<DashTreeElement>> units = MenuUtilities.getAllUnits();
		List<String> unitTitles = new LinkedList<String>();
		for (Section<DashTreeElement> unit : units) {
			unitTitles.add(MenuUtilities.getUnitPagename(unit));
		}

		// Füge zusätzliche Seiten an
		for (String s : ADDITIONAL_PAGES) {
			unitTitles.add(s);
		}

		// TODO Neuen pagelogger benutzen
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(new FileInputStream(new File(path))), "UTF-8"));
			try {
				while ((line = br.readLine()) != null) {
					String name = line.split(PageLoggerHandler.getSeparator())[1];
					String title = line.split(PageLoggerHandler.getSeparator())[2];
					if (userContext.getUserName().equals(name) && unitTitles.contains(title)) page = title;
				}
			}
			finally {
				br.close();
			}
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
		}

		readon.append("<p><a href='Wiki.jsp?page=" + page + "'>" + page
				+ "</a></p>");

		result.appendHtml(readon.toString());
	}
}
