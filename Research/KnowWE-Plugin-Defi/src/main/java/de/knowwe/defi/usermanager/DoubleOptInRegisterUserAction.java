/*
 * Copyright (C) 2014 think-further.de
 */
package de.knowwe.defi.usermanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.mail.MessagingException;

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
	public static String KEY_ACTIVATION_TOKEN = "DefiDoubleOptInActivationToken";

	@Override
	protected String getResponseText(UserProfile newUser) {
		return "Eine E-Mail mit Aktivierungs-Link wurde an " + newUser.getEmail() + " gesendet!";
	}

	@Override
	protected void postCreation(UserProfile newUser, WikiEngine eng, UserDatabase udb, UserActionContext context) {
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
			Log.severe("Unable to save locking of user: " + newUser, e);
		}

		// TODO: assemble message
		StringBuilder message = new StringBuilder();
		// TODO: get external URI of KnowWE...
		message.append(DoubleOptInConfirmationAction.PARAM_AUTH_TOKEN);
		message.append("=");
		message.append(activationToken);
		message.append("&");
		message.append(DoubleOptInConfirmationAction.PARAM_USER);
		message.append("=");
		message.append(newUser.getLoginName());

		try {
			MailUtils.sendDefiMail(message.toString(), "Bitte bestätigen Sie Ihre Registrierung im ICD-Forum.", newUser.getEmail());
		}
		catch (MessagingException e) {
			Log.severe("Activation message could not be transported to: " + newUser.getEmail());
		}
	}
}
