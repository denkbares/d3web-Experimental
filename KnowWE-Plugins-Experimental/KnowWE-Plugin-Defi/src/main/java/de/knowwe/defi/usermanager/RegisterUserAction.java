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

import java.security.Principal;
import java.util.Date;

import org.apache.wiki.WikiEngine;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.authorize.Group;
import org.apache.wiki.auth.user.UserDatabase;
import org.apache.wiki.auth.user.UserProfile;

import de.knowwe.core.action.UserActionContext;


/**
 * 
 * @author dupke
 * @created 05.04.2012
 */
public class RegisterUserAction extends AbstractRegisterUserAction {

	@Override
	protected boolean checkUnsecurePassword(String password) {
		return false; // unsecure passwords are allowed when defined by admin
	}

	@Override
	protected boolean checkSpam(UserActionContext context) {
		return false;
	}

	@Override
	protected String getResponseText(UserProfile newUser) {
		return newUser.getFullname() + " wurde erfolgreich erstellt.";
	}

	@Override
	protected void postCreation(UserProfile newUser, WikiEngine eng, UserDatabase udb, UserActionContext context) {
		// add to admins
		boolean admin = Boolean.valueOf(context.getParameter("admin"));
		if (admin) {
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
	}

}
