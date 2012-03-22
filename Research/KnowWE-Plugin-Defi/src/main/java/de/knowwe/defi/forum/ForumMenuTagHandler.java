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
package de.knowwe.defi.forum;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ecyrd.jspwiki.WikiEngine;

import de.knowwe.comment.forum.Forum;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.defi.aboutMe.AboutMe;
import de.knowwe.defi.logger.PageLoggerHandler;
import de.knowwe.defi.menu.DynamicMenuMarkup;
import de.knowwe.defi.time.TimeTableMarkup;
import de.knowwe.jspwiki.JSPWikiConnector;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.dashtree.DashTreeUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author dupke
 * @created 03.08.2011
 */
public class ForumMenuTagHandler extends AbstractTagHandler {

	/** Label für Einheiten-Foren */
	private static final String UNIT_DISCUSSION_LABEL = "Themenüberblick";
	/** Label für sonstige Foren */
	private static final String LEFTOVER_LABEL = "Sonstige Themen";
	/** Label für User-Chat */
	private static final String USER_CHAT_LABEL = "Mit anderen Benutzern diskutieren";

	/** Beschriftungen für das "Neues Forum"-Formular */
	private static final String OPEN_FORM_BUTTON = "Neues Thema";
	private static final String CLOSE_FORM_BUTTON = "Abbrechen";
	private static final String SEND_BUTTON = "Abschicken";
	private static final String FORM_LABEL = "Neues Forum erstellen:";
	private static final String TEXTAREA_LABEL = "Geben Sie bitte Ihre Nachricht ein:";
	private static final String TOPIC_LABEL = "Geben Sie bitte eine &Uuml;berschrift ein:";

	/** Button für persönliche Nachricht an Benutzer */
	private static final String PERSONAL_MESSAGE_BUTTON = "Pers&ouml;nliche Nachricht";

	/** Größe der Eingabefelder */
	private static final int TOPIC_PANEL_SIZE = 70;
	private static final int TEXTAREA_COLS = 55;
	private static final int TEXTAREA_ROWS = 10;

	/**
	 * @param name
	 */
	public ForumMenuTagHandler() {
		super("forummenu");
	}

	@Override
	public String render(Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuilder fm = new StringBuilder();
		Iterator<Article> it = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB).getArticleIterator();
		List<Section<? extends Forum>> forums = new LinkedList<Section<? extends Forum>>();
		List<Section<? extends Forum>> chats = new LinkedList<Section<? extends Forum>>();
		List<Section<? extends Forum>> other = new LinkedList<Section<? extends Forum>>();
		String pageName, unit, topic, lastVisit;
		boolean noForum = true, newEntry = false;
		int numberOfNewEntries = 0;
		HashMap<String, String> logPages = checkLog(userContext);

		// Hole Lektionen aus dem Left Menu
		List<Section<DashTreeElement>> rootUnits = getAllRootUnits();

		// Hole alle Foren
		while (it.hasNext()) {
			for (Section<? extends Type> sec : it.next().getAllNodesPreOrder()) {
				Section<? extends Forum> forum = Sections.findSuccessor(sec, Forum.class);
				if (forum != null && !forums.contains(forum)) forums.add(forum);
			}

		}
		// Kopiere Forum-Liste
		other.addAll(forums);

