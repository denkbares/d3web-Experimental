/*
 * Copyright (C) 2014 think-further.de
 */
package de.knowwe.defi.usermanager;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.WikiEngine;
import org.apache.wiki.auth.NoSuchPrincipalException;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.user.UserDatabase;
import org.apache.wiki.auth.user.UserProfile;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.event.EventManager;

/**
 * @author Sebastian Furth (think-further.de)
 * @created 13.11.14
 */
public abstract class AbstractRegisterUserAction extends AbstractAction {


	@Override
	public void execute(UserActionContext context) throws IOException {
		
		if (checkSpam(context)) {
			context.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			context.getWriter().write("Zu viele Registrierungsversuche. Bitte versuchen Sie es später noch ein mal.");
			return;
		}


		String password = context.getParameter("password");
		if (checkUnsecurePassword(password)) {
			context.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			context.getWriter().write("Das Passwort muss Groß- und Kleinbuchstaben sowie Zahlen enthalten und mindestens 8 Zeichen umfassen.");
			return;
		}

		String loginname = context.getParameter("loginname");
		String fullname = context.getParameter("fullname");
		String email = context.getParameter("email");

		WikiEngine eng = WikiEngine.getInstance(Environment.getInstance().getContext(), null);
		UserDatabase udb = eng.getUserManager().getUserDatabase();
		try {
			if (udb.findByLoginName(loginname) != null) {
				context.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				context.getWriter().write("Es existiert bereits ein Nutzer mit dem Login-Namen \"" + loginname + "\"! Bitte wählen Sie einen anderen Login-Namen.");
				return;
			}
		}
		catch (NoSuchPrincipalException e) {
			// Do nothing, this is what we want for new users!
		}

		try {
			if (udb.findByFullName(fullname) != null) {
				context.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				context.getWriter().write("Es existiert bereits ein Nutzer mit dem Namen \"" + fullname + "\"! Bitte wählen Sie einen anderen Namen.");
				return;
			}
		}
		catch (NoSuchPrincipalException e) {
			// Do nothing, this is what we want for new users!
		}

		UserProfile newUser = udb.newProfile();
		newUser.setLoginName(loginname);
		newUser.setPassword(password);
		newUser.setFullname(fullname);
		newUser.setEmail(email);


		// save new user
		try {
			udb.save(newUser);
			EventManager.getInstance().fireEvent(new UserRegisteredEvent(newUser));
		}
		catch (WikiSecurityException e) {
			context.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			context.getWriter().write("Interner Server-Fehler: Nutzer \"" + loginname + "\" konnte nicht angelegt werden.");
			return;
		}

		postCreation(newUser, eng, udb, context);
		context.getResponse().getWriter().write(getResponseText(newUser));
	}

	protected abstract boolean checkUnsecurePassword(String password);

	protected abstract boolean checkSpam(UserActionContext context);

	protected abstract String getResponseText(UserProfile newUser);

	protected abstract void postCreation(UserProfile newUser, WikiEngine eng, UserDatabase udb, UserActionContext context) throws IOException;


}
