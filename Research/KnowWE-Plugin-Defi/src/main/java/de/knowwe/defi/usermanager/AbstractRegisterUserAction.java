/*
 * Copyright (C) 2013 denkbares GmbH
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
 * @author Sebastian Furth (denkbares GmbH)
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
