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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.plugin.forum.Forum;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.core.dashtree.DashTreeElement;
import de.knowwe.core.dashtree.DashTreeUtils;
import de.knowwe.defi.menu.DynamicMenuMarkup;
import de.knowwe.defi.time.TimeTableMarkup;
/**
 * 
 * @author dupke
 * @created 03.08.2011
 */
public class ForumMenuTagHandler extends AbstractTagHandler {

	// TODO: Usernames Mit Berater sprechen individuell anzeigen oder überlegen

	private static final String OPEN_FORM_BUTTON = "Neues Forum";
	private static final String CLOSE_FORM_BUTTON = "Schließen";
	private static final String SHOW_FORUMS_BUTTON = "Foren anzeigen";
	private static final String SEND_BUTTON = "Abschicken";

	/**
	 * @param name
	 */
	public ForumMenuTagHandler() {
		super("forummenu");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		StringBuilder fm = new StringBuilder();
		Iterator<KnowWEArticle> it = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB).getArticleIterator();
		List<Section<? extends Forum>> forums = new LinkedList<Section<? extends Forum>>();
		List<Section<? extends Forum>> other = new LinkedList<Section<? extends Forum>>();
		String pageName, unit, topic;
		boolean noForum = true;

		// Hole Lektionen aus dem Left Menu
		List<Section<DashTreeElement>> rootUnits = getAllRootUnits();
		List<Section<DashTreeElement>> units = getAllUnits();

		fm.append("<h3>Foren-Übersicht</h3>");

		// Hole alle Foren
		while (it.hasNext()) {
			for (Section<? extends Type> sec : it.next().getAllNodesPreOrder()) {
				if (Sections.findSuccessor(sec, Forum.class) != null
						&& !forums.contains(Sections.findSuccessor(sec, Forum.class))
						&& !Sections.findSuccessor(sec, Forum.class).getTitle().endsWith(
								"comment_therapist"))
					forums.add(Sections.findSuccessor(sec, Forum.class));
			}

		}
		other.addAll(forums);

		/* Foren zu Einheiten */
		// <tr>-Klassen: inactive - active - new
		fm.append("<table class='forumMenu'>");
		for (Section<DashTreeElement> rootUnit : rootUnits) {
			pageName = getPageName(rootUnit);
			fm.append("\n"); // fixes JSPWiki's
								// "10000 characters without linebreak-bug"

			if (isFree(rootUnit)) {
				fm.append("<tr class='active'><td class='fm_status'></td><td>" + pageName);

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
						
							fm.append("<li><a href='Wiki.jsp?page=" + sec.getTitle() + "'>"
								+ topic + " (" + unit + ")"
								+ "</a></li>");
						other.remove(sec);
						noForum = false;
					}
				}
				if (noForum) fm.append("<li>Noch keine Foren vorhanden</p></li>");
				noForum = true;
				fm.append("</ul></div>");

				// Formular zur Erstellung eines neuen Forums
				fm.append("<br /><form style='display:none' name='" + pageName
							+ "'><br /><h4>Neues Forum erstellen:</h4>");

				// ++ Select-Tag zur Lektionsauswahl
				fm.append("<p class='fm_newforum'>Thema:&nbsp;<select name='" + pageName
						+ "_select'>");
				fm.append("<option value='" + pageName + "'>" + pageName + "</option>");

				for (Section<DashTreeElement> unitSec : units) {
					if (DashTreeUtils.getFatherDashTreeElement(unitSec) != null
							&& DashTreeUtils.getFatherDashTreeElement(unitSec).equals(rootUnit)) fm.append("<option value='"
							+ pageName
							+ ": "
							+ getPageName(unitSec)
							+ "'>"
							+ pageName
							+ ": "
							+ getPageName(unitSec) + "</option>");
				}

				fm.append("</select></p>");

				// ++ Ueberschrift
				fm.append("<p class='fm_newforum'>&Uuml;berschrift:&nbsp<input type='text' size='50' name='"
						+ pageName
						+ "_topic' /></p>");

				// ++ Nachrichteneingabe
				fm.append("<p>Beitrag:</p><textarea name='" + pageName
							+ "_text' rows='10' cols='50'></textarea><br />");
				fm.append("<input type='button' value='" + SEND_BUTTON +
							"' onclick='sendforumForm(\""
							+ pageName +
							"\");return false' /><input type='button' onclick='forumForm(\""
							+ pageName
							+ "\");return false' value='" + CLOSE_FORM_BUTTON + "' /></form>");

