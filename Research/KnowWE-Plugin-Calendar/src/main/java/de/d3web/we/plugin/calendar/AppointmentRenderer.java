/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.we.plugin.calendar;

import java.util.Map;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

public class AppointmentRenderer extends KnowWEDomRenderer<Appointment> {

	@Override
	public void render(KnowWEArticle article, Section<Appointment> sec, UserContext user, StringBuilder string) {

		Map<String, String> persons = CalendarModule.getPersons();

		Section<?> dateSec = Sections.findChildOfType(Sections.findChildOfType(sec,
				AppointmentStartSymbol.class), AppointmentDate.class);
		Section<?> timeSec = Sections.findChildOfType(Sections.findChildOfType(sec,
				AppointmentStartSymbol.class), AppointmentTime.class);
		Section<?> textSec = Sections.findChildOfType(sec, AppointmentText.class);
		Section<?> nameSec = Sections.findChildOfType(sec, AppointmentAuthor.class);

		String name = "";
		String text = textSec.getOriginalText();

		// TODO Error messages for dateSec, timeSec = null ??

		if (nameSec == null) {

			name = "{person not found}";

		}
		else {

			name = persons.get(nameSec.getOriginalText());
		}

		StringBuilder b = new StringBuilder();
		b.append("<table class=wikitable width=95% border=0>\n");
		b.append("<tr><th align=left width=80>" + dateSec.getOriginalText()
				+ "</th><th align=left>" + timeSec.getOriginalText()
				+ "</th><th align=right width=250><i>" + name + "</i></th></tr>\n");
		b.append("<tr><td colspan=3>" + text + "</td></tr>\n</table>\n");

		string.append(KnowWEUtils.maskHTML(b.toString()));

	}
}
