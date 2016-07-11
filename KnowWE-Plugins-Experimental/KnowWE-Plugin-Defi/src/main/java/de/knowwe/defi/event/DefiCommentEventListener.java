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
package de.knowwe.defi.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.mail.MessagingException;

import de.d3web.strings.Strings;
import de.d3web.we.event.NewCommentEvent;
import de.knowwe.comment.forum.Forum;
import de.knowwe.comment.forum.ForumBox;
import de.knowwe.core.Environment;
import com.denkbares.events.Event;
import com.denkbares.events.EventListener;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.defi.logger.DefiCommentEventLogger;
import de.knowwe.defi.logger.DefiCommentLogLine;
import de.knowwe.defi.mailform.MailUtils;
import de.knowwe.kdom.xml.AbstractXMLType;


/**
 * 
 * @author dupke
 * @created 31.07.2013
 */
public class DefiCommentEventListener implements EventListener {

	private static final String THERAPIST = "Berater";
	private static final String NO_ADRESSEE = "offenes Forum";
	private static final String NO_RESPONSE = "Startbeitrag";

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		ArrayList<Class<? extends Event>> events = new ArrayList<>(1);
		events.add(NewCommentEvent.class);

		return events;
	}

	@Override
	public void notify(Event event) {
		final NewCommentEvent comEvent = (NewCommentEvent) event;
		DefiCommentLogLine commentLogLine = getLogLineOfForum(comEvent);
		DefiCommentEventLogger.logComment(commentLogLine);
		notifyMail(comEvent.getTopic());
	}

	private DefiCommentLogLine getLogLineOfForum(NewCommentEvent event) {
		DefiCommentLogLine logLine = new DefiCommentLogLine();
		Section<? extends Forum> forum = getForum(event.getTopic());
		List<Section<ForumBox>> boxes = Sections.successors(forum, ForumBox.class);
		Section<ForumBox> lastBox = null, previousBox = null;
		for (Section<ForumBox> box : boxes) {
			if (lastBox != null) previousBox = lastBox;
			lastBox = box;
		}

		// Nachricht
		logLine.setMsg(event.getComment());

		// Einheit
		Map<String, String> mapForForum = AbstractXMLType.getAttributes(forum);
		logLine.setUnit(mapForForum.get("unit"));
		
		// Absender (Benutzer)
		Map<String, String> lastBoxMap = AbstractXMLType.getAttributes(lastBox);
		logLine.setUser(lastBoxMap.get("name"));

		// Vorgänger
		if (previousBox == null || (logLine.getUnit().equals("chat") && boxes.size() == 2)) logLine.setResponse(NO_RESPONSE);
		else {
			Map<String, String> prevBoxMap = AbstractXMLType.getAttributes(previousBox);
			logLine.setResponse(prevBoxMap.get("name"));
		}

		// Adressat
		String adressee;
		adressee = getAdressee(logLine.getUser(), logLine.getUnit(), mapForForum);
		logLine.setAdressee(adressee);

		// Zeit und Datum
		String date = lastBoxMap.get("date");
		logLine.setDate(date.split(" ")[0]);
		logLine.setTime(date.split(" ")[1]);

		// Topic
		logLine.setTopic(event.getTopic());

		return logLine;
	}

	/**
	 * 
	 */
	private String getAdressee(String user, String unit, Map<String, String> mapForForum) {
		String adressee;
		if (unit.equals("chat")) {
			String berater = ResourceBundle.getBundle("KnowWE_Defi_config").getString(
					"defi.berater");
			String chatUser1 = mapForForum.get("user1");
			String chatUser2 = mapForForum.get("user2");
			if (!user.equals(berater) && (chatUser1.equals(berater) || chatUser2.equals(berater))) {
				adressee = THERAPIST;
			}
			else {
				adressee = (user.equals(chatUser1)) ? chatUser2 : chatUser1;
			}
		}
		else {
			adressee = NO_ADRESSEE;
		}
		return adressee;
	}

	/**
	 * 
	 */
	private Section<Forum> getForum(String page) {
		Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB, page);
		if (article == null) {
			article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
					Strings.decodeURL(page));
		}
		return Sections.successor(article.getRootSection(),
				Forum.class);
	}

	/**
	 * Creates the notification mail content and sends the email to the user
	 * specified in the projects properties file.
	 * 
	 * @created 08.03.2011
	 * @param topic
	 * @return boolean
	 */
	private boolean notifyMail(String topic) {
		ResourceBundle rb = ResourceBundle.getBundle("KnowWE_Defi_config");
		String subject = rb.getString("defi.fmail.subject");
		String message = rb.getString("defi.fmail.message");

		// replace @page@
		message = message.replaceAll("@page@", topic);

		// replace @ink@
		String link = Environment.getInstance().getWikiConnector().getBaseUrl();
		link += "/Wiki.jsp?page=" + topic;
		link = link.replaceAll(" ", "%20");
		message = message.replaceAll("@link@", link);

		// don't send mails in dev-mode
		if (rb.getString("defi.dev").equals("false")) {
			try {
				String mailTo = rb.getString("defi.mail.to");
				MailUtils.sendDefiMail(message, subject, mailTo);
			}
			catch (MessagingException e) {
				return false;
			}
		}

		return true;
	}
}
