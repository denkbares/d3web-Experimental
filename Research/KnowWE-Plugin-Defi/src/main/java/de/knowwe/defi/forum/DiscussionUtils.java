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
package de.knowwe.defi.forum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.knowwe.comment.forum.Forum;
import de.knowwe.comment.forum.ForumBox;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.defi.logger.DefiPageEventLogger;
import de.knowwe.defi.logger.DefiPageLogLine;
import de.knowwe.kdom.xml.AbstractXMLType;

/**
 * 
 * @author dupke
 * @created 18.07.2013
 */
public class DiscussionUtils {

	/**
	 * Ermittelt die Anzahl der neuen Beiträge seit dem letzten Besuch.
	 */
	public static int getNumberOfNewEntries(Section<? extends Forum> forum, String user) {
		List<Section<ForumBox>> boxes = Sections.findSuccessorsOfType(forum, ForumBox.class);
		HashMap<String, String> logPages = new HashMap<String, String>();

		for (DefiPageLogLine logLine : DefiPageEventLogger.getLogLines()) {
			String lineUser = logLine.getUser();
			String title = logLine.getPage();
			String date = logLine.getStartDate() + " " + logLine.getstartTime();
			if (lineUser.equals(user)) logPages.put(title, date);
		}

		String lastVisit = logPages.get(forum.getTitle());
		int numberOfNewEntries = 0;

		if (lastVisit == null) return -1;
		Date lastVisitDate = stringToDate(lastVisit);
		for (Section<ForumBox> box : boxes) {
			Map<String, String> boxMap = AbstractXMLType.getAttributeMapFor(box);
			Date boxDate = stringToDate(boxMap.get("date"));
			if (boxDate.after(lastVisitDate)) numberOfNewEntries++;

		}

		return numberOfNewEntries;
	}

	/**
	 * String into Date.
	 */
	public static Date stringToDate(String dateString) {
		Date date = null;
		LinkedList<SimpleDateFormat> formats = new LinkedList<SimpleDateFormat>();
		formats.add(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));
		formats.add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"));
		formats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		formats.add(new SimpleDateFormat("dd.MM.yyyy HH:mm"));

		for (SimpleDateFormat sdf : formats) {
			try {
				date = sdf.parse(dateString);
			}
			catch (ParseException e) {
				continue;
			}

			break;
		}

		return date;
	}
}
