/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.util.MailUtil;

import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

/**
 * Reachable from the ForumMenu. Builds new forum.
 * 
 * @author dupke
 */
public class NewForumAction extends AbstractAction {

	private static final String FORUM_BUTTON = "Zum Diskussionsforum";
	private static final String BACK_BUTTON = "Zur&uuml;ck zur letzten Seite";

	@Override
	public void execute(UserActionContext context) throws IOException {
		String username = context.getUserName();
		// Seitenname = Einheit + Unterseite
		String pageName = context.getParameter("pagename");
		// Thema
		String topic = context.getParameter("topic");
		// Nachricht
		String message = context.getParameter("message");
		String title = "";
		String responseString = "\n";
		GregorianCalendar now = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		ArticleManager mgr = Environment.getInstance().getArticleManager(
				Environment.DEFAULT_WEB);

		String content = "";
		// links above the forum
		content += "<a class=\"forumLinkLeft\" href=\"\" onclick=\"javascript:history.back();return false;\">"
				+ BACK_BUTTON + "</a>\n";
		content += "<a class=\"forumLinkRight\" href=\"Wiki.jsp?page=Diskussion\">" + FORUM_BUTTON
				+ "</a>\n";
		content += "<div style='clear:both'></div>\n";

		if (pageName == "Sonstiges") {
			content += "\n<forum topic='" + topic + "' name='" + topic + "'>\n" +
					"<box name=\"" + username + "\" date=\"" + sdf.format(now.getTime()) + "\">"
					+ message + "</box>\n</forum>";
		}
		else {
			content += "\n<forum topic='" + topic + "' unit='" + pageName + "' name='" + topic
					+ " (" + pageName + ")" + "'>\n" +
					"<box name=\"" + username + "\" date=\"" + sdf.format(now.getTime()) + "\">"
					+ message + "</box>\n</forum>";
		}

		// links under the forum
		content += "\n<br /><br />\n<a class=\"forumLinkLeft\" href=\"\" onclick=\"javascript:history.back();return false;\">"
				+ BACK_BUTTON + "</a>\n";
		content += "<a class=\"forumLinkRight\" href=\"Wiki.jsp?page=Diskussion\">" + FORUM_BUTTON
				+ "</a>\n";
		content += "<div style='clear:both'></div>\n";

		if (pageName == "") title = "Forum zu " + " \"" + topic + "\"";
		else title = "Forum zu " + pageName + " (" + topic + ")";
		if (mgr.getArticle(title) == null) {
			Environment.getInstance().buildAndRegisterArticle(content,
						title, Environment.DEFAULT_WEB);
			Environment.getInstance().getWikiConnector()
						.createArticle(title, content, username);
		}
		else {
			responseString = "Ein Forum zu diesem Thema existiert bereits.\n";
		}

		/* Send mail */
		String nachricht = "Neuer Forumeintrag auf der Seite '" + title + "'";
		String subject = "Defi - neuer Forumeintrag";
		ResourceBundle rb = ResourceBundle.getBundle("KnowWE_Defi_config");
		String mailTo = rb.getString("defi.mail.to");

		try {
			ServletContext sc =
					Environment.getInstance().getWikiConnector().getServletContext();
			WikiEngine engine = WikiEngine.getInstance(sc, null);
			MailUtil.sendMessage(engine, mailTo, subject, nachricht);
		}
		catch (AddressException e) {
		}
		catch (MessagingException e) {
		}

		responseString += title;
		HttpServletResponse response = context.getResponse();
		response.getWriter().write(responseString);
	}
}
