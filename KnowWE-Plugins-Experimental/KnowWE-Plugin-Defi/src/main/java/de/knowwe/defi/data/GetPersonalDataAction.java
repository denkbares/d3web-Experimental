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
package de.knowwe.defi.data;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.d3web.strings.Strings;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.wikiConnector.WikiAttachment;
import de.knowwe.defi.logger.DefiCommentLogLine;
import de.knowwe.defi.logger.DefiOtherEventsLogger;
import de.knowwe.defi.logger.DefiPageEventLogger;
import de.knowwe.defi.logger.DefiPageLogLine;
import de.knowwe.defi.logger.DefiSessionEventLogger;
import de.knowwe.defi.logger.DefiSessionLogLine;
import de.knowwe.defi.menu.MenuUtilities;
import de.knowwe.defi.readbutton.ReadbuttonUtilities;
import de.knowwe.defi.table.TableEntryType;
import de.knowwe.defi.user.UserUtilities;
import de.knowwe.defi.utils.DefiUtils;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author dupke
 * @created 20.08.2013
 */
public class GetPersonalDataAction extends AbstractAction {

	private final String LS = System.getProperty("line.separator");
	private final String SEP = "-----------------------------------" + LS;

	@Override
	public void execute(UserActionContext context) throws IOException {
		String user = context.getParameter("user");
		String title = context.getTitle();

		StringBuilder xml = new StringBuilder();
		xml.append(getSessionData(user));
		xml.append(getPageData(user));
		xml.append(getExternalLinkData(user));
		xml.append(getForumData(user));
		xml.append(getEmergencyPlanData(user));
		xml.append(getFeedbackData(user));
		xml.append(getReadbuttonData(user));

		File dataFile = new File(Environment.getInstance().getWikiConnector().getSavePath() + "/"
				+ user
				+ "_userdata.txt");
		Strings.writeFile(dataFile.getPath(), xml.toString());
		WikiAttachment wa = Environment.getInstance().getWikiConnector().storeAttachment(title,
				"Wiki", dataFile);

		context.getWriter().write("attach/" + wa.getPath());
	}

	private String getSessionData(String user) {
		StringBuilder xml = new StringBuilder();
		List<DefiSessionLogLine> sessionsForUser = new ArrayList<>();
		int timeOutsForUser = 0;
		// session data
		for (DefiSessionLogLine logLine : DefiSessionEventLogger.getLogLines()) {
			if (user.equals(logLine.getUser())) {
				sessionsForUser.add(logLine);
				if (logLine.getTimeout().equals("true")) timeOutsForUser++;
			}
		}

		String firstSession = "";
		String lastSession = "";
		if (!sessionsForUser.isEmpty()) {
			firstSession = sessionsForUser.get(0).getLoginDateTime();
			lastSession = sessionsForUser.get(sessionsForUser.size() - 1).getLoginDateTime();
		}

		xml.append(SEP).append("Erster Login").append(LS).append(SEP);
		xml.append("- ").append(firstSession).append(LS);
		xml.append(LS);
		xml.append(SEP).append("Letzter Login").append(LS).append(SEP);
		xml.append("- ").append(lastSession).append(LS);
		xml.append(LS);
		xml.append(SEP).append("Anzahl Logins").append(LS).append(SEP);
		xml.append("- ").append(sessionsForUser.size()).append(LS);
		xml.append(LS);
		xml.append(SEP).append("Anzahl Timeouts").append(LS).append(SEP);
		xml.append("- ").append(timeOutsForUser).append(LS);
		xml.append(LS);

		return xml.toString();
	}

