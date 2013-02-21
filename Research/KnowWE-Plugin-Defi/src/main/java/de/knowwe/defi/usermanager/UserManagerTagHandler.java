/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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

import java.security.Principal;
import java.util.Map;

import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.auth.NoSuchPrincipalException;
import com.ecyrd.jspwiki.auth.WikiSecurityException;
import com.ecyrd.jspwiki.auth.authorize.GroupManager;
import com.ecyrd.jspwiki.auth.user.UserDatabase;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.jspwiki.JSPWikiConnector;

/**
 * 
 * @author dupke
 * @created 05.04.2012
 */
/**
 * 
 * @author dupke
 * @created 06.04.2012
 */
/**
 * 
 * @author dupke
 * @created 06.04.2012
 */
public class UserManagerTagHandler extends AbstractTagHandler {

	/**
	 * @param name
	 */
	public UserManagerTagHandler() {
		super("usermanager");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {
		StringBuilder builder = new StringBuilder();

		if (userContext.userIsAdmin()) {
			builder.append("<div id='usermanager'>");
			builder.append("<h4>Benutzer bearbeiten:</h4>");
			JSPWikiConnector wc = new JSPWikiConnector(WikiEngine.getInstance(
					Environment.getInstance().getContext(), null));
			String[] users = wc.getAllUsers();
			int i = 0;

			builder.append("<table>");
			for (String user : users) {
				if (user.equals(userContext.getUserName())) continue;
				builder.append("<tr>");
				builder.append("<td><a href='" + JSPWikiConnector.LINK_PREFIX + user + "'>" + user
						+ "</a></td>\n");
				// edit-button
				builder.append("<td><span onclick='showEditUser(" + i
						+ ")'>[Bearbeiten]</span></td>\n");
				// delete-button
				builder.append("<td><span onclick='deleteUser(\"" + user
						+ "\")'>[Löschen]</span></td>\n");
				builder.append("</tr><tr>\n");
				builder.append("<td colspan='3'><div style='display:none'>\n");
				// change userdata form
				builder.append("<form><table>");
				builder.append("<tr><th colspan='2'>" + user + "</th></tr>");
				builder.append("<tr><td>Neues Passwort:</td><td><input type='password' name='password' size='20' value='' /></td></tr>");
				builder.append("<tr><td>Passwort wiederholen:</td><td><input type='password' name='password2' size='20' value='' /></td></tr>");
				// - radiobuttons
				builder.append("<tr><td>Admin*</td><td><input type='radio' name='admin' value='true'");
				if (userIsAdmin(user)) builder.append(" checked");
				builder.append(" />Ja");
				builder.append("<input type='radio' name='admin' value='false'");
				if (!userIsAdmin(user)) builder.append(" checked");
				builder.append(" />Nein</td></tr>");
				builder.append("<tr><td colspan='2' style='font-size:80%'>*(Die Passwortfelder leer lassen, um nur die Adminrechte zu ändern)</td></tr>");
				// - -------------
				builder.append("</table>");
				builder.append("<input type='button' onclick='editUser(" + i + ", \"" + user
						+ "\")' value='Änderungen speichern' />");
				builder.append("<input type='button' onclick='showEditUser(" + i
						+ ")' value='Abbrechen' />");
				builder.append("</form></div></td></tr>\n");
				// ------------------------------
				i++;
			}
			// new user
			builder.append("<tr><td colspan='3'>");
			builder.append("<span onClick='showEditUser(" + i + ")'>[Neuer Benutzer]</span>");
			builder.append("</td></tr>\n");
			builder.append("<tr><td colspan='3'><div style='display:none;margin-bottom:15px'>");
			builder.append(getNewUserForm());
			builder.append("<input type='button' onclick='registerUser()' value='abschicken' />");
			builder.append("<input type='button' onclick='showEditUser(" + i
					+ ")' value='Abbrechen' />");
			builder.append("</div></td></tr>");

			builder.append("</table></div>");
		}

		result.appendHTML(builder.toString());
	}

	/**
	 * Test if user is admin.
	 */
	private boolean userIsAdmin(String username) {
		WikiEngine eng = WikiEngine.getInstance(Environment.getInstance().getContext(),
				null);
		UserDatabase udb = eng.getUserManager().getUserDatabase();
		GroupManager gm = eng.getGroupManager();
		try {
			for (Principal princ : udb.getWikiNames()) {
				if (princ.getName().equals(udb.find(username).getWikiName())) {
					return gm.getGroup("Admin").isMember(princ);
				}
			}
		}
		catch (NoSuchPrincipalException e) {
		}
		catch (WikiSecurityException e) {
		}

		return false;
	}

	/**
	 * Build a new userform.
	 */
	private String getNewUserForm() {
		StringBuilder builder = new StringBuilder();
		builder.append("<form><table><tr><th colspan='2'>Neuer Benutzer</th></tr>");
		builder.append("<tr><td>Benutzername (Loginname)</td>");
		builder.append("<td><input type='text' value='' size='20' name='loginname' /></td>");
		builder.append("</tr><tr>");
		builder.append("<td>Passwort</td>");
		builder.append("<td><input type='password' value='' size='20' name='password' /></td>");
		builder.append("</tr><tr>");
		builder.append("<td>Passwort wiederholen</td>");
		builder.append("<td><input type='password' value='' size='20' name='password2' /></td>");
		builder.append("</tr><tr>");
		builder.append("<td>Voller Name (Wikiname)</td>");
		builder.append("<td><input type='text' value='' size='20' name='fullname' /></td>");
		builder.append("</tr><tr>");
		builder.append("<td>E-Mail-Adresse (optional)</td>");
		builder.append("<td><input type='text' value='' size='20' name='email' /></td>");
		builder.append("</tr><tr>");
		builder.append("<td>Admin: </td><td><input type=\"radio\" name=\"admin\" value=\"true\" />Ja");
		builder.append("<input type=\"radio\" name=\"admin\" value=\"false\" checked />Nein</td>");
		builder.append("</tr></table></form>");
		return builder.toString();
	}

}