				fm.append("</td></tr>");
			}
			else {
				fm.append("<tr class='inactive'><td colspan='2'>" + pageName
						+ "</td></tr>");
			}
		}

		/* Sonstige Foren */
		pageName = "Sonstiges";
		fm.append("<tr class='active'><td class='fm_status'></td><td>" + pageName);

		// "Neues Forum"-Button
		fm.append(" <input class='fm_open' type='button' value='"
				+ OPEN_FORM_BUTTON
				+ "' onclick='forumForm(\"" + pageName
				+ "\");return false' />");

		// Auflistung der Foren
		fm.append("<div name='" + pageName + "_forums'><ul>");
		for (Section<? extends Forum> sec : other) {
			// Hole Attribute aus Forum-XML
			topic = getForumAttribute("topic", sec.getText());
			if (topic == "") topic = sec.getTitle();

			fm.append("<li><a href='Wiki.jsp?page=" + sec.getTitle() + "'>"
					+ topic + "</a></li>");
			noForum = false;
		}
		if (noForum) fm.append("<li>Noch keine Foren vorhanden</p></li>");
		noForum = true;
		fm.append("</ul></div>");

		// Formular zur Erstellung eines neuen Forums
		fm.append("<br /><form style='display:none' name='" + pageName
					+ "'><br /><h4>Neues Forum erstellen:</h4>");

		// ++ Ueberschrift
		fm.append("<p class='fm_newforum'>&Uuml;berschrift:&nbsp<input type='text' size='50' name='"
				+ pageName
				+ "_topic' /></p>");

		// ++ Nachrichteneingabe
		fm.append("<p>Beitrag:</p><textarea name='" + pageName
					+ "_text' rows='10' cols='50'></textarea><br />");
		fm.append("<input type='button' value='" + SEND_BUTTON +
					"' onclick='sendforumForm(\""
					+ pageName +
					"\");return false' /><input type='button' onclick='forumForm(\""
					+ pageName
					+ "\");return false' value='" + CLOSE_FORM_BUTTON + "' /></form>");

		fm.append("</td></tr></table>");

		return KnowWEUtils.maskHTML(fm.toString());
	}

	/**
	 * 
	 */
	private List<Section<DashTreeElement>> getAllUnits() {
		List<Section<DashTreeElement>> units = new LinkedList<Section<DashTreeElement>>();
		KnowWEArticle leftMenu = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB).getArticle("LeftMenu");

		if (leftMenu != null) {
			Section<DynamicMenuMarkup> menu = Sections.findSuccessor(leftMenu.getSection(),
					DynamicMenuMarkup.class);
			Sections.findSuccessorsOfType(menu, DashTreeElement.class, units);
		}

		return units;
	}

	/**
	 * 
	 */
	private List<Section<DashTreeElement>> getAllRootUnits() {
		List<Section<DashTreeElement>> units = new LinkedList<Section<DashTreeElement>>();
		List<Section<DashTreeElement>> rootUnits = new LinkedList<Section<DashTreeElement>>();
		KnowWEArticle leftMenu = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB).getArticle("LeftMenu");

		if (leftMenu != null) {
			Section<DynamicMenuMarkup> menu = Sections.findSuccessor(leftMenu.getSection(),
					DynamicMenuMarkup.class);
			Sections.findSuccessorsOfType(menu, DashTreeElement.class, units);
		}

		for (Section<DashTreeElement> unit : units) {
			if (DashTreeUtils.getDashLevel(unit) == 0) rootUnits.add(unit);
		}

		return rootUnits;
	}

	/**
	 * 
	 */
	private static String getPageName(Section<DashTreeElement> sec) {
		String pagename = sec.getOriginalText().trim();
		if (DashTreeUtils.getDashLevel(sec) == 1) {
			pagename = pagename.substring(2);
		}
		else if (DashTreeUtils.getDashLevel(sec) == 2) {
			pagename = pagename.substring(3);
		}
		if (sec.getOriginalText().contains("|")) {
			String[] split = sec.getOriginalText().split("\\|");
			pagename = split[1].trim();
		}

		return pagename;
	}

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

		KnowWEArticle zeitplanArticle = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB).getArticle(
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

}
