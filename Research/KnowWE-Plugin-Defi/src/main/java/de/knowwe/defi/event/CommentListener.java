/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.defi.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletContext;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.auth.NoSuchPrincipalException;
import com.ecyrd.jspwiki.auth.user.UserProfile;
import com.ecyrd.jspwiki.util.MailUtil;

import de.d3web.we.event.NewCommentEvent;
import de.knowwe.core.Environment;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;

/**
 * Listens to the NewCommentEvent of the Comment plugin. If fired a user
 * specified in the project properties is notified per mail of this event. The
 * mail is send through the Wiki mail engine.
 *
 * @author smark
 * @created 08.03.2011
 */
public class CommentListener implements EventListener {

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		ArrayList<Class<? extends Event>> events = new ArrayList<Class<? extends
				Event>>(1);
		events.add(NewCommentEvent.class);
		return events;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof NewCommentEvent) {
			NewCommentEvent e = (NewCommentEvent) event;
			this.notifyMail(e.getTopic(), e.getComment());
		}
	}

	/**
	 * Creates the notification mail content and sends the email to the user
	 * specified in the projects properties file.
	 *
	 * @created 08.03.2011
	 * @param topic
	 * @param comment
	 * @return boolean
	 */
	private boolean notifyMail(String topic, String comment) {

		ResourceBundle rb = ResourceBundle.getBundle("KnowWE_Defi_config");

		String to = rb.getString("defi.mail.to");
		String subject = rb.getString("defi.mail.subject");
		String content = rb.getString("defi.mail.content.template");

		// replace @page@
		content = content.replaceAll("@page@", topic);

		// replace @ink@
		String link = Environment.getInstance().getWikiConnector().getBaseUrl();
		link += "/Wiki.jsp?page=" + topic;
		content = content.replaceAll("@link@", link);

		// replace @name@
		String user = rb.getString("defi.last.login.user");
		String fullName = this.getRealName(user);
		content = content.replaceAll("@name@", fullName);

		// replace @comment@
		content = content.replaceAll("@comment@", comment);

		if (rb.getString("defi.dev").equals("false")) {
			return this.notifyMail(to, subject, content);
		}
		else {
			return true; // in dev environement
		}
	}

	/**
	 * Send an email to the specified mail address (see project properties
	 * file), to inform the owner of a new comment an a certain page.
	 *
	 * @created 15.03.2011
	 * @param to
	 * @param subject
	 * @param content
	 * @return
	 */
	private boolean notifyMail(String to, String subject, String content) {
		try {
			ServletContext sc = Environment.getInstance().getWikiConnector().getServletContext();
			WikiEngine engine = WikiEngine.getInstance(sc, null);
			MailUtil.sendMessage(engine, to, subject, content);
		}
		catch (AddressException e) {
			// mail address is erroneous, stop sending
			return false;
		}
		catch (MessagingException e) {
			return false;
		}
		return true;
	}

	/**
	 * Deciphers the login name of a user and returns the real name.
	 *
	 * @created 15.03.2011
	 * @param loginName
	 * @return
	 */
	private String getRealName(String loginName) {

		UserProfile profile;
		try {
			ServletContext sc = Environment.getInstance().getWikiConnector().getServletContext();
			WikiEngine engine = WikiEngine.getInstance(sc, null);
			profile = engine.getUserManager().getUserDatabase().find(loginName);
			if (profile != null) {
				return profile.getFullname();
			}
		}
		catch (NoSuchPrincipalException e) {
			return loginName;
		}
		return loginName;
	}
}
