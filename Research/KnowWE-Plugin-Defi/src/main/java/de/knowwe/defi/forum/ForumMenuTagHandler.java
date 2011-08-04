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
package de.knowwe.defi.forum;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.core.dashtree.DashTreeElement;
import de.knowwe.core.dashtree.DashTreeUtils;
import de.knowwe.defi.menu.DynamicMenuMarkup;

/**
 * 
 * @author dupke
 * @created 03.08.2011
 */
public class ForumMenuTagHandler extends AbstractTagHandler {
	// TODO: Usernames Mit Berater sprechen individuell anzeigen oder überlegen

	private static final String OPEN_FORM_BUTTON = "Neues Forum";
	private static final String CLOSE_FORM_BUTTON = "Schließen";
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
		List<String> forums = new LinkedList<String>();
		List<String> other = new LinkedList<String>();

		// Hole Lektionen aus dem Left Menu
		List<String> units = getALlUnits();

		fm.append("<h3>Foren-Übersicht</h3>");

		while (it.hasNext()) {
			for (Section<? extends Type> sec : it.next().getAllNodesPreOrder()) {
				if (sec.getText().contains("<forum")
						&& !sec.getTitle().endsWith("comment_therapist")
						&& !forums.contains(sec.getTitle())) {
					forums.add(sec.getTitle());
				}
			}
		}

		other.addAll(forums);

		fm.append("<ul>");
		for (String s : units) {
			if (s.startsWith("###")) {
				s = s.substring(3);
				fm.append("<li class='root'><span>" + s + "</span>");
			}
			else fm.append("<li class='subUnit'><span>" + s + "</span>");
			fm.append(" <input style='margin-left:10px' type='button' value='" + OPEN_FORM_BUTTON
					+ "' onclick='forumForm(\"" + s
					+ "\");return false' />");

			for (String f : forums) {
				if (f.split("_forum")[0].equals(s)) {
					fm.append("<ul><li><a href='Wiki.jsp?page=" + f + "'>" + f + "</a></li></ul>");
					other.remove(f);
				}
			}
			fm.append("</li><br />");
			fm.append("<form style='display:none' name='" + s
					+ "'><textarea name='" + s
					+ "_text' rows='10' cols='50'></textarea><br />");
			fm.append("<input type='button' value='" + SEND_BUTTON + "' onclick='sendforumForm(\""
					+ s + "\");return false' /><input type='button' onclick='forumForm(\""
					+ s
					+ "\");return false' value='" + CLOSE_FORM_BUTTON + "' /></form>");
		}
		
		fm.append("<li class='root'><span>Sonstiges</span>");
		for (String s : other) {
			fm.append("<ul><li><a href='Wiki.jsp?page=" + s + "'>" + s + "</a></li></ul>");
		}
		fm.append("</ul>");

		return KnowWEUtils.maskHTML(fm.toString());
	}

	/**
	 * 
	 */
	private List<String> getALlUnits() {
		List<Section<DashTreeElement>> units = new LinkedList<Section<DashTreeElement>>();
		List<String> pageNames = new LinkedList<String>();
		KnowWEArticle leftMenu = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB).getArticle("LeftMenu");

		if (leftMenu != null) {
			Section<DynamicMenuMarkup> menu = Sections.findSuccessor(leftMenu.getSection(),
					DynamicMenuMarkup.class);
			Sections.findSuccessorsOfType(menu, DashTreeElement.class, units);
		}

		for (Section<DashTreeElement> unit : units) {
			if (DashTreeUtils.getDashLevel(unit) == 0) pageNames.add("###" + getPageName(unit));
			else pageNames.add(getPageName(unit));
		}

		return pageNames;
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

}
