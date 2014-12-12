/*
 * Copyright (C) 2014 think-further.de
 */
package de.knowwe.defi.usermanager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.wiki.WikiEngine;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.user.UserDatabase;
import org.apache.wiki.auth.user.UserProfile;

import de.d3web.utils.Log;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.defi.mailform.MailUtils;

/**
 *
 *
 * @author Sebastian Furth (think-further.de)
 * @created 13.11.14
 */
public class DoubleOptInRegisterUserAction extends AbstractRegisterUserAction {

	private static final SimpleDateFormat DF = new SimpleDateFormat("dd/MM/yyyy");
	public static final String KEY_ACTIVATION_TOKEN = "DefiDoubleOptInActivationToken";

	private static final String KEY_FIRST_ATTEMPT = "FirstRegistrationAttempt";
	private static final String KEY_SECOND_ATTEMPT = "SecondRegistrationAttempt";

	@Override
	protected boolean checkUnsecurePassword(String password) {
		if (password.length() < 8) return true;
		if (!(password.matches(".*[A-Z].*") // at least one upper case letter
				&& password.matches(".*[a-z].*") // at least one lower case letter
				&& password.matches(".*\\d.*"))) { // at least one digit
			return true;
		}
		return false;
	}

	@Override
	protected boolean checkSpam(UserActionContext context) {
		Date first = (Date) context.getSession().getAttribute(KEY_FIRST_ATTEMPT);
		Date second = (Date) context.getSession().getAttribute(KEY_SECOND_ATTEMPT);
		if (first != null && second != null) {
			Date current = new Date();
			long firstDiff = TimeUnit.MILLISECONDS.toMinutes(current.getTime() - first.getTime());
			long secondDiff = TimeUnit.MILLISECONDS.toMinutes(current.getTime() - second.getTime());
			if (firstDiff < 60 || secondDiff < 60) return true;
		}
		return false;
	}

	@Override
	protected String getResponseText(UserProfile newUser) {
		return "Eine E-Mail mit Aktivierungs-Link wurde an " + newUser.getEmail() + " gesendet!";
	}

	@Override
	protected void postCreation(UserProfile newUser, WikiEngine eng, UserDatabase udb, UserActionContext context) throws IOException {
		setSpamCounters(context);
		String activationToken = String.valueOf(newUser.hashCode() + System.currentTimeMillis());
		try {
			newUser.getAttributes().put(KEY_ACTIVATION_TOKEN, activationToken);
			newUser.setLockExpiry(DF.parse("01/01/2999"));
			udb.save(newUser);
		}
		catch (ParseException e) {
			Log.warning("Could not create lock for new user: " + newUser, e);
		}
		catch (WikiSecurityException e) {
			Log.severe("Unable to save lock of user: " + newUser, e);
		}

		// assemble activation link
		String baseUrl = context.getRequest().getRequestURL().toString();
		baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getRequest().getServletPath()));

		StringBuilder activationUrl = new StringBuilder();
		activationUrl.append(baseUrl);
		activationUrl.append("/action/");
		activationUrl.append(DoubleOptInConfirmationAction.class.getSimpleName());
		activationUrl.append("?");
		activationUrl.append(DoubleOptInConfirmationAction.PARAM_AUTH_TOKEN);
		activationUrl.append("=");
		activationUrl.append(activationToken);
		activationUrl.append("&");
		activationUrl.append(DoubleOptInConfirmationAction.PARAM_USER);
		activationUrl.append("=");
		activationUrl.append(newUser.getLoginName());

		StringBuilder message = new StringBuilder();
		message.append("Sehr geehrte/r Frau/Herr ");
		message.append(newUser.getFullname());
		message.append(",\n\n<br><br>");
		message.append("vielen Dank für Ihre Registrierung im ICD-Forum. Um den Vorgang abzuschließen, klicken Sie bitte auf nachfolgenden Link:\n<br>");
		message.append("<a href='");
		message.append(activationUrl);
		message.append("'>");
		message.append(activationUrl);
		message.append("</a>");
		message.append("\n\n<br><br>");
		message.append("Im Anschluss können Sie sich mit Ihren Zugangsdaten im ICD-Forum anmelden.\n\n<br><br>");
		message.append("Wir freuen uns auf Ihren Besuch!\n\n<br><br>");
		message.append("Ihr ICD-Team");

		try {
			MailUtils.sendDefiMail(message.toString(), "Bitte bestätigen Sie Ihre Registrierung im ICD-Forum.", newUser.getEmail());
		}
		catch (MessagingException e) {
			Log.severe("Activation message could not be transported to: " + newUser.getEmail());
			context.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			context.getWriter().write("Es konnte keine Bestätigungs-E-Mail an " + newUser.getEmail() + " gesendet werden, bitte wenden Sie sich an den Administrator.");
		}
	}

	private void setSpamCounters(UserActionContext context) {
		Date first = (Date) context.getSession().getAttribute(KEY_FIRST_ATTEMPT);
		Date second = (Date) context.getSession().getAttribute(KEY_SECOND_ATTEMPT);
		HttpSession session = context.getSession();
		if (first == null)
			session.setAttribute(KEY_FIRST_ATTEMPT, new Date());
		else if (second == null)
			session.setAttribute(KEY_SECOND_ATTEMPT, new Date());
		else if (first.before(second))
			session.setAttribute(KEY_FIRST_ATTEMPT, new Date());
		else
			session.setAttribute(KEY_SECOND_ATTEMPT, new Date());
	}
}
