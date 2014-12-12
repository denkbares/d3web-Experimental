/*
 * Copyright (C) 2014 think-further.de
 */
package de.knowwe.defi.usermanager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.wiki.WikiEngine;
import org.apache.wiki.auth.NoSuchPrincipalException;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.user.UserDatabase;
import org.apache.wiki.auth.user.UserProfile;

import de.d3web.utils.Log;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

/**
 * @author Sebastian Furth (think-further.de)
 * @created 13.11.14
 */
public class DoubleOptInConfirmationAction extends AbstractAction {

	public static final String PARAM_USER = "user";
	public static final String PARAM_AUTH_TOKEN = "auth";

	private static final SimpleDateFormat DF = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public void execute(UserActionContext context) throws IOException {
		WikiEngine eng = WikiEngine.getInstance(Environment.getInstance().getContext(), null);
		UserDatabase udb = eng.getUserManager().getUserDatabase();
		String loginName = context.getParameter(PARAM_USER);
		String authToken = context.getParameter(PARAM_AUTH_TOKEN);

		try {
			UserProfile user = udb.findByLoginName(loginName);
			String authKeyExpected = String.valueOf(user.getAttributes()
					.get(DoubleOptInRegisterUserAction.KEY_ACTIVATION_TOKEN));
			if (authKeyExpected.equals(authToken)) {
				user.setLockExpiry(DF.parse("01/01/1900"));
				udb.save(user);
			}
		}
		catch (NoSuchPrincipalException e) {
			Log.warning("Unable to find user with login name: " + loginName, e);
		}
		catch (ParseException e) {
			Log.severe("Unable to unlock user: " + loginName, e);
		}
		catch (WikiSecurityException e) {
			Log.severe("Unable to save unlocked user: " + loginName, e);
		}

		String baseUrl = context.getRequest().getRequestURL().toString();
		baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getRequest().getServletPath()));

		context.sendRedirect(baseUrl + "/Login.jsp?tab=confirmed");
	}
}
