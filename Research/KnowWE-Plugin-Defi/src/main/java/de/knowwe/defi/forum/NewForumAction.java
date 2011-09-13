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

import javax.servlet.http.HttpServletResponse;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;

/**
 * Reachable from the ForumMenu. Buildes new Forum.
 * 
 * @author dupke
 */
public class NewForumAction extends AbstractAction {

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
		GregorianCalendar now = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(
				KnowWEEnvironment.DEFAULT_WEB);
		JOptionPane jop = new JOptionPane(
				"Ihr Forum wurde erfolgreich erstellt.");
		String content;

		if (pageName == "") {
			content = "\n<forum topic='" + topic + "' name='" + topic + "'>\n" +
					"<box name=\"" + username + "\" date=\"" + sdf.format(now.getTime()) + "\">"
					+ message + "</box>\n</forum>";
		}
		else {
			content = "\n<forum topic='" + topic + "' unit='" + pageName + "' name='" + topic
					+ " (" + pageName + ")" + "'>\n" +
					"<box name=\"" + username + "\" date=\"" + sdf.format(now.getTime()) + "\">"
					+ message + "</box>\n</forum>";
		}

		title = pageName + " (" + topic + ")";
		if (mgr.getArticle(title) == null) {
				KnowWEEnvironment.getInstance().buildAndRegisterArticle(username, content,
						title, KnowWEEnvironment.DEFAULT_WEB);
				KnowWEEnvironment.getInstance().getWikiConnector()
						.createWikiPage(title, content, username);
		}
		else {
			jop.setMessage("Es existiert bereits ein Forum zu diesem Thema.");
		}

		JDialog dialog = jop.createDialog(null, "Hinweis");
		dialog.setAlwaysOnTop(true);
		dialog.setModal(true);
		dialog.setVisible(true);

		HttpServletResponse response = context.getResponse();
		response.sendRedirect("Wiki.jsp?redirect=" + title);
	}
}
