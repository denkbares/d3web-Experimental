/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.event;

import javax.servlet.ServletContext;

import org.apache.wiki.WikiEngine;
import org.apache.wiki.event.WikiEvent;
import org.apache.wiki.event.WikiEventListener;
import org.apache.wiki.event.WikiEventManager;
import org.apache.wiki.event.WikiSecurityEvent;

import de.knowwe.core.Environment;
import de.knowwe.defi.logger.DefiLoggerUtils;
import de.knowwe.defi.logger.DefiPageEventLogger;
import de.knowwe.defi.logger.DefiSessionEventLogger;
import de.knowwe.defi.logger.DefiSessionLogLine;

/**
 * 
 * @author dupke
 * @created 31.07.2013
 */
public class DefiSessionEventListener implements WikiEventListener {

	public DefiSessionEventListener() {
		// TODO:
		// "java.lang.NullPointerException: Used WikiConnector can not provide a ServletContext"
		try {
			ServletContext context = Environment.getInstance().getContext();
			WikiEngine en = WikiEngine.getInstance(context, null);
			WikiEventManager.addWikiEventListener(en.getAuthenticationManager(), this);
		}
		catch (Exception e) {

		}
	}

	@Override
	public void actionPerformed(WikiEvent event) {

		if (!(event instanceof WikiSecurityEvent)
				|| (event.getType() != WikiSecurityEvent.LOGIN_AUTHENTICATED
						&& event.getType() != WikiSecurityEvent.LOGOUT && event.getType() != WikiSecurityEvent.SESSION_EXPIRED)) return;

		WikiSecurityEvent e = (WikiSecurityEvent) event;
		DefiSessionLogLine line = new DefiSessionLogLine();
		line.setUser(DefiLoggerUtils.getUserNameOfWikiSecurityEvent(e));

		String dateOfWikiEvent = DefiLoggerUtils.getDateOfWikiEvent(e);
		String timeOfWikiEvent = DefiLoggerUtils.getTimeOfWikiEvent(e);
		if (e.getType() == WikiSecurityEvent.LOGIN_AUTHENTICATED) {
			line.setLoginDate(dateOfWikiEvent);
			line.setLoginTime(timeOfWikiEvent);
			DefiSessionEventLogger.logLogin(line);
		}
		else {
			DefiSessionLogLine last = DefiSessionEventLogger.findLastEntryForUser(line.getUser());
			if (last == null) last = line;
			last.setLogOutDate(dateOfWikiEvent);
			last.setLogOutTime(timeOfWikiEvent);
			last.setTimeout(e.getType() == WikiSecurityEvent.SESSION_EXPIRED);
			DefiSessionEventLogger.logLogout(last);
			DefiPageEventLogger.updateLastEntryOnLogout(last.getUser(), dateOfWikiEvent,
					timeOfWikiEvent);
		}
	}

}