		/* ######################################### */
		/* # -----------EINHEITEN-FOREN----------- # */
		/* ######################################### */
		fm.append("<table class='forumMenu'>");
		fm.append("<tr><th colspan=2>" + UNIT_DISCUSSION_LABEL + "</th></tr>");
		for (Section<DashTreeElement> rootUnit : rootUnits) {
			newEntry = false;
			pageName = getPageName(rootUnit);
			fm.append("\n"); // "10000 characters without linebreak-bug"
			if (isFree(rootUnit)) {
				fm.append("<tr class='active'><td>" + pageName);

				// "Neues Forum"-Button
				fm.append(" <input class='fm_open' type='button' value='"
						+ OPEN_FORM_BUTTON
						+ "' onclick='forumForm(\"" + pageName
						+ "\");return false' />");

				// Auflistung der Foren
				fm.append("<div name='" + pageName + "_forums'><ul>");
				for (Section<? extends Forum> sec : forums) {
					// Hole Attribute aus Forum-XML
					unit = getForumAttribute("unit", sec.getText());
					topic = getForumAttribute("topic", sec.getText());

					if (pageName.equals(unit.split(":")[0])) {

						// Testen ob es neue Beiträge gibt
						lastVisit = logPages.get(sec.getTitle());
						if (userContext.userIsAsserted()) {

							// Benutzer war noch nicht in diesem Forum
							if (lastVisit == null) {
								// Hat er es selbst erstellt?
								if (userContext.getUserName().equals(
										getAuthor(sec.getText()))) {
									// Gibt es mehr als den eigenen Eintrag?
									numberOfNewEntries = getNumberOfNewEntries(
											sec.getText(),
											"01.01.1900 00:00") - 1;
								}
								else numberOfNewEntries = -1;
							}
							else {
								numberOfNewEntries = getNumberOfNewEntries(sec.getText(),
										lastVisit);
							}
							if (numberOfNewEntries != 0) newEntry = true;
						}

						fm.append("<li><a href='Wiki.jsp?page=" + sec.getTitle() + "'>" + topic
								+ "</a>");
						if (numberOfNewEntries == -1) fm.append("&nbsp;<span class='fm_new'>(ungelesenes Thema)</span>");
						else if (numberOfNewEntries == 1) fm.append("&nbsp;<span class='fm_new'>(1 neuer Beitrag)</span>");
						else if (numberOfNewEntries > 0)
							fm.append("&nbsp;<span class='fm_new'>("
									+ numberOfNewEntries + " neue Beiträge)</span>");
						fm.append("</li>");
						other.remove(sec);
						noForum = false;
					}
					else if (topic.startsWith("Persönliche Nachrichten(")) {
						chats.add(sec);
						other.remove(sec);
					}
				}
				if (noForum) fm.append("<li>Noch keine Foren vorhanden</p></li>");
				noForum = true;
				fm.append("</ul></div>");

				// Formular zur Erstellung eines neuen Forums
				fm.append("<br />" + buildForm(pageName, newEntry));
				fm.append("</tr>");
			}
			else {
				fm.append("<tr class='inactive'><td colspan='2'>" + pageName
						+ "</td></tr>");
			}
		}

