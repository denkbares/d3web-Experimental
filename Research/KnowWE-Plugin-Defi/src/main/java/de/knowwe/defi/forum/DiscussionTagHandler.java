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
package de.knowwe.defi.forum;

import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.wiki.WikiEngine;
import org.apache.wiki.auth.NoSuchPrincipalException;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.authorize.GroupManager;
import org.apache.wiki.auth.user.UserDatabase;

import de.d3web.strings.Strings;
import de.knowwe.comment.forum.Forum;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.defi.aboutMe.AboutMe;
import de.knowwe.defi.menu.MenuUtilities;
import de.knowwe.jspwiki.JSPWikiConnector;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.xml.AbstractXMLType;

/**
 * Übersicht über alle Foren.
 * 
 * @author dupke
 * @created 18.07.2013
 */
public class DiscussionTagHandler extends AbstractTagHandler {

	/** Label für Einheitenforen */
	private static final String UNIT_LABEL = "Themenüberblick";
	/** Label für sonstige Foren */
	private static final String OTHERS_LABEL = "Sonstige Themen";
	/** Label für Chats */
	private static final String CHATS_LABEL = "Mit anderen Benutzern diskutieren";
	/** Label für fehlerhafte Forent */
	private static final String ERRORS_LABEL = "Fehlerhafte Foren (Nur für Admins sichtbar)";
	/** Hinweis für beitragslose Kategorien **/
	private static final String NO_FORUMS = "Es wurden noch keine Beiträge erstellt";
	/** Button für persönliche Nachricht an Benutzer */
	private static final String PERSONAL_MESSAGE_BUTTON = "Pers&ouml;nliche Nachricht";
	/** Beschriftungen für das "Neues Forum"-Formular */
	private static final String OPEN_FORM_BUTTON = "Neues Thema";
	private static final String CLOSE_FORM_BUTTON = "Abbrechen";
	private static final String SEND_BUTTON = "Abschicken";
	private static final String FORM_LABEL = "Neues Forum erstellen:";
	private static final String TEXTAREA_LABEL = "Geben Sie bitte Ihre Nachricht ein:";
	private static final String TOPIC_LABEL = "Geben Sie bitte eine &Uuml;berschrift ein:";
	/** Größe der Eingabefelder */
	private static final String TOPIC_PANEL_WIDTH = "700px";
	private static final String TEXTAREA_WIDTH = "700px";
	private static final String TEXTAREA_HEIGHT = "200px";

