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

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.defi.menu.DynamicMenuMarkup;
import de.knowwe.defi.readbutton.DataMarkup;
import de.knowwe.defi.time.TimeTableMarkup;
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
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuilder readstatus = new StringBuilder();
		// Hole alle Einheiten
		List<Section<DashTreeElement>> units = getALlUnits();
		// Hole Zeitplan
		List<Date> dates = getTimeTable();
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
					return KnowWEUtils.maskHTML("<p>Fehler: Zu wenig Zeiteinheiten im <a href='Wiki.jsp?page=Zeitplan'>Zeitplan</a> vorhanden</p>");

				unitDate = dates.get(rootCounter);
				// Hole alle Readbuttons aller Untereinheiten und der Einheit
				readbuttons = searchForReadbuttons(getSubUnits(rootUnit, units));

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
					readstatus.append("<li class='readstatus'>In ");
					readstatus.append("<a href='Wiki.jsp?page="
							+ getPageName(rootUnit) + "'>" + getLabel(rootUnit)
							+ "</a> ");

					if (timeStatus == -1) {
						readstatus.append(" haben Sie noch nicht alle Seiten bearbeitet!");
					}

					if (timeStatus == 0 && !current.before(warning)) {
						readstatus.append(" haben Sie noch nicht alle Seiten bearbeitet!");
					}

					readstatus.append("</li>");
				}
				/* ------------------------------------------------------------ */

				read = true;
				rootCounter++;
			}

		}

		return KnowWEUtils.maskHTML(readstatus.toString());
	}

	/**
	 * 
	 * @created 29.05.2011
	 * @return
	 */
	private List<Section<DashTreeElement>> getALlUnits() {
		List<Section<DashTreeElement>> units = new LinkedList<Section<DashTreeElement>>();
		KnowWEArticle leftMenu = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB).getArticle("LeftMenu");

		if (leftMenu != null) {
			Section<DynamicMenuMarkup> menu = Sections.findSuccessor(
					leftMenu.getSection(),
					DynamicMenuMarkup.class);
			Sections.findSuccessorsOfType(menu, DashTreeElement.class, units);
		}

		return units;
	}

	/**
	 * 
	 * @created 31.05.2011
	 * @return
	 */
	private List<Date> getTimeTable() {
		List<Date> dates = new ArrayList<Date>();
		KnowWEArticle zeitplanArticle = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB).getArticle("Zeitplan");

		if (zeitplanArticle != null) {
			Section<TimeTableMarkup> timetable = Sections.findSuccessor(
					zeitplanArticle.getSection(), TimeTableMarkup.class);
			if (timetable != null) {
				dates = TimeTableMarkup.getDates(timetable);
			}
		}

		return dates;
	}

	/**
	 * 
	 * @created 29.05.2011
	 * @param rootUnit
	 * @return
	 */
	private List<Section<DashTreeElement>> getSubUnits(Section<DashTreeElement> rootUnit, List<Section<DashTreeElement>> units) {
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

			if (add) subUnits.add(sec);
		}

		return subUnits;
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
			KnowWEArticle unit = KnowWEEnvironment.getInstance().getArticleManager(
					KnowWEEnvironment.DEFAULT_WEB).getArticle(getPageName(s));

			if (unit != null) {
				List<Section<? extends Type>> allNodes = unit.getAllNodesPreOrder();
				Section<? extends Type> node;

				for (int i = 0; i < allNodes.size(); i++) {
					node = allNodes.get(i);

					if (node.get().toString().contains("TagHandlerType")
							&& node.toString().contains("KnowWEPlugin readbutton"))
						readbuttons.add(getPageName(s)
								+ "::" + getReadButtonID(node.toString()));
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
		String[] readpages = new String[0];
		KnowWEArticle userData = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB).getArticle(userName + "_data");

		if (userData != null) {
			Section<DataMarkup> data = Sections.findSuccessor(
					userData.getSection(), DataMarkup.class);
			if (data != null && DataMarkup.getAnnotation(data, "readpages") != null) {
				// Hole alle gelesenen Readbuttons
				readpages = DataMarkup.getAnnotation(data, "readpages").split(";");
				// Ist gesuchter dabei?
				for (String s : readpages) {
					// Vergleiche pagenames und ids
					if (s.split("::")[0].equals(readbutton.split("::")[0])
							&& s.split("::")[1].equals(readbutton.split("::")[1])) {
						return true;
					}
				}
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