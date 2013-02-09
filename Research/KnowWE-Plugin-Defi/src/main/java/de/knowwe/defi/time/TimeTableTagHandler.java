/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.time;

import java.util.List;
import java.util.Map;

import com.ecyrd.jspwiki.WikiEngine;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.defi.menu.MenuUtilities;
import de.knowwe.jspwiki.JSPWikiConnector;
import de.knowwe.kdom.dashtree.DashTreeElement;


/**
 * 
 * @author dupke
 * @created 06.02.2013
 */
public class TimeTableTagHandler extends AbstractTagHandler {

	/**
	 * @param name
	 */
	public TimeTableTagHandler() {
		super("timetable");
	}

	@Override
	public String render(Section<?> section, UserContext userContext, Map<String, String> parameters) {
		if (!userContext.userIsAdmin()) return ""; // only admin may see this
		StringBuilder timetable = new StringBuilder();
		List<Section<DashTreeElement>> units = MenuUtilities.getRootUnits();
		JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(
				Environment.getInstance().getContext(), null));
		String[] users = wc.getAllUsers();

		// get timetable template
		List<Integer> timeTableTemplate = TimeTableUtilities.getTimeTableTemplate();

		// timetable for all users
		timetable.append("<div id='timetable'>");
		timetable.append("<h3>Allgemeiner Zeitplan</h3><hr />");
		timetable.append("<form name='timetable_all'><table>");
		timetable.append("<tr><th>Einheit</th><th>Beginn nach Erstellung (in Tagen)</th></tr>");
		for (int i = 0; i < units.size(); i++) {
			timetable.append("<tr><td>" + MenuUtilities.getUnitPagename(units.get(i))
					+ "</td><td><input type='text' size='10' value='" + timeTableTemplate.get(i)
					+ "'/></td></tr>");
		}
		timetable.append("</table>");
		timetable.append("<input type='button' onClick='changeTimeTable()' value='Bestätigen' /></form>");
		timetable.append("<br /><br />");

		// timetable for single user
		timetable.append("<h3>Individueller Zeitplan</h3><hr />");
		timetable.append("<p>Benutzer: <select name='users'>");
		for (String user : users) {
			timetable.append("<option>" + user + "</option>");
		}
		timetable.append("</select></p>");
		timetable.append("<form name='timetable_single'><table>");
		timetable.append("<tr><th>Einheit</th><th>Beginn (Datum)</th></tr>");
		for (Section<DashTreeElement> unit : units) {
			timetable.append("<tr><td>" + MenuUtilities.getUnitPagename(unit)
					+ "</td><td><input type='text' size='10' /></td></tr>");
		}
		timetable.append("</table>");
		timetable.append("<input type='button' onclick='alert(\"Hallo\");' value='Bestätigen' /></form>");
		timetable.append("</div>");

		return Strings.maskHTML(timetable.toString());
	}

}
