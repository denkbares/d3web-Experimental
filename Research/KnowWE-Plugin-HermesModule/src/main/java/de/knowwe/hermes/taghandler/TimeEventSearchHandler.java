/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.hermes.taghandler;

import java.util.Map;

import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;

public class TimeEventSearchHandler extends AbstractHTMLTagHandler {

	public TimeEventSearchHandler() {
		super("timeEventSearch");
	}

	@Override
	public void renderHTML(String web, String topic,
			UserContext user, Map<String, String> values, RenderResult result) {

		int startIndex = 1;
		int noEntries = 20;
		String startTimeString = "-10000";
		String endTimeString = "2008";
		String header = "Suche nach Ereignissen";
		String s = "";

		s += "<div class=\"panel\"><h3>" + header + "</h3>";
		s += "<form action=\"Wiki.jsp?page="
				+ topic
				+ "\" name=\"testform\" accept-charset=\"UTF-8\" method=\"post\" enctype=\"application/x-www-form-urlencoded\">"
				+
				"<input type=\"hidden\" name=\"formname\" value=\"testform\"/>";
		s += "<p>Start Index: <input id='startIndexTimeline' type='text' value='" + startIndex
				+ "'/> " +
				"Anzahl: <input id='hermesSearchResultCount' type='text' value='"
				+ noEntries + "'/></p>";
		s += "<p>Von: <input id='hermesSearchFrom'  type='text' value='" + startTimeString + "'/> "
				+
				"Bis: <input id='hermesSearchTo'  type='text' value='" + endTimeString + "'/></p>";
		s += "<p><input onclick='sendTimeEventSearchRequest()' type='button' value='Anzeigen'/></p>";
		s += "</form>";

		s += "<p><input onclick='sendTimeEventBackSearchRequest()' type='button' value='vorherige Seite'/>";
		s += "<input onclick='sendTimeEventNextSearchRequest()' type='button' value='nächste Seite'/></p>";

		s += "</div>";
		s += "<div id='hermesSearchResult'></div>";

		s += "<div><p><input onclick='sendTimeEventBackSearchRequest()' type='button' value='vorherige Seite'/>";
		s += "<input onclick='sendTimeEventNextSearchRequest()' type='button' value='nächste Seite'/></p></div>";
		result.appendHtml(s);
	}

}