	public DiscussionTagHandler() {
		super("discussion");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {
		StringBuilder disc = new StringBuilder();
		List<Section<? extends Forum>> units = new LinkedList<Section<? extends Forum>>();
		List<Section<? extends Forum>> chats = new LinkedList<Section<? extends Forum>>();
		List<Section<? extends Forum>> others = new LinkedList<Section<? extends Forum>>();
		List<Section<? extends Forum>> errors = new LinkedList<Section<? extends Forum>>();

		// Hole Lektionennamen aus dem Left Menu
		List<String> rootUnitNames = new LinkedList<String>();
		for (Section<DashTreeElement> rootUnit : MenuUtilities.getRootUnits()) {
			rootUnitNames.add(MenuUtilities.getUnitPagename(rootUnit));
		}

		// Foren zuordnen
		for (Section<? extends Forum> forum : DiscussionUtils.getAllForums()) {
			Map<String, String> mapFor = AbstractXMLType.getAttributes(forum);
			String unit = mapFor.get("unit");

			if (unit == null) errors.add(forum);
			else if (unit.equals("chat")) chats.add(forum);
			else if (unit.equals("Sonstiges")) others.add(forum);
			else if (rootUnitNames.contains(unit)) units.add(forum);
			else errors.add(forum);
		}

		disc.append("<table class='discussion'>");
		disc.append(buildUnitRows(units, userContext.getUserName()));
		disc.append(buildChatRows(chats, userContext.getUserName()));
		disc.append(buildOtherRow(others, userContext.getUserName()));
		if (userContext.userIsAdmin()) {
			disc.append(buildErrorRow(errors));
		}
		disc.append("</table>");

		result.appendHtml(disc.toString());
	}

	/**
	 * Baue Reihen für Einheitenforen.
	 */
	private String buildUnitRows(List<Section<? extends Forum>> units, String user) {
		StringBuilder tmp = new StringBuilder();
		boolean noForums = true;
		boolean newEntry = false;

		tmp.append("<tr><th>" + UNIT_LABEL + "</th></tr>");
		tmp.append("\n"); // "10000 characters without linebreak-bug"
		for (Section<DashTreeElement> rootUnit : MenuUtilities.getRootUnits()) {
			newEntry = false;
			String unitName = MenuUtilities.getUnitPagename(rootUnit);

			if (!MenuUtilities.isUnitOpen(rootUnit, user)) {
				tmp.append("<tr class='inactive'><td colspan='2'>" + unitName + "</td></tr>");
			}
			else {
				tmp.append("<tr><td>" + unitName + buildFormButton(unitName) + "<ul>");
				for (Section<? extends Forum> forum : units) {
					int newEntries = DiscussionUtils.getNumberOfNewEntriesInForum(forum, user);
					Map<String, String> mapFor = AbstractXMLType.getAttributes(forum);
					if (unitName.equals(mapFor.get("unit"))) {
						tmp.append("<li><a href='Wiki.jsp?page="
								+ Strings.encodeURL(forum.getTitle())
								+ "'>"
								+ mapFor.get("topic") + "</a>");
						tmp.append(getNewEntriesSpan(newEntries));
						noForums = false;
						if (newEntries != 0) newEntry = true;
					}
				}
				if (noForums) tmp.append("<li>" + NO_FORUMS + "</li>");
				else noForums = true;
				tmp.append("</ul>");
				tmp.append("<br />" + buildForm(unitName));
				if (newEntry) tmp.append("</td><td class='new'></td></tr>");
				else tmp.append("</td><td class='old'></td></tr>");
			}
		}

		return tmp.toString();
	}

	/**
	 * Baue Reihen für Chats.
	 */
	private String buildChatRows(List<Section<? extends Forum>> chats, String userName) {
		StringBuilder tmp = new StringBuilder();
		JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(
				Environment.getInstance().getContext(), null));
		String[] users = wc.getAllUsers();
		String[] activeUsers = wc.getAllActiveUsers();
		List<String> admins = getAdmins();

		tmp.append("<tr><th colspan=2>" + CHATS_LABEL + "</th></tr>");
		for (String user : users) {
			// Filtere Benutzer
			if (admins.contains(user) || user.startsWith("Patient")
					|| user.equals(userName)) continue;

			// Sortiere Namen der Chatteilnehmer alphabetisch
			String[] names = {
					userName, user };
			Arrays.sort(names);
			// User online?
			boolean online = Arrays.asList(activeUsers).contains(user);

			// Gibt es neue Nachrichten im Chat?
			boolean newEntry = false;
			String chatTopic = "Persoenliche Nachrichten(" + names[0] + "," + names[1] + ")";
			Section<? extends Forum> forum = null;
			for (Section<? extends Forum> chat : chats) {
				if (chatTopic.equals(chat.getTitle())) {
					forum = chat;
					break;
				}
			}
			if (forum != null && DiscussionUtils.getNumberOfNewEntriesInForum(forum, userName) > 0) newEntry = true;

			// Baue Zeile für User
			tmp.append(buildChatRow(online, user, names, newEntry));
		}

		return tmp.toString();
	}

	/**
	 * Erstelle HTML-Output für eine Benutzerzeile.
	 */
	private String buildChatRow(boolean online, String user, String[] names, boolean newEntry) {
		StringBuilder tmp = new StringBuilder();
		tmp.append("<tr><td><div class='userchat'>");
		// Avatar
		tmp.append("<img src=\"KnowWEExtension/images/avatars/"
				+ getAvatar(user)
				+ "\" height=\"80px\" width=\"80px\" alt=\"avatar\" />");

		// Username + status
		tmp.append("<span class='userchat'><p>" + user + "</p><a href='"
				+ JSPWikiConnector.LINK_PREFIX
				+ user + "'>"
				+ "Zum Profil</a><br />- " + getStatus(online, user)
				+ " -</span>");

		// "Persönliche Nachricht"-Button
		tmp.append("<input type='button' value='"
				+ PERSONAL_MESSAGE_BUTTON
				+ "' onclick='newChat(\"" + names[0] + "\", \"" + names[1]
				+ "\");return false' />");
		tmp.append("</div>");

		if (newEntry) tmp.append("</td><td class='new'></td></tr>");
		else tmp.append("</td><td class='old'></td></tr>");

		return tmp.toString();
	}

	/**
	 * Baue Reihe für restliche Foren.
	 */
	private String buildOtherRow(List<Section<? extends Forum>> others, String user) {
		StringBuilder tmp = new StringBuilder();
		boolean newEntry = false;
		tmp.append("<tr><th colspan=2>" + OTHERS_LABEL + "</th></tr>");
		tmp.append("<tr><td>");
		tmp.append(buildFormButton("Sonstiges"));
		tmp.append("<ul>");
		for (Section<? extends Forum> forum : others) {
			Map<String, String> mapFor = AbstractXMLType.getAttributes(forum);
			tmp.append("<li><a href='Wiki.jsp?page=" + Strings.encodeURL(forum.getTitle()) + "'>"
					+ mapFor.get("topic") + "</a>");
			int newEntries = DiscussionUtils.getNumberOfNewEntriesInForum(forum, user);
			if (newEntries != 0) newEntry = true;
			tmp.append(getNewEntriesSpan(newEntries));
			tmp.append("</li>");
		}
		if (others.size() == 0) tmp.append("<li>" + NO_FORUMS + "</li>");
		tmp.append("</ul>");
		tmp.append(buildForm("Sonstiges"));

		if (newEntry) tmp.append("</td><td class='new'></td></tr>");
		else tmp.append("</td><td class='old'></td></tr>");

		return tmp.toString();

	}

