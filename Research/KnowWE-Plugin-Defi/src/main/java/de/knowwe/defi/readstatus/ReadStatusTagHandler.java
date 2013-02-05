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
package de.knowwe.defi.readstatus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.defi.menu.MenuUtilities;
import de.knowwe.defi.readbutton.ReadbuttonType;
import de.knowwe.defi.time.TimeTableUtilities;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.dashtree.DashTreeUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author dupke
 * @created 29.05.2011
 */
public class ReadStatusTagHandler extends AbstractTagHandler {

	private static final int DAYS_UNTIL_WARNING = 3;

	/**
	 * @param name
	 */
	public ReadStatusTagHandler() {
		super("readstatus");
	}

	@Override
	public String render(Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuilder readstatus = new StringBuilder();
		List<Section<DashTreeElement>> units = MenuUtilities.getUnits();
		List<Date> dates = TimeTableUtilities.getTimeTable();
		List<String> readbuttons = new ArrayList<String>();
		// Zählt Rooteinheiten
		int rootCounter = 0;
		// Einheit komplett gelesen?
		boolean read = true;
		// -1: vergangen, 0: aktiv; 1: anstehend
		int timeStatus = 0;
		Date current = new Date();
		Date unitDate = new Date();
		Calendar calendar = new GregorianCalendar();

		for (Section<DashTreeElement> rootUnit : units) {

			// Ist die Einheit eine Rooteinheit?
			if (DashTreeUtils.getDashLevel(rootUnit) == 0) {
				// Datum der Einheit
				if (rootCounter >= dates.size())
					return Strings.maskHTML("<p>Fehler: Zu wenig Zeiteinheiten im <a href='Wiki.jsp?page=Zeitplan'>Zeitplan</a> vorhanden</p>");

				unitDate = dates.get(rootCounter);
				// Hole alle Readbuttons aller Untereinheiten und der Einheit
				readbuttons = searchForReadbuttons(MenuUtilities.getSubUnits(rootUnit, units));

				for (int i = 0; i < readbuttons.size(); i++) {
					// Prüfe ob alle Buttons der Lektion geklickt wurden
					if (!getReadbuttonStatus(readbuttons.get(i),
							userContext.getUserName()))
						read = false;
				}

				calendar.setTime(dates.get(rootCounter));
				calendar.add(Calendar.DAY_OF_MONTH, DAYS_UNTIL_WARNING);
				Date warning = calendar.getTime();

				// Einheit anstehend
				if (unitDate.after(current)) {
					timeStatus = 1;
				}
				// Einheit aktiv
				else if (rootCounter + 1 < dates.size()
						&& dates.get(rootCounter + 1).after(current)) {
					timeStatus = 0;
				}
				// Letzte Einheit
				else if (rootCounter + 1 == dates.size()) {
					if (current.before(warning)) timeStatus = 0;
					else timeStatus = -1;
				}
				// Einheit vergangen
				else {
					timeStatus = -1;
				}

				/* ------------------------------------------------------------ */
				/* AUSGABE */
				/* ------------------------------------------------------------ */
				if (!read) {
					readstatus.append("<li class='readstatus'>");
					if (timeStatus == -1 || (timeStatus == 0 && !current.before(warning))) readstatus.append("In ");
					readstatus.append("<a href='Wiki.jsp?page="
							+ getPageName(rootUnit) + "'>" + getLabel(rootUnit)
							+ "</a> ");

					if (timeStatus == -1 || (timeStatus == 0 && !current.before(warning)))
						readstatus.append(" haben Sie noch nicht alle Seiten bearbeitet!");

					readstatus.append("</li>");
				}
				/* ------------------------------------------------------------ */

				read = true;
				rootCounter++;
			}

		}

		return Strings.maskHTML(readstatus.toString());
	}

	/**
	 * 
	 * @created 29.05.2011
	 * @param units
	 * @return
	 */
	private List<String> searchForReadbuttons(List<Section<DashTreeElement>> units) {
		List<String> readbuttons = new LinkedList<String>();

		for (Section<DashTreeElement> s : units) {
			Article unit = Environment.getInstance().getArticleManager(
					Environment.DEFAULT_WEB).getArticle(getPageName(s));

			if (unit != null) {
				for (Section<AbstractType> sec : Sections.findSuccessorsOfType(
						unit.getRootSection(),
						AbstractType.class)) {
					if (sec.getText().matches("\\[\\{KnowWEPlugin readbutton[^}]*}]")) readbuttons.add(getReadButtonID(sec.getText()));
				}
			}

		}

		return readbuttons;
	}

	/**
	 * Prüft ob ein Readbutton geklickt wurde.
	 * 
	 * @return true = geklickt, false = noch nicht geklickt
	 */
	private boolean getReadbuttonStatus(String readbutton, String userName) {
		String dataPagename = userName + "_data";
		ArticleManager mgr = Environment.getInstance().getArticleManager(Environment.DEFAULT_WEB);
		if (Environment.getInstance().getWikiConnector().doesArticleExist(dataPagename)) {
			Section<?> sec = mgr.getArticle(dataPagename).getRootSection();
			List<Section<ReadbuttonType>> rbSecs = Sections.findSuccessorsOfType(sec,
					ReadbuttonType.class);
			for (Section<ReadbuttonType> rbSec : rbSecs) {
				if (readbutton.equals(DefaultMarkupType.getAnnotation(rbSec, "id"))) return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * @created 30.05.2011
	 * @param rb
	 * @return
	 */
	private String getReadButtonID(String rb) {
		String id = "";

		id = rb.split("id=")[1];
		if (id.split(" ,").length > 1) return id.split(" ,")[0];
		else if (id.split(" }]").length > 1) return id.split(" }]")[0];
		else return id.split("}]")[0];
	}

	/**
	 * 
	 * @created 30.05.2011
	 * @param sec
	 * @return
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

	/**
	 * 
	 * @created 30.05.2011
	 * @param sec
	 * @return
	 */
	private static String getLabel(Section<DashTreeElement> sec) {
		String pagename = sec.getText().trim();
		if (DashTreeUtils.getDashLevel(sec) == 1) {
			pagename = pagename.substring(2);
		}
		else if (DashTreeUtils.getDashLevel(sec) == 2) {
			pagename = pagename.substring(3);
		}
		String label = pagename;
		if (sec.getText().contains("|")) {
			String[] split = sec.getText().split("\\|");
			label = split[0].trim();
		}

		return label;
	}
}