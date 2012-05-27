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
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import com.ecyrd.jspwiki.WikiEngine;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.jspwiki.JSPWikiConnector;

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

		String subject = "Defi - Nachricht von " + username;
		String nachricht = "Absender: " + username + "\n";
		nachricht += "Seite: " + pagename + "\n";
		nachricht += "Formular: " + id + "\n\n";
		nachricht += "--------------------------------------\n\n";
		nachricht += context.getParameter("nachricht") + "\n\n";
		nachricht += "--------------------------------------\n";


		String responseText = "Vielen Dank!\nIhre Nachricht wurde erfolgreich versandt.";
		try {
			sendDefiMail(nachricht, subject);
		}
		catch (MessagingException e) {
			responseText = "Die Nachricht konnte nicht gesendet werden.";
		}

		HttpServletResponse response = context.getResponse();
		response.getWriter().write(responseText);
	}

	public void sendDefiMail(String message, String subject) throws MessagingException {
		JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(
				Environment.getInstance().getContext(), null));
		String from = wc.getWikiProperty("mail.from");
		String uname = wc.getWikiProperty("mail.smtp.account");
		String pass = wc.getWikiProperty("mail.smtp.password");
		String host = wc.getWikiProperty("mail.smtp.host");
		int port = Integer.parseInt(wc.getWikiProperty("mail.smtp.port"));
		ResourceBundle rb = ResourceBundle.getBundle("KnowWE_Defi_config");
		String mailTo = rb.getString("defi.mail.to");

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.port", port);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		Session session = Session.getDefaultInstance(props);
		Transport transport = session.getTransport("smtp");
		transport.connect(host, port, uname, pass);
		Address[] addresses = InternetAddress.parse(mailTo);

		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));
		msg.setRecipients(Message.RecipientType.TO, addresses);
		msg.setSubject(subject);
		msg.setText(message);
		transport.sendMessage(msg, addresses);
		transport.close();
	}
}