	/**
	 * Baue Reihe für fehlerhafte Foren.
	 */
	private String buildErrorRow(List<Section<? extends Forum>> errors) {
		StringBuilder tmp = new StringBuilder();
		tmp.append("<tr><th colspan=2>" + ERRORS_LABEL + "</th></tr>");
		tmp.append("<tr><td><ul>");
		for (Section<? extends Forum> forum : errors) {

			tmp.append("<li><a href='Wiki.jsp?page=" + Strings.encodeURL(forum.getTitle()) + "'>"
					+ forum.getTitle() + "</a></li>");
		}
		if (errors.size() == 0) tmp.append("<li>" + NO_FORUMS + "</li>");
		tmp.append("</ul></td><td></td></tr>");

		return tmp.toString();
	}

	/**
	 * Button um ein neues Forum zu erstellen.
	 */
	private String buildFormButton(String unitName) {
		StringBuilder tmp = new StringBuilder();
		tmp.append(" <input type='button' value='"
				+ OPEN_FORM_BUTTON
				+ "' onclick='forumForm(\"" + unitName
				+ "\");return false' />");
		return tmp.toString();
	}

	/**
	 * Formular um ein neues Forum zu eröffnen.
	 */
	private String buildForm(String unitName) {
		StringBuilder tmp = new StringBuilder();
		tmp.append("<form style='display:none' name='" + unitName + "'>");
		tmp.append("<h4>" + FORM_LABEL + "</h4>");
		tmp.append("<p class='topic_label'>" + TOPIC_LABEL + "<br />");
		tmp.append("<input type='text' style='width:" + TOPIC_PANEL_WIDTH + "' name='" + unitName
				+ "_topic' /></p>");
		tmp.append("<p>" + TEXTAREA_LABEL + "<p>");
		tmp.append("<textarea name='" + unitName
				+ "_text' style='width:" + TEXTAREA_WIDTH + ";height:" + TEXTAREA_HEIGHT
				+ "'></textarea>");
		tmp.append("<input type='button' value='" + SEND_BUTTON + "' onclick='sendforumForm(\""
				+ unitName + "\");return false' />");
		tmp.append("<input type='button' onclick='forumForm(\""
				+ unitName
				+ "\");return false' value='" + CLOSE_FORM_BUTTON
				+ "' />");
		tmp.append("</form>");

		return tmp.toString();
	}

	/**
	 * Baue Anzeige der neuen Einträge.
	 */
	private String getNewEntriesSpan(int newEntries) {
		StringBuilder tmp = new StringBuilder();
		if (newEntries == -1) tmp.append("&nbsp;<span class='newEntry'>(ungelesenes Thema)</span>");
		else if (newEntries == 1) tmp.append("&nbsp;<span class='newEntry'>(1 neuer Beitrag)</span>");
		else if (newEntries > 0) tmp.append("&nbsp;<span class='newEntry'>("
				+ newEntries + " neue Beiträge)</span>");
		return tmp.toString();
	}

	/**
	 * Get HTML-Output for a user being online or offline.
	 */
	private String getStatus(boolean online, String user) {

		return online
				? "<span class='online'>online</span>"
				: "<span class='offline'>offline</span>";
	}

	/**
	 * Get all admins.
	 */
	private List<String> getAdmins() {
		List<String> admins = new LinkedList<String>();
		WikiEngine eng = WikiEngine.getInstance(Environment.getInstance().getContext(), null);
		UserDatabase udb = eng.getUserManager().getUserDatabase();
		GroupManager gm = eng.getGroupManager();
		try {
			Principal[] users = udb.getWikiNames();
			for (Principal p : gm.getGroup("Admin").members()) {
				for (Principal u : users) {
					if (u.getName().equals(p.getName())) admins.add(udb.findByWikiName(p.getName()).getFullname());
				}
			}
		}
		catch (NoSuchPrincipalException e) {
		}
		catch (WikiSecurityException e) {
		}

		return admins;
	}

	/**
	 * Get the user's avatar.
	 */
	private String getAvatar(String userName) {
		String avatar = "";

		try {
			Article article = Environment.getInstance().getArticle(
					Environment.DEFAULT_WEB, userName);
			Section<?> s = article.getRootSection();
			Section<AboutMe> sec = Sections.findSuccessor(s, AboutMe.class);
			avatar = DefaultMarkupType.getAnnotation(sec, "avatar");
		}
		catch (NullPointerException e) {
			// e.printStackTrace();
		}
		if (avatar == null || avatar == "") avatar = "1000px-Comic_image_missing.svg.jpg";

		return avatar;
	}

}
