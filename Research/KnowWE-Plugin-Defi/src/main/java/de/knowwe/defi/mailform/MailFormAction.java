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
package de.knowwe.defi.mailform;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.util.MailUtil;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

/**
 * Send a mail, show/hide form.
 * 
 * @author dupke
 */
public class MailFormAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String username = context.getUserName();
		String pagename = context.getTitle();
		String id = context.getParameter("id");

		String nachricht = "Absender: " + username + "\n";
		nachricht += "Seite: " + pagename + "\n";
		nachricht += "Formular: " + id + "\n\n";
		nachricht += "--------------------------------------\n\n";
		nachricht += context.getParameter("nachricht") + "\n\n";
		nachricht += "--------------------------------------\n";

		String subject = "Defi - Nachricht von " + username;
		ResourceBundle rb = ResourceBundle.getBundle("KnowWE_Defi_config");
		String mailTo = rb.getString("defi.mailform.to");
		String responseText = "Vielen Dank!\nIhre Nachricht wurde erfolgreich versandt.";

		try {
			ServletContext sc =
					Environment.getInstance().getWikiConnector().getServletContext();
			WikiEngine engine = WikiEngine.getInstance(sc, null);
			MailUtil.sendMessage(engine, mailTo, subject, nachricht);
		}
		catch (AddressException e) {
			responseText = "Die Nachricht konnte nicht gesendet werden.";
		}
		catch (MessagingException e) {
			responseText = "Die Nachricht konnte nicht gesendet werden.";
		}

		HttpServletResponse response = context.getResponse();
		response.getWriter().write(responseText);
	}
}
