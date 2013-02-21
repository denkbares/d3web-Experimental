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

package de.knowwe.hermes.kdom.event.renderer;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.hermes.HermesUserManagement;
import de.knowwe.hermes.kdom.event.TimeEventNew.ImportanceType;

public class TimeEventRenderer implements Renderer {

	private static TimeEventRenderer instance;

	public static TimeEventRenderer getInstance() {
		if (instance == null) {
			instance = new TimeEventRenderer();
		}
		return instance;
	}

	@Override
	public void render(Section<?> sec, UserContext user,
			RenderResult result) {

		int eventImportanceLevel = -1;
		Section<ImportanceType> childOfType = Sections.findChildOfType(sec, ImportanceType.class);
		if (childOfType != null) {
			eventImportanceLevel = ImportanceType.getImportance(childOfType);
		}

		// check filter Level
		int filterLevel = getFilterLevel(user);
		if (eventImportanceLevel > filterLevel) {
			// do NOT render TimeEvent at all
			return;
		}

		result.appendHtml("%%collapsebox-closed \n<h4> ");

		// change color of title depending on importance
		String style = "color:black";
		if (eventImportanceLevel == 1) {
			style = "color:rgb(255, 0, 102)";
		}
		if (eventImportanceLevel == 2) {
			style = "color:rgb(235, 235, 20)";
		}
		if (eventImportanceLevel == 3) {
			style = "color:rgb(20, 200, 102)";
		}
		// closes
		// in
		// TimeEventTitleRenderer

		result.appendHtml("<span style='" + style + "'>");
		DelegateRenderer.getInstance().render(sec, user, result);
		result.append("/%\n");
	}

	private int getFilterLevel(UserContext user) {
		Integer impFilterLevel = HermesUserManagement.getInstance()
				.getEventFilterLevelForUser(user.getUserName());
		int filterLevel = 3;
		if (impFilterLevel != null) {
			filterLevel = impFilterLevel.intValue();
		}
		return filterLevel;
	}

}
