/*
 * Copyright (C) 2014 University Wuerzburg, Computer Science VI
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.defi.menu.MenuUtilities;
import de.knowwe.kdom.dashtree.DashTreeElement;


/**
 * 
 * @author dupke
 * @created 09.03.2014
 */
public class PersonalTimeTableTagHandler extends AbstractTagHandler {

	public PersonalTimeTableTagHandler() {
		super("personaltimetable");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		List<Date> dates = TimeTableUtilities.getPersonalTimeTable(userContext.getUserName());
		List<Section<DashTreeElement>> rootUnits = MenuUtilities.getRootUnits();

		if (dates.size() != rootUnits.size()) throw new IllegalStateException(
				"Die Größe des Zeitplans unterscheidet sich von der Anzahl der Einheiten");

		StringBuilder timetable = new StringBuilder();
		timetable.append("<ul>");
		for (int i = 0; i < dates.size(); i++) {
			timetable.append("<li>" + MenuUtilities.getUnitPagename(rootUnits.get(i))
					+ " wird freigeschaltet am " + sdf.format(dates.get(i)) + ".</li>");
		}
		timetable.append("</ul>");

		result.appendHtml(timetable.toString());
	}

}
