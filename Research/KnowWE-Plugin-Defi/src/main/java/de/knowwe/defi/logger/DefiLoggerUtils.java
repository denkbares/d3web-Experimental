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
package de.knowwe.defi.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wiki.WikiEngine;
import org.apache.wiki.auth.NoSuchPrincipalException;
import org.apache.wiki.auth.user.UserDatabase;
import org.apache.wiki.auth.user.UserProfile;
import org.apache.wiki.event.WikiEvent;
import org.apache.wiki.event.WikiSecurityEvent;

import de.knowwe.core.Environment;

/**
 * 
 * @author dupke
 * @created 31.07.2013
 */
public class DefiLoggerUtils {

	/** Sepearator between entries in a logline **/
	public final static String SEPARATOR = "~";
	public final static String SEPARATOR_UNICODE = "&#126;";
	/** encoding **/
	public final static String ENCODING = "UTF-8";

	public static String getUserNameOfWikiSecurityEvent(WikiSecurityEvent e) {
		WikiEngine eng = WikiEngine.getInstance(Environment.getInstance().getContext(),
				null);
		UserDatabase udb = eng.getUserManager().getUserDatabase();
		UserProfile user;
		String userName = e.getPrincipal().toString();
		userName = userName.substring(28, userName.length() - 1);
		try {
			user = udb.findByLoginName(userName);
			userName = user.getFullname();
		}
		catch (NoSuchPrincipalException e1) {
		}

		return userName;
	}

	public static String getDateOfWikiEvent(WikiEvent e) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String date = sdf.format(new Date(e.getWhen()));

		return date;
	}

	public static String getTimeOfWikiEvent(WikiEvent e) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time = sdf.format(new Date(e.getWhen()));

		return time;
	}

	/**
	 * 
	 * @created 19.11.2012
	 * @return
	 */
	public static String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String date = sdf.format(new Date(System.currentTimeMillis()));

		return date;
	}

	/**
	 * 
	 * @created 19.11.2012
	 * @return
	 */
	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time = sdf.format(new Date(System.currentTimeMillis()));

		return time;
	}

	/**
	 * 
	 * @created Mar 19, 2014
	 * @return
	 */
	public static String getEncoding() {
		return ENCODING;
	}
}
