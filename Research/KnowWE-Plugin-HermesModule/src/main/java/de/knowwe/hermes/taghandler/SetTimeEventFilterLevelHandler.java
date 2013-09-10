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
import de.knowwe.hermes.HermesUserManagement;

public class SetTimeEventFilterLevelHandler extends AbstractHTMLTagHandler {

	public SetTimeEventFilterLevelHandler() {
		super("filterEventLevel");
	}

	@Override
	public void renderHTML(String web, String topic,
			UserContext user, Map<String, String> values, RenderResult result) {

		Integer currentLevel = HermesUserManagement.getInstance().getEventFilterLevelForUser(
				user.getUserName());
		int currInt = -1;
		if (currentLevel != null) {
			currInt = currentLevel.intValue();
		}

		StringBuffer buffy = new StringBuffer();

		buffy.append("Ereignisse anzeigen ab Stufe:");

		buffy.append("<br>");

		// quick & dirty
		wrappBoldIf("<a href=\"\" onclick=\"sendFilterLevel('0','" + user.getUserName()
				+ "')\">  0  </a>", buffy, currInt, 0);

		wrappBoldIf("<a href=\"\" onclick=\"sendFilterLevel('1','" + user.getUserName()
				+ "')\"> 1  </a>", buffy, currInt, 1);

		wrappBoldIf("<a href=\"\" onclick=\"sendFilterLevel('2','" + user.getUserName()
				+ "')\"> 2  </a>", buffy, currInt, 2);

		wrappBoldIf("<a href=\"\" onclick=\"sendFilterLevel('3','" + user.getUserName()
				+ "')\"> 3  </a>", buffy, currInt, 3);

		result.appendHtml(buffy.toString());
	}

	private void wrappBoldIf(String string, StringBuffer buffy, int currInt,
			int i) {
		if (currInt == i) {
			buffy.append("<b>");
		}
		buffy.append(string);
		if (currInt == i) {
			buffy.append("</b>");
		}

	}

}
