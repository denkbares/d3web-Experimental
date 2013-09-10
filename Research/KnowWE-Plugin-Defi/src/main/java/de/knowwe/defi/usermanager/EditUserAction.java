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
import com.ecyrd.jspwiki.auth.NoSuchPrincipalException;
import com.ecyrd.jspwiki.auth.WikiSecurityException;
import com.ecyrd.jspwiki.auth.authorize.Group;
import com.ecyrd.jspwiki.auth.authorize.GroupManager;
import com.ecyrd.jspwiki.auth.user.UserDatabase;
import com.ecyrd.jspwiki.auth.user.UserProfile;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;


/**
 * 
 * @author dupke
 * @created 05.04.2012
 */
public class EditUserAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String username = context.getParameter("username");
		String password = context.getParameter("password");
		String admin = context.getParameter("admin");
		String responseText = username + " wurde erfolgreich editiert.";
		Principal userPrinc = null;


		WikiEngine eng = WikiEngine.getInstance(Environment.getInstance().getContext(), null);
		UserDatabase udb = eng.getUserManager().getUserDatabase();
		GroupManager gm = eng.getGroupManager();
		try {
			UserProfile user = udb.find(username);

			for (Principal princ : udb.getWikiNames()) {
				if (princ.getName().equals(user.getWikiName()))
					userPrinc = princ;
			}
			
			// admin
			if (userPrinc != null) {
				Group adminGrp = gm.getGroup("Admin");
				// make him admin
				if (admin.equals("true") && !adminGrp.isMember(userPrinc)) {
					adminGrp.add(userPrinc);
				}
				// remove admin
				else if (admin.equals("false") && adminGrp.isMember(userPrinc)) {
					adminGrp.remove(userPrinc);
				}
				// fix a strange error with missing created-date and save
				// admin-group
				if (adminGrp.getCreated() == null) adminGrp.setCreated(new Date());
				eng.getGroupManager().getGroupDatabase().save(adminGrp, adminGrp.getPrincipal());
			}
			else {
				if (password == "") responseText = "Die Adminrechte konnten nicht verändert werden.";
			}
			
			// Set user preferences
			if (password != "") {
				user.setPassword(password);
				udb.save(user);
			}
			
		}
		catch (NoSuchPrincipalException e) {
			responseText = username + " konnte nicht editiert werden.";
		}
		catch (WikiSecurityException e) {
			responseText = username + " konnte nicht editiert werden.";
		}

		context.getResponse().getWriter().write(responseText);
	}

}
