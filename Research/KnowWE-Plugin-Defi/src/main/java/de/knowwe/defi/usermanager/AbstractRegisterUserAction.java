/*
 * Copyright (C) 2014 think-further.de
 */
package de.knowwe.defi.usermanager;

import java.io.IOException;

import org.apache.wiki.WikiEngine;
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

		String loginname = context.getParameter("loginname");
		String password = context.getParameter("password");
		String fullname = context.getParameter("fullname");
		String email = context.getParameter("email");

		WikiEngine eng = WikiEngine.getInstance(Environment.getInstance().getContext(), null);
		UserDatabase udb = eng.getUserManager().getUserDatabase();
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
			context.getResponse().getWriter().write(fullname + " konnte nicht angelegt werden.");
			return;
		}

		postCreation(newUser, eng, udb, context);
		context.getResponse().getWriter().write(getResponseText(newUser));
	}

	protected abstract String getResponseText(UserProfile newUser);

	protected abstract void postCreation(UserProfile newUser, WikiEngine eng, UserDatabase udb, UserActionContext context);


}
