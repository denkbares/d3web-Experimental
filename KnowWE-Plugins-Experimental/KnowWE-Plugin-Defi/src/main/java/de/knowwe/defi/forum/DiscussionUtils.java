/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import de.knowwe.comment.forum.Forum;
import de.knowwe.comment.forum.ForumBox;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.defi.logger.DefiPageEventLogger;
import de.knowwe.defi.logger.DefiPageLogLine;
import de.knowwe.kdom.xml.AbstractXMLType;

/**
 * Some useful methods for the discussion plugin and forums.
 * 
 * @author dupke
 */
public class DiscussionUtils {

	/**
	 * Get all forums.
	 */
	public static List<Section<? extends Forum>> getAllForums() {
		List<Section<? extends Forum>> forums = new LinkedList<Section<? extends Forum>>();
		Iterator<Article> it = KnowWEUtils.getArticleManager(Environment.DEFAULT_WEB).getArticles().iterator();

		while (it.hasNext()) {
			for (Section<? extends Type> sec : Sections.successors(it.next().getRootSection())) {
				Section<? extends Forum> forum = Sections.successor(sec, Forum.class);
				if (forum != null && !forums.contains(forum)) forums.add(forum);
			}

		}

		return forums;
	}

	/**
	 * Get number of new entries for a forum.
	 */
	public static int getNumberOfNewEntriesInForum(Section<? extends Forum> forum, String user) {
		List<Section<ForumBox>> boxes = Sections.successors(forum, ForumBox.class);
		HashMap<String, String> logPages = new HashMap<String, String>();

		for (DefiPageLogLine logLine : DefiPageEventLogger.getLogLines()) {
			String lineUser = logLine.getUser();
			String title = logLine.getPage();
			String date = logLine.getStartDate() + " " + logLine.getstartTime();
			if (lineUser.equals(user)) logPages.put(title, date);
		}

		String lastVisit = logPages.get(forum.getTitle());
		int numberOfNewEntries = 0;
		Date lastVisitDate = (lastVisit == null) ? new Date(0L) : stringToDate(lastVisit);
		// skip first box if its a chat
		Map<String, String> mapFor = AbstractXMLType.getAttributes(forum);
		int start = mapFor.get("unit").equals("chat") ? 1 : 0;
		for (int i = start; i < boxes.size(); i++) {
			Section<ForumBox> box = boxes.get(i);
			Map<String, String> boxMap = AbstractXMLType.getAttributes(box);
			Date boxDate = stringToDate(boxMap.get("date"));
			if (!boxMap.get("name").equals(user) && boxDate.after(lastVisitDate)) numberOfNewEntries++;
		}

		return numberOfNewEntries;
	}

	/**
	 * Checks whether the user has a new message from the therapist.
	 */
	public static Boolean userHasNewTherapistMessage(String user) {

		for (Section<? extends Forum> forum : getAllForums()) {
			Map<String, String> mapFor = AbstractXMLType.getAttributes(forum);
			if (!mapFor.get("unit").equals("chat")) continue;

			try {
				String berater = ResourceBundle.getBundle("KnowWE_Defi_config").getString(
						"defi.berater");
				if (!mapFor.get("user1").equals(berater) && !mapFor.get("user2").equals(berater)) continue;

				if ((mapFor.get("user1").equals(user) || mapFor.get("user2").equals(user))
						&& getNumberOfNewEntriesInForum(forum, user) > 0) return true;
			}
			catch (NullPointerException e) {
				// old chats don't have the user attribute
			}
		}

		return false;
	}

	/**
	 * Checks whether the user has a new message from another user.
	 */
	public static Boolean userHasNewUserMessage(String user) {

		for (Section<? extends Forum> forum : getAllForums()) {
			Map<String, String> mapFor = AbstractXMLType.getAttributes(forum);
			if (!mapFor.get("unit").equals("chat")) continue;

			try {
				String berater = ResourceBundle.getBundle("KnowWE_Defi_config").getString(
						"defi.berater");
				if (!user.equals(berater)
						&& (mapFor.get("user1").equals(berater) || mapFor.get("user2").equals(
								berater))) continue;

				if ((mapFor.get("user1").equals(user) || mapFor.get("user2").equals(user))
						&& getNumberOfNewEntriesInForum(forum, user) > 0)
					return true;
			}
			catch (NullPointerException e) {
				// old chats don't have the user attribute
			}
		}

		return false;
	}

	/**
	 * String to Date.
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
