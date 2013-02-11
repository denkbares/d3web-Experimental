/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.defi.usermanager;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.auth.WikiSecurityException;
import com.ecyrd.jspwiki.auth.authorize.Group;
import com.ecyrd.jspwiki.auth.user.UserDatabase;
import com.ecyrd.jspwiki.auth.user.UserProfile;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.event.EventManager;


/**
 * 
 * @author dupke
 * @created 05.04.2012
 */
public class RegisterUserAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String loginname = context.getParameter("loginname");
		String password = context.getParameter("password");
		String fullname = context.getParameter("fullname");
		String email = context.getParameter("email");
		String admin = context.getParameter("admin");
		String responseText = fullname + " wurde erfolgreich erstellt.";

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
			responseText = fullname + " konnte nicht angelegt werden.";
		}

		// add to admins
		if (admin.equals("true")) {
			try {
				Principal userPrinc = null;
				for (Principal princ : udb.getWikiNames()) {
					if (princ.getName().equals(newUser.getWikiName())) userPrinc = princ;
				}

				if (userPrinc != null) {
					Group group = eng.getGroupManager().getGroup("Admin");
					group.add(userPrinc);
					// fix a strange error with missing created-date
					if (group.getCreated() == null) group.setCreated(new Date());
					eng.getGroupManager().getGroupDatabase().save(group, group.getPrincipal());
				}

			}
			catch (WikiSecurityException e) {
			}
		}

		context.getResponse().getWriter().write(responseText);
	}

}