		/* ######################################### */
		/* # ------------BENUTZER-CHAT------------ # */
		/* ######################################### */
		fm.append("<tr><th colspan=2>" + USER_CHAT_LABEL + "</th></tr>");
		JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(
				Environment.getInstance().getContext(), null));
		String[] users = wc.getAllUsers();
		String[] activeUsers = wc.getAllActiveUsers();
		List<String> admins = getAdmins();

		for (int i = 0; i < users.length; i++) {
			// filter displayed users
			if (admins.contains(users[i]) || users[i].startsWith("Patient")
					|| users[i].equals(userContext.getUserName())) continue;
			else {
				newEntry = false;
				noForum = true;
				// Sortiere Namen alphabetisch
				String[] names = {
						userContext.getUserName(), users[i] };
				Arrays.sort(names);
				// Hole Section des entsprechenden Chats
				for (Section<? extends Forum> sec : chats) {
					topic = getForumAttribute("topic", sec.getText());
					if (topic.equals("Persönliche Nachrichten(" + names[0] + ","
							+ names[1] + ")")) {
						noForum = false;

						// Testen ob es neue Beiträge gibt
						lastVisit = logPages.get(sec.getTitle());
						if (userContext.userIsAsserted()) {

							// Benutzer war noch nicht in diesem Forum
							if (lastVisit == null) {
								// Hat er es selbst erstellt?
								if (userContext.getUserName().equals(
										getAuthor(sec.getText()))) {
									// Gibt es mehr als den eigenen Eintrag?
									numberOfNewEntries = getNumberOfNewEntries(
											sec.getText(),
											"01.01.1900 00:00") - 1;
								}
								else numberOfNewEntries = -1;
							}
							else {
								numberOfNewEntries = getNumberOfNewEntries(sec.getText(),
										lastVisit);
							}

							if (numberOfNewEntries != 0) newEntry = true;
						}

						break;
					}

				}

				fm.append("<tr class='active'><td>");

				fm.append("<div class='userchat'>");
				// Avatar
				fm.append("<img src=\"KnowWEExtension/images/avatars/"
						+ getAvatar(users[i])
						+ "\" height=\"80px\" width=\"80px\" alt=\"avatar\" />");

				// Username + status
				fm.append("<span class='userchat'><a href='"
						+ JSPWikiConnector.LINK_PREFIX
						+ users[i] + "'>"
						+ users[i] + "</a><br />- " + getStatus(activeUsers, users[i])
						+ " -</span>");

				// "Persönliche Nachricht"-Button
				fm.append("<input class='fm_open' type='button' value='"
						+ PERSONAL_MESSAGE_BUTTON
						+ "' onclick='newChat(\"" + names[0] + "\", \"" + names[1]
						+ "\");return false' />");
				fm.append("</div>");

				if (newEntry) fm.append("</td><td class='fm_new'></td></tr>");
				else fm.append("</td><td class='fm_old'></td></tr>");
			}

		}

		/* ######################################### */
		/* # -----------SONSTIGE-FOREN------------ # */
		/* ######################################### */
		fm.append("<tr><th colspan=2>" + LEFTOVER_LABEL + "</th></tr>");
		pageName = "Sonstiges";
		fm.append("<tr class='active'><td>");

		// "Neues Forum"-Button
		fm.append("<input class='fm_open' type='button' value='"
				+ OPEN_FORM_BUTTON
				+ "' onclick='forumForm(\"" + pageName
				+ "\");return false' />");

		// Auflistung der Foren
		fm.append("<div name='" + pageName + "_forums'><ul>");
		for (Section<? extends Forum> sec : other) {
			newEntry = false;
			// Hole Attribute aus Forum-XML
			topic = getForumAttribute("topic", sec.getText());
			if (topic == "") topic = sec.getTitle();

			// Testen ob es neue Beiträge gibt
			lastVisit = logPages.get(sec.getTitle());
			if (userContext.userIsAsserted()) {

				if (lastVisit == null) {
					// Hat er es selbst erstellt?
					if (userContext.getUserName().equals(getAuthor(sec.getText()))) {
						// Gibt es mehr als den eigenen Eintrag?
						numberOfNewEntries = getNumberOfNewEntries(sec.getText(),
								"01.01.1900 00:00") - 1;
					}
					else numberOfNewEntries = -1;
				}
				else {
					numberOfNewEntries = getNumberOfNewEntries(sec.getText(),
							lastVisit);
				}

				if (numberOfNewEntries != 0) newEntry = true;
			}

			fm.append("<li><a href='Wiki.jsp?page=" + sec.getTitle() + "'>"
					+ topic + "</a>");
			if (numberOfNewEntries == -1) fm.append("&nbsp;<span class='fm_new'>(ungelesenes Thema)</span>");
			else if (numberOfNewEntries == 1) fm.append("&nbsp;<span class='fm_new'>(1 neuer Beitrag)</span>");
			else if (numberOfNewEntries > 0) fm.append("&nbsp;<span class='fm_new'>("
					+ numberOfNewEntries + " neue Beiträge)</span>");
			fm.append("</li>");
			noForum = false;
		}
		if (noForum) fm.append("<li>Noch keine Foren vorhanden</p></li>");
		noForum = true;
		fm.append("</ul></div>");

		// Formular zur Erstellung eines neuen Forums
		fm.append("<br />" + buildForm(pageName, newEntry));
		fm.append("</tr>");

		fm.append("</table>");

		return KnowWEUtils.maskHTML(fm.toString());
	}

	/**
	 * Get all root units
	 */
	private List<Section<DashTreeElement>> getAllRootUnits() {
		List<Section<DashTreeElement>> units = new LinkedList<Section<DashTreeElement>>();
		List<Section<DashTreeElement>> rootUnits = new LinkedList<Section<DashTreeElement>>();
		Article leftMenu = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB).getArticle("LeftMenu");

		if (leftMenu != null) {
			Section<DynamicMenuMarkup> menu = Sections.findSuccessor(
					leftMenu.getSection(),
					DynamicMenuMarkup.class);
			Sections.findSuccessorsOfType(menu, DashTreeElement.class, units);
		}

		for (Section<DashTreeElement> unit : units) {
			if (DashTreeUtils.getDashLevel(unit) == 0) rootUnits.add(unit);
		}

		return rootUnits;
	}

	/**
	 * Get the page's name.
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
	 * Get HTML-Form for a new forum.
	 */
	private String buildForm(String pageName, boolean newEntry) {
		StringBuilder builder = new StringBuilder();

		// Formular zur Erstellung eines neuen Forums
		builder.append("<form style='display:none' name='" + pageName
				+ "'><br /><h4>" + FORM_LABEL + "</h4>");

		// ++ Ueberschrift
		builder.append("<p class='fm_newforum'>" + TOPIC_LABEL
				+ ":<br /><input type='text' size='" + TOPIC_PANEL_SIZE + "' name='"
				+ pageName
				+ "_topic' /></p>");

		// ++ Nachrichteneingabe
		builder.append("<p>" + TEXTAREA_LABEL + "</p><textarea name='" + pageName
				+ "_text' rows='" + TEXTAREA_ROWS + "' cols='" + TEXTAREA_COLS
				+ "'></textarea><br />");
		builder.append("<input type='button' value='" + SEND_BUTTON +
				"' onclick='sendforumForm(\""
				+ pageName +
				"\");return false' /><input type='button' onclick='forumForm(\""
				+ pageName
				+ "\");return false' value='" + CLOSE_FORM_BUTTON + "' /></form>");

		if (newEntry) {
			builder.append("</td><td class='fm_new'></td>");
		}
		else {
			builder.append("</td><td class='fm_old'></td>");
		}

		return builder.toString();
	}

	/**
	 * 
	 */
	private boolean isFree(Section<DashTreeElement> sec) {
		Section<? extends Type> dashtree = sec.getFather().getFather().getFather();
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

		Article zeitplanArticle = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB).getArticle(
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

	/**
	 * Get a forum's attribute.
	 */
	private String getForumAttribute(String attribute, String forumXML) {
		String attributeContent = "";

		try {
			DocumentBuilderFactory dbf =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(forumXML));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("forum");

			Element knoten = (Element) nodes.item(0);
			attributeContent = knoten.getAttribute(attribute);

		}
		catch (Exception e) {
			return "";
		}

		return attributeContent;
	}

	/**
	 * Get the number of new entries in a forum.
	 */
	private int getNumberOfNewEntries(String forumXML, String date) {
		int number = 0;
		Date thresholdDate, entryDate;

		thresholdDate = stringToDate(date);
		if (thresholdDate != null) {

			try {
				DocumentBuilderFactory dbf =
						DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(forumXML));

				Document doc = db.parse(is);
				NodeList nodes = doc.getElementsByTagName("forum");
				NodeList boxes = ((Element) nodes.item(0)).getElementsByTagName("box");

				for (int i = 0; i < boxes.getLength(); i++) {
					Element element = (Element) boxes.item(i);
					entryDate = stringToDate(element.getAttribute("date"));

					if (entryDate.after(thresholdDate)) number++;
				}
			}
			catch (Exception e) {
			}
		}

		return number;
	}

	/**
	 * Get a forum's author.
	 */
	private String getAuthor(String forumXML) {
		String author = "";

		try {
			DocumentBuilderFactory dbf =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(forumXML));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("forum");
			Element firstBox = (Element) ((Element) nodes.item(0)).getElementsByTagName(
					"box").item(0);
			author = firstBox.getAttribute("name");
		}
		catch (Exception e) {
		}

		return author;
	}

	/**
	 * Get all admins.
	 */
	@SuppressWarnings("unchecked")
	private List<String> getAdmins() {
		JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(
				Environment.getInstance().getContext(), null));
		List<String> admins = new LinkedList<String>();
		SAXBuilder sxbuild = new SAXBuilder();
		InputSource is = new InputSource(wc.getWikiProperty("jspwiki.xmlGroupDatabaseFile"));
		org.jdom.Document doc;
		try {
			doc = sxbuild.build(is);
			org.jdom.Element root = doc.getRootElement();
			List<org.jdom.Element> elms, groups;
			elms = root.getChildren("group");
			for (org.jdom.Element elm : elms) {
				if (elm.getAttributeValue("name").equals("Admin")) {
					groups = elm.getChildren();
					for (org.jdom.Element elem : groups) {
						admins.add(getWikiName(elem.getAttributeValue("principal")));
					}
				}
			}
		}
		catch (JDOMException e) {
		}
		catch (IOException e) {
		}

		return admins;
	}

	/**
	 * Transform loginName into WikiName.
	 */
	@SuppressWarnings("unchecked")
	private String getWikiName(String loginName) {
		JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(
				Environment.getInstance().getContext(), null));
		SAXBuilder sxbuild = new SAXBuilder();
		InputSource is = new InputSource(wc.getWikiProperty("jspwiki.xmlUserDatabaseFile"));
		org.jdom.Document doc;
		try {
			doc = sxbuild.build(is);
			org.jdom.Element root = doc.getRootElement();
			List<org.jdom.Element> elms = root.getChildren("user");
			for (org.jdom.Element elm : elms) {
				if (elm.getAttributeValue("loginName").equals(loginName)) {
					return elm.getAttributeValue("fullName");
				}
			}
		}
		catch (JDOMException e) {
		}
		catch (IOException e) {
		}

		return "";
	}

	/**
	 * check the log.
	 */
	private HashMap<String, String> checkLog(UserContext uc) {
		HashMap<String, String> logPages = new HashMap<String, String>();
		String log = PageLoggerHandler.getPath();
		String line;

		try {
			BufferedReader br = new BufferedReader(new FileReader(log));
			while ((line = br.readLine()) != null) {
				if (uc.getUserName().equals(line.split(";")[1])) {
					logPages.put(line.split(";")[2], line.split(";")[0]);
				}
			}
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
		}

		return logPages;
	}

	/**
	 * String into Date.
	 */
	private Date stringToDate(String s) {
		SimpleDateFormat sdfToDate = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date date = null;

		try {
			date = sdfToDate.parse(s);
		}
		catch (ParseException e) {
			try {
				sdfToDate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
				date = sdfToDate.parse(s);
			}
			catch (ParseException e1) {
				try {
					sdfToDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
					date = sdfToDate.parse(s);
				}
				catch (ParseException e2) {
				}
			}
		}

		return date;
	}

	/**
	 * Get the user's avatar.
	 * 
	 * @param userName
	 * @return
	 */
	private String getAvatar(String userName) {
		String avatar = "";

		try {
			Article article = Environment.getInstance().getArticle(
					Environment.DEFAULT_WEB, userName);
			Section<?> s = article.getSection();
			Section<AboutMe> sec = Sections.findSuccessor(s, AboutMe.class);
			avatar = DefaultMarkupType.getAnnotation(sec, "avatar");
		}
		catch (NullPointerException e) {
			// e.printStackTrace();
		}
		if (avatar == null || avatar == "")
			avatar = "1000px-Comic_image_missing.svg.jpg";

		return avatar;
	}

	/**
	 * Get the user's status.
	 */
	private String getStatus(String[] activeUsers, String userName) {

		for (String s : activeUsers) {
			if (s.equals(userName)) return "<span class='online'>online</span>";
		}

		return "<span class='offline'>offline</span>";
	}

}
