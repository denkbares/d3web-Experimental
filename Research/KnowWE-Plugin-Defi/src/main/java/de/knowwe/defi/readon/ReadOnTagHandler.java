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

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.defi.logger.PageLoggerHandler;
import de.knowwe.defi.menu.DynamicMenuMarkup;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.dashtree.DashTreeUtils;

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
	public String render(Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuilder readon = new StringBuilder();
		String path = PageLoggerHandler.getPath();
		String line;
		String page = "";
		// Hole Lektionen aus dem Left Menu
		List<String> units = getALlUnits();
		// Füge zusätzliche Seiten an
		for (String s : ADDITIONAL_PAGES) {
			units.add(s);
		}

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(new FileInputStream(
					new File(path))), "UTF-8"));
			while ((line = br.readLine()) != null) {
				if (userContext.getUserName().equals(line.split(";")[1])
						&& units.contains(line.split(";")[2]))
					page = line.split(";")[2];
			}
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
		}

		readon.append("<p><a href='Wiki.jsp?page=" + page + "'>" + page
				+ "</a></p>");

		return KnowWEUtils.maskHTML(readon.toString());
	}

	/**
	 * 
	 */
	private List<String> getALlUnits() {
		List<Section<DashTreeElement>> units = new LinkedList<Section<DashTreeElement>>();
		List<String> pageNames = new LinkedList<String>();
		Article leftMenu = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB).getArticle("LeftMenu");

		if (leftMenu != null) {
			Section<DynamicMenuMarkup> menu = Sections.findSuccessor(
					leftMenu.getSection(),
					DynamicMenuMarkup.class);
			Sections.findSuccessorsOfType(menu, DashTreeElement.class, units);
		}

		for (Section<DashTreeElement> unit : units) {
			pageNames.add(getPageName(unit));
		}

		return pageNames;
	}

	/**
	 * 
	 */
	private static String getPageName(Section<DashTreeElement> sec) {
		String pagename = sec.getText().trim();
		if (DashTreeUtils.getDashLevel(sec) == 1) {
			pagename = pagename.substring(2);
		}
		else if (DashTreeUtils.getDashLevel(sec) == 2) {
			pagename = pagename.substring(3);
		}
		if (sec.getText().contains("|")) {
			String[] split = sec.getText().split("\\|");
			pagename = split[1].trim();
		}

		return pagename;
	}

}
