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


/**
 * 
 * @author dupke
 * @created 31.07.2013
 */
public class DefiSessionLogLine {

	/** Line entries **/
	private String user;
	private String loginDate;
	private String loginTime;
	private String logOutDate;
	private String logOutTime;
	private String timeout;

	private final static String NO_DATA = "--";

	/** Separator S used in pagelog **/
	private final String S = DefiSessionEventLogger.getSeparator();
	/** Regex to find sessionlogline **/
	private final String MATCH_ME = "^((?!" + S + ").)+" + S + "((?!" + S + ").)+" + S + "((?!" + S
			+ ").)+" + S + "((?!" + S + ").)+" + S + "((?!" + S + ").)+" + S + "((?!" + S + ").)+$";

	public DefiSessionLogLine(String s) {
		if (!s.matches(MATCH_ME)) throw new IllegalArgumentException(
				"line has not the correct syntax.");

		String[] parts = s.split(S);
		user = parts[0];
		loginDate = parts[1];
		loginTime = parts[2];
		logOutDate = parts[3];
		logOutTime = parts[4];
		timeout = parts[5];
	}

	public DefiSessionLogLine() {
		user = NO_DATA;
		loginDate = NO_DATA;
		loginTime = NO_DATA;
		logOutDate = NO_DATA;
		logOutTime = NO_DATA;
		timeout = NO_DATA;
	}

	public static String getNoDataSign() {
		return NO_DATA;
	}

	public String getUser() {
		return user;
	}

	public String getLoginDate() {
		return loginDate;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public String getLogOutDate() {
		return logOutDate;
	}

	public String getLogOutTime() {
		return logOutTime;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setLoginDate(String date) {
		this.loginDate = date;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public void setLogOutDate(String logOutDate) {
		this.logOutDate = logOutDate;
	}

	public void setLogOutTime(String logOutTime) {
		this.logOutTime = logOutTime;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public void setTimeout(boolean timeout) {
		this.timeout = String.valueOf(timeout);
	}

	public boolean equalLogin(DefiSessionLogLine line) {
		if (loginDate == null) {
			if (line.loginDate != null) return false;
		}
		else if (!loginDate.equals(line.loginDate)) return false;
		if (loginTime == null) {
			if (line.loginTime != null) return false;
		}
		else if (!loginTime.equals(line.loginTime)) return false;
		if (user == null) {
			if (line.user != null) return false;
		}
		else if (!user.equals(line.user)) return false;
		return true;
	}

	public String getLoginDateTime() {
		return getLoginDate() + " " + getLoginTime();
	}

	@Override
	public String toString() {
		return user + S + loginDate + S + loginTime + S + logOutDate + S + logOutTime + S + timeout;
	}

}
