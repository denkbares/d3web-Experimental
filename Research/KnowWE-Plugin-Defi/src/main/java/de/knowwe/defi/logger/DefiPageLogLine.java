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
public class DefiPageLogLine {

	/** Line entries **/
	private String user;
	private String page;
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;

	private final static String NO_DATA = "--";
	/** Separator S used in page log **/
	private final String S = DefiPageEventLogger.getSeparator();
	private final String SU = DefiPageEventLogger.getSeparatorUnicode();
	/** Regex to find page log line **/
	private final String MATCH_ME = "^((?!\\" + S + ").)+\\" + S + "((?!\\" + S + ").)+\\"
			+ S + "((?!\\" + S + ").)+\\" + S + "((?!\\" + S + ").)+\\" + S
			+ "((?!\\" + S + ").)+\\" + S + "((?!\\" + S + ").)+$";

	public DefiPageLogLine(String s) {
		if (!s.matches(MATCH_ME)) throw new IllegalArgumentException(
				"line has not the correct syntax.");

		String[] parts = s.split(S);
		user = parts[0];
		page = parts[1];
		startDate = parts[2];
		startTime = parts[3];
		endDate = parts[4];
		endTime = parts[5];
	}

	public DefiPageLogLine() {
		user = NO_DATA;
		startDate = NO_DATA;
		startTime = NO_DATA;
		endDate = NO_DATA;
		endTime = NO_DATA;
	}

	public static String getNoDataString() {
		return NO_DATA;
	}

	public String getUser() {
		return user;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getstartTime() {
		return startTime;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getPage() {
		return page;
	}

	public void setUser(String user) {
		this.user = user.replace(S, SU);
	}

	public void setStartDate(String date_start) {
		this.startDate = date_start.replace(S, SU);
	}

	public void setStartTime(String time_start) {
		this.startTime = time_start.replace(S, SU);
	}

	public void setEndDate(String date_end) {
		this.endDate = date_end.replace(S, SU);
	}

	public void setEndTime(String time_end) {
		this.endTime = time_end.replace(S, SU);
	}

	public void setPage(String page) {
		this.page = page.replace(S, SU);
	}

	@Override
	public String toString() {
		return user + S + page + S + startDate + S + startTime + S + endDate + S
				+ endTime;
	}
}