	private String getPageData(String user) {
		StringBuilder xml = new StringBuilder();
		List<String> visitedPages = UserUtilities.getVisitedPages(user);
		List<String> ratedPages = ReadbuttonUtilities.getRatedPages(user);
		// visits/rates per unit
		HashMap<Section<DashTreeElement>, Integer> visitsPerUnit = new HashMap<>();
		HashMap<Section<DashTreeElement>, Integer> ratesPerUnit = new HashMap<>();
		for (Section<DashTreeElement> rootUnit : MenuUtilities.getRootUnits()) {
			visitsPerUnit.put(rootUnit, 0);
			ratesPerUnit.put(rootUnit, 0);
			for (Section<DashTreeElement> unit : MenuUtilities.getSubUnits(rootUnit)) {
				String unitpage = MenuUtilities.getUnitPagename(unit);
				for (String page : visitedPages) {
					if (page.equals(unitpage)) visitsPerUnit.put(rootUnit,
							visitsPerUnit.get(rootUnit) + 1);
				}
				for (String page : ratedPages) {
					if (page.equals(unitpage)) ratesPerUnit.put(rootUnit,
							ratesPerUnit.get(rootUnit) + 1);
				}
			}
		}
		// visits per page
		HashMap<String, Integer> visitsPerPage = new HashMap<>();
		List<String> allArticles = DefiUtils.getAllDefiArticleNames();
		for (String page : allArticles) {
			visitsPerPage.put(page, 0);
		}
		for (String page : visitedPages) {
			if (visitsPerPage.containsKey(page)) visitsPerPage.put(page,
					visitsPerPage.get(page) + 1);
			else visitsPerPage.put(page, 1);
		}

		// render visited pages
		xml.append(SEP).append("Anzahl besuchter Seiten").append(LS).append(SEP);
		xml.append("- Gesamt: ").append(visitedPages.size()).append(LS);
		for (Section<DashTreeElement> unit : MenuUtilities.getRootUnits()) {
			xml.append("- ")
					.append(MenuUtilities.getUnitPagename(unit))
					.append(": ")
					.append(visitsPerUnit.get(unit))
					.append(LS);
		}
		xml.append(LS);
		// render rated pages
		xml.append(SEP).append("Anzahl bewerteter Seiten").append(LS).append(SEP);
		xml.append("- Gesamt: ").append(ratedPages.size()).append(LS);
		for (Section<DashTreeElement> unit : MenuUtilities.getRootUnits()) {
			xml.append("- ")
					.append(MenuUtilities.getUnitPagename(unit))
					.append(": ")
					.append(ratesPerUnit.get(unit))
					.append(LS);
		}
		xml.append(LS);
		// render number of visits per page
		xml.append(SEP).append("Anzahl Besuche pro Seite").append(LS).append(SEP);
		for (String page : allArticles) {
			xml.append("- ").append(page).append(": ").append(visitsPerPage.get(page)).append(LS);
		}
		xml.append(LS);

		return xml.toString();
	}

	private String getExternalLinkData(String user) {
		StringBuilder xml = new StringBuilder();
		List<String> logLines = DefiOtherEventsLogger.getLogLines(DefiOtherEventsLogger.EXT_LINK_PREFIX);
		List<String> logLinesForUser = new LinkedList<>();

		for (String logline : logLines) {
			if (logline.split(DefiOtherEventsLogger.SEPARATOR)[1].equals(user)) logLinesForUser.add(logline);
		}

		// render external link data
		xml.append(SEP).append("Externe Seiten besucht").append(LS).append(SEP);
		xml.append("- ").append(logLinesForUser.size() > 0).append(LS);
		xml.append(LS);
		xml.append(SEP).append("Anzahl Besuche von externen Seite").append(LS).append(SEP);
		xml.append("- ").append(logLinesForUser.size()).append(LS);
		xml.append(LS);

		return xml.toString();
	}

	private String getForumData(String user) {
		StringBuilder xml = new StringBuilder();
		List<DefiCommentLogLine> userComments = UserUtilities.getComments(user);
		int NumberOfStartComments = 0;
		HashMap<Section<DashTreeElement>, Integer> commentsPerUnit = new HashMap<>();
		HashMap<Section<DashTreeElement>, Integer> startcomPerUnit = new HashMap<>();
		for (Section<DashTreeElement> rootUnit : MenuUtilities.getRootUnits()) {
			commentsPerUnit.put(rootUnit, 0);
			startcomPerUnit.put(rootUnit, 0);
			String unit = MenuUtilities.getUnitPagename(rootUnit);
			for (DefiCommentLogLine comment : userComments) {
				if (comment.getUnit().equals(unit)) {
					commentsPerUnit.put(rootUnit, commentsPerUnit.get(rootUnit) + 1);

					if (comment.getResponse().equals("Startbeitrag")) {
						startcomPerUnit.put(rootUnit, startcomPerUnit.get(rootUnit) + 1);
						NumberOfStartComments++;
					}
				}
			}
		}

		xml.append(SEP).append("Anzahl der Forenbeiträge").append(LS).append(SEP);
		xml.append("- Gesamt: ").append(userComments.size()).append(LS);
		for (Section<DashTreeElement> unit : MenuUtilities.getRootUnits()) {
			xml.append("- ")
					.append(MenuUtilities.getUnitPagename(unit))
					.append(": ")
					.append(commentsPerUnit.get(unit))
					.append(LS);
		}
		xml.append(LS);
		xml.append(SEP).append("Anzahl der Startbeiträge").append(LS).append(SEP);
		xml.append("- Gesamt: ").append(NumberOfStartComments).append(LS);
		for (Section<DashTreeElement> unit : MenuUtilities.getRootUnits()) {
			xml.append("- ")
					.append(MenuUtilities.getUnitPagename(unit))
					.append(": ")
					.append(startcomPerUnit.get(unit))
					.append(LS);
		}
		xml.append(LS);
		return xml.toString();
	}

