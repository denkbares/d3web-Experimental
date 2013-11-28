/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.logger;

import de.knowwe.defi.event.DefiPageRatedEvent;

/**
 * 
 * @author dupke
 * @created 18.11.2013
 */
public class DefiPageRateLogLine {

	/** Line entries **/
	private  String id;
	private  String page;
	private  String user;
	private  String date;
	private  String realvalue;
	private  String value;
	private  String label;
	private  String discussed;
	private  String closed;

	private final static String NO_DATA = "--";

	/** Separator S used in pagelog **/
	private final String S = DefiPageRateEventLogger.getSeparator();
	/** Regex to find sessionlogline **/
	private final String MATCH_ME = "^((?!" + S + ").)+" + S + "((?!" + S + ").)+" + S + "((?!" + S
			+ ").)+" + S + "((?!" + S + ").)+" + S + "((?!" + S + ").)+" + S + "((?!" + S + ").)+"
			+ S + "((?!" + S + ").)+" + S + "((?!" + S + ").)+" + S + "((?!" + S + ").)+$";

	public DefiPageRateLogLine(String s) {
		if (!s.matches(MATCH_ME)) throw new IllegalArgumentException(
				"line has not the correct syntax.");

		String[] parts = s.split(S);
		id = parts[0];
		page = parts[1];
		user = parts[2];
		date = parts[3];
		realvalue = parts[4];
		value = parts[5];
		label = parts[6];
		discussed = parts[7];
		closed = parts[8];
	}

	public DefiPageRateLogLine() {
		id = NO_DATA;
		page = NO_DATA;
		user = NO_DATA;
		date = NO_DATA;
		realvalue = NO_DATA;
		value = NO_DATA;
		label = NO_DATA;
		discussed = NO_DATA;
		closed = NO_DATA;
	}

	public DefiPageRateLogLine(DefiPageRatedEvent event) {
		id = event.getId();
		page = event.getTitle();
		user = event.getUser();
		date = event.getDate();
		realvalue = event.getRealvalue();
		value = event.getValue().equals("") ? NO_DATA : event.getValue();
		label = event.getLabel().equals("") ? NO_DATA : event.getLabel();
		discussed = event.getDiscussed();
		closed = event.getClosed().equals("") ? NO_DATA : event.getClosed();
	}

	public String getId() {
		return id;
	}

	public String getPage() {
		return page;
	}

	public String getUser() {
		return user;
	}

	public String getDate() {
		return date;
	}

	public String getRealvalue() {
		return realvalue;
	}

	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public String getDiscussed() {
		return discussed;
	}

	public String getClosed() {
		return closed;
	}

	public static String getNoData() {
		return NO_DATA;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setRealvalue(String realvalue) {
		this.realvalue = realvalue;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setDiscussed(String discussed) {
		this.discussed = discussed;
	}

	public void setClosed(String closed) {
		this.closed = closed;
	}

	public boolean equalsLogLine(DefiPageRateLogLine line) {
		return id.equals(line.getId()) && user.equals(line.getUser());
	}

	@Override
	public String toString() {
		return id + S + page + S + user + S + date + S + realvalue + S + value + S + label + S
				+ discussed + S + closed;
	}
}
