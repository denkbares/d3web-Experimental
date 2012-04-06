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

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.auth.NoSuchPrincipalException;
import com.ecyrd.jspwiki.auth.WikiSecurityException;
import com.ecyrd.jspwiki.auth.user.UserDatabase;

import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;


/**
 * 
 * @author dupke
 * @created 05.04.2012
 */
public class DeleteUserAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String user = context.getParameter("username");
		String responseText = user + " wurde erfolgreich gelöscht.";

		WikiEngine eng = WikiEngine.getInstance(Environment.getInstance().getContext(), null);
		UserDatabase udb = eng.getUserManager().getUserDatabase();

		try {
			udb.deleteByLoginName(udb.find(user).getLoginName());
		}
		catch (NoSuchPrincipalException e) {
			responseText = "Kein Benutzer mit dem Namen " + user + " vorhanden.";
		}
		catch (WikiSecurityException e) {
			responseText = user + " konnte nicht gelöscht werden.";
		}

		context.getResponse().getWriter().write(responseText);
	}

}
