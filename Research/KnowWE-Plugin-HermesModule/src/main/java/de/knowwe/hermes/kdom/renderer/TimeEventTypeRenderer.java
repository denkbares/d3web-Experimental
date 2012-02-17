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

package de.knowwe.hermes.kdom.renderer;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWERenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.hermes.HermesUserManagement;
import de.knowwe.hermes.TimeStamp;
import de.knowwe.hermes.kdom.TimeEventDateType;
import de.knowwe.hermes.kdom.TimeEventDescriptionType;
import de.knowwe.hermes.kdom.TimeEventImportanceType;
import de.knowwe.hermes.kdom.TimeEventSourceType;
import de.knowwe.hermes.kdom.TimeEventTitleType;
import de.knowwe.hermes.kdom.TimeEventType;

public class TimeEventTypeRenderer implements KnowWERenderer<TimeEventType> {

	private static TimeEventTypeRenderer instance;

	public static TimeEventTypeRenderer getInstance() {
		if (instance == null) {
			instance = new TimeEventTypeRenderer();
		}
		return instance;
	}

	@Override
	public void render(Section<TimeEventType> sec, UserContext user, StringBuilder result) {

		// check filter Level
		int filterLevel = getFilterLevel(user);
		int eventImportanceLevel = getImportanceOfEvent(sec);

		if (eventImportanceLevel > filterLevel) {
			// do NOT render TimeEvent at all
			return;
		}

		Section<? extends TimeEventTitleType> titleSection = Sections.findChildOfType(sec,
				TimeEventTitleType.class);
		String title = "no title found";
		if (titleSection != null) title = titleSection.getText();

		String date = TimeStamp.decode(getDateString(sec));

		Section<? extends TimeEventDescriptionType> descriptionSection = Sections
				.findChildOfType(sec, TimeEventDescriptionType.class);
		// String description = "no description found";
		// if (descriptionSection != null)
		// description = descriptionSection.getOriginalText();

		List<Section<TimeEventSourceType>> sources = new ArrayList<Section<TimeEventSourceType>>();
		Sections.findSuccessorsOfType(sec, TimeEventSourceType.class, sources);

		// StringBuilder result = new StringBuilder();

		String titleHeader = "";
		String style = "color:rgb(20, 200, 102)";
		if (eventImportanceLevel == 1) {
			style = "color:rgb(255, 0, 102)";
		}
		if (eventImportanceLevel == 2) {
			style = "color:rgb(235, 235, 20)";
		}

		title = KnowWEUtils.maskHTML("<span style='" + style + "'>") + title
				+ KnowWEUtils.maskHTML("</span>");
		titleHeader = title + "   :   ";
		titleHeader += date;
		// titleHeader += " " + importance;
		result.append("%%collapsebox-closed \n");

		result.append("! " + titleHeader + " \n");

		DelegateRenderer.getInstance().render(descriptionSection, user, result);

		if (sources.size() > 0) {
			result.append("\\\\__Quellen:__\n\n");
			for (Section<TimeEventSourceType> section : sources) {
				String text = section.getText();
				String key = "QUELLE:";
				if (text.startsWith(key)) {
					text = text.substring(key.length());
				}
				result.append(text + "\\\\");
			}
		}
		result.append("/%\n");

		// return result.toString();
	}

	private String getDateString(Section<TimeEventType> sec) {
		Section<? extends TimeEventDateType> dateSection = Sections.findChildOfType(sec,
				TimeEventDateType.class);
		String date = "no date found";
		if (dateSection != null) date = dateSection.getText();
		if (date.startsWith("\r\n")) date = date.substring(2);
		return date;
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

	private int getImportanceOfEvent(Section<TimeEventType> sec) {
		Section<? extends TimeEventImportanceType> importanceSection = Sections
				.findChildOfType(sec, TimeEventImportanceType.class);
		String importance = "no importance found";
		if (importanceSection != null) {
			importance = importanceSection.getText();
		}

		try {
			String digit = importance.substring(importance.indexOf('(') + 1,
					importance.indexOf(')')).trim();
			int eventLevel = Integer.parseInt(digit);
			return eventLevel;
		}
		catch (Exception e) {
			return -1;
		}
	}

}