	private String getEmergencyPlanData(String user) {
		StringBuilder xml = new StringBuilder();
		Article dataPage = UserUtilities.getDataPage(user);
		List<String> ids = new LinkedList<>();
		Date lastChange = null;
		boolean planChanged = false;
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy, HH:mm");
		ids.add("name");
		ids.add("model");
		ids.add("disease");
		ids.add("blood");
		for (int i = 1; i < 4; i++) {
			ids.add("tel" + i);
		}
		for (int i = 1; i < 5; i++) {
			ids.add("med" + i);
		}
		// last change on emergencyplan
		for (Section<TableEntryType> table : Sections.children(dataPage.getRootSection(),
				TableEntryType.class)) {
			String id = DefaultMarkupType.getAnnotation(table, "tableid");
			String date = DefaultMarkupType.getAnnotation(table, "date");
			if (ids.contains(id)) {
				planChanged = true;
				try {
					Date tmp = sdf.parse(date);
					if (lastChange == null || tmp.before(lastChange)) lastChange = tmp;
				}
				catch (ParseException e) {
				}
			}
		}
		String lastChangeString = "";
		if (lastChange != null) lastChangeString = new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(lastChange);
		else if (planChanged) lastChangeString = "Error";

		// last visit of emergencyplan
		String lastVisit = "";
		for (DefiPageLogLine page : DefiPageEventLogger.getLogLines()) {
			if (page.getPage().equals("EmergencyPlan.jsp") && page.getUser().equals(user)) {
				lastVisit = page.getStartDate() + " " + page.getstartTime();
			}
		}

		xml.append(SEP).append("Notfallplan erstellt").append(LS).append(SEP);
		xml.append("- ").append(planChanged).append(LS);
		xml.append(LS);
		xml.append(SEP).append("Datum der letzten Bearbeitung des Notfallplans").append(LS).append(SEP);
		xml.append("- ").append(lastChangeString).append(LS);
		xml.append(LS);
		xml.append(SEP).append("Notfallplan aufgerufen").append(LS).append(SEP);
		xml.append("- ").append(!lastVisit.equals("")).append(LS);
		xml.append(LS);
		xml.append(SEP).append("Letzter Aufrufs des Notfallplans").append(LS).append(SEP);
		xml.append("- ").append(lastVisit).append(LS);
		xml.append(LS);

		return xml.toString();
	}

	private String getFeedbackData(String user) {
		StringBuilder xml = new StringBuilder();
		boolean feedback = false;
		List<String> logLines = DefiOtherEventsLogger.getLogLines(DefiOtherEventsLogger.FEEDBACK_PREFIX);
		List<String> logLinesForUser = new ArrayList<>();
		for (String logline : logLines) {
			String logUser = logline.split(DefiOtherEventsLogger.SEPARATOR)[1];
			if (logUser.equals(user)) logLinesForUser.add(logline);
		}

		String firstDate = "";
		String lastDate = "";
		feedback = !logLinesForUser.isEmpty();

		if (feedback) {
			firstDate = logLinesForUser.get(0).split(DefiOtherEventsLogger.SEPARATOR)[2];
			lastDate = logLinesForUser.get(logLinesForUser.size() - 1).split(
					DefiOtherEventsLogger.SEPARATOR)[2];
		}

		xml.append(SEP).append("Abschlussfragebogen ausgefüllt").append(LS).append(SEP);
		xml.append("- ").append(feedback).append(LS);
		xml.append(LS);
		xml.append(SEP).append("Abschlussfragebogen - Ersten Speicherung").append(LS).append(SEP);
		xml.append("- ").append(firstDate).append(LS);
		xml.append(LS);
		xml.append(SEP).append("Abschlussfragebogen - Letzte Speicherung").append(LS).append(SEP);
		xml.append("- ").append(lastDate).append(LS);
		xml.append(LS);
		return xml.toString();
	}

	private String getReadbuttonData(String user) {
		// TODO Readbuttonlog auslesen
		// StringBuilder xml = new StringBuilder();
		// Section<RootType> dataPage =
		// UserUtilities.getDataPage(user).getRootSection();
		// xml.append(SEP + "Bewertungen" + LS + SEP);
		// for (Section<ReadbuttonType> readButton :
		// Sections.children(dataPage,
		// ReadbuttonType.class)) {
		// String id = DefaultMarkupType.getAnnotation(readButton, "id");
		// String page = DefaultMarkupType.getAnnotation(readButton, "page");
		// String value = DefaultMarkupType.getAnnotation(readButton,
		// "realvalue");
		// String label = DefaultMarkupType.getAnnotation(readButton, "label");
		// String date = DefaultMarkupType.getAnnotation(readButton, "date");
		// xml.append("- " + page + "(" + id + ")" + " am " + date + ": " +
		// value + "(" + label
		// + ")" + LS);
		// }
		// xml.append(LS);

		return "";
	}

}
