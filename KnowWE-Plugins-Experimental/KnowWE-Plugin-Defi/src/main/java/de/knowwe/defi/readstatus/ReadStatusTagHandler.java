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
package de.knowwe.defi.readstatus;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.defi.menu.MenuUtilities;
import de.knowwe.defi.readbutton.ReadbuttonUtilities;
import de.knowwe.defi.time.TimeTableUtilities;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.dashtree.DashTreeUtils;

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
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {
		StringBuilder readstatus = new StringBuilder();
		List<Section<DashTreeElement>> rootUnits = MenuUtilities.getRootUnits();
		List<Section<DashTreeElement>> allUnits = MenuUtilities.getAllUnits();
		List<Date> dates = TimeTableUtilities.getPersonalTimeTable(userContext.getUserName());
		Date current = new Date();
		boolean unitsOpen = false;

		// Prüfe ob jeder root unit ein Zeitpunkt zugewiesen wurde
		if (rootUnits == null || dates == null || rootUnits.size() > dates.size()) {
			result.appendHtml("<p>Fehler: Zu wenig Zeiteinheiten im <a href='Wiki.jsp?page=Zeitplan'>Zeitplan</a> vorhanden</p>");
			return;
		}

		for (int i = 0; i < rootUnits.size(); i++) {
			Section<DashTreeElement> unit = rootUnits.get(i);
			int unread = 0;

			// Prüfe ob alle Buttons einer Einheit beantwortet wurden
			List<String> rBtnsForUnit = searchForReadbuttons(MenuUtilities.getSubUnits(unit,
					allUnits));
			for (String readBtn : rBtnsForUnit) {
				if (!getReadbuttonStatus(readBtn, userContext.getUserName())) unread++;
			}

			if (unread == 0) continue;

			// Daten
			Date unitDate = dates.get(i);
			Date warningDate = getDateOfWarning(unitDate);

			/* ------------------------------------------------------------ */
			/* AUSGABE */
			/* ------------------------------------------------------------ */
			if (current.compareTo(unitDate) >= 0) {
				if (current.compareTo(warningDate) == -1) readstatus.append("<li class='readstatus'>");
				else readstatus.append("<li class='readstatusWARN'>");
				readstatus.append("In <a href='Wiki.jsp?page=" + getPageName(unit) + "'>"
						+ getLabel(unit) + "</a> haben Sie " + unread
						+ " Bewertungen noch nicht abgegeben.");
				readstatus.append("</li>");

				unitsOpen = true;
			}
			/* ------------------------------------------------------------ */
		}

		if (!unitsOpen) readstatus.append("<li style='list-style: none'>Keine offenen Bewertungen</li>");

		result.appendHtml(readstatus.toString());
	}

	/**
	 * Calculate date of warning.
	 * 
	 * @param date
	 * @created 17.06.2013
	 * @return
	 */
	private Date getDateOfWarning(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, DAYS_UNTIL_WARNING);
		return calendar.getTime();
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
			Article unit = KnowWEUtils.getArticleManager(
					Environment.DEFAULT_WEB).getArticle(getPageName(s));

			if (unit != null) {
				for (Section<AbstractType> sec : Sections.successors(
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
		return ReadbuttonUtilities.isPageRated(readbutton, userName);
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