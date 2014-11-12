/*
 * Copyright (C) 2014 think-further.de
 */
package de.knowwe.defi.mailform;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.wiki.WikiEngine;

import de.knowwe.core.Environment;
import de.knowwe.jspwiki.JSPWikiConnector;

/**
 * @author Sebastian Furth (think-further.de)
 * @created 13.11.14
 */
public class MailUtils {

	public static void sendDefiMail(String message, String subject, String mailTo) throws MessagingException {
		JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(
				Environment.getInstance().getContext(), null));
		String from = wc.getWikiProperty("mail.from");
		String uname = wc.getWikiProperty("mail.smtp.account");
		String pass = wc.getWikiProperty("mail.smtp.password");
		String host = wc.getWikiProperty("mail.smtp.host");
		int port = Integer.parseInt(wc.getWikiProperty("mail.smtp.port"));

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
