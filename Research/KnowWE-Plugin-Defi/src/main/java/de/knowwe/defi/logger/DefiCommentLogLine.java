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
public class DefiCommentLogLine {

	/** Line entries **/
	private String user;
	private String msg;
	private String date;
	private String time;
	private String adressee;
	private String response;
	private String unit;
	private String topic;

	private final static String NO_DATA = "--";
	/** Separator S used in comment log **/
	private final String S = DefiCommentEventLogger.getSeparator();
	private final String SU = DefiCommentEventLogger.getSeperatorUnicode();
	/** Regex to find comment log line **/
	private final String MATCH_ME = "^((?!\\" + S + ").)+\\" + S + "((?!\\" + S + ").)+\\" + S
			+ "((?!\\" + S + ").)+\\" + S + "((?!\\" + S + ").)+\\" + S + "((?!\\" + S + ").)+\\"
			+ S + "((?!\\" + S + ").)+\\" + S + "((?!\\" + S + ").)+\\" + S + "((?!\\"
			+ S + ").)+$";

	/**
	 * @param user comment's author
	 * @param msg comment's message
	 * @param adressee comment's adressee (offen||berater||[username])
	 * @param response (startbeitrag||[username])
	 * @param unit unit number
	 */
	public DefiCommentLogLine(String line) {
		if (!line.matches(MATCH_ME)) throw new IllegalArgumentException(
				"line has not the correct syntax.");

		String[] parts = line.split(S);
		user = parts[0];
		msg = parts[1];
		date = parts[2];
		time = parts[3];
		adressee = parts[4];
		response = parts[5];
		unit = parts[6];
		topic = parts[7];
	}

	public DefiCommentLogLine() {
		user = NO_DATA;
		msg = NO_DATA;
		date = NO_DATA;
		time = NO_DATA;
		adressee = NO_DATA;
		response = NO_DATA;
		unit = NO_DATA;
		topic = NO_DATA;
	}

	public String getUser() {
		return user.replace(SU, S);
	}

	public String getMsg() {
		return msg.replace(SU, S);
	}

	public String getDate() {
		return date.replace(SU, S);
	}

	public String getTime() {
		return time.replace(SU, S);
	}

	public String getAdressee() {
		return adressee.replace(SU, S);
	}

	public String getResponse() {
		return response.replace(SU, S);
	}

	public String getUnit() {
		return unit.replace(SU, S);
	}

	public String getTopic() {
		return topic.replace(SU, S);
	}

	public void setUser(String user) {
		this.user = user.replace(S, SU);
	}

	public void setMsg(String msg) {
		this.msg = msg.replace(S, SU);
	}

	public void setDate(String date) {
		this.date = date.replace(S, SU);
	}

	public void setTime(String time) {
		this.time = time.replace(S, SU);
	}

	public void setAdressee(String adressee) {
		this.adressee = adressee.replace(S, SU);
	}

	public void setResponse(String response) {
		this.response = response.replace(S, SU);
	}

	public void setUnit(String unit) {
		this.unit = unit.replace(S, SU);
	}

	public void setTopic(String topic) {
		this.topic = topic.replace(S, SU);
	}

	@Override
	public String toString() {
		return user + S + msg + S + date + S + time + S + adressee + S + response + S
				+ unit + S + topic;
	}

}
