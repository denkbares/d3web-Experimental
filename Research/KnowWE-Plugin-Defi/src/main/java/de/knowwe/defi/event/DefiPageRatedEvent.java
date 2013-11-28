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

import de.knowwe.core.event.Event;


/**
 * 
 * @author dupke
 * @created 18.11.2013
 */
public class DefiPageRatedEvent extends Event {

	private final String EVENT_ID;
	private final String EVENT_TITLE;
	private final String EVENT_USER;
	private final String EVENT_DATE;
	private final String EVENT_REALVALUE;
	private final String EVENT_VALUE;
	private final String EVENT_LABEL;
	private final String EVENT_DISCUSSED;
	private final String EVENT_CLOSED;

	public DefiPageRatedEvent(String id, String title, String user, String date, String realvalue, String value, String label, String discussed, String closed) {
		EVENT_ID = id;
		EVENT_TITLE = title;
		EVENT_USER = user;
		EVENT_DATE = date;
		EVENT_REALVALUE = realvalue;
		EVENT_VALUE = value;
		EVENT_LABEL = label;
		EVENT_DISCUSSED = discussed;
		EVENT_CLOSED = closed;
	}

	public String getId() {
		return EVENT_ID == null ? "" : EVENT_ID;
	}

	public String getTitle() {
		return EVENT_TITLE == null ? "" : EVENT_TITLE;
	}

	public String getUser() {
		return EVENT_USER == null ? "" : EVENT_USER;
	}

	public String getDate() {
		return EVENT_DATE == null ? "" : EVENT_DATE;
	}

	public String getRealvalue() {
		return EVENT_REALVALUE == null ? "" : EVENT_REALVALUE;
	}

	public String getValue() {
		return EVENT_VALUE == null ? "" : EVENT_VALUE;
	}

	public String getLabel() {
		return EVENT_LABEL == null ? "" : EVENT_LABEL;
	}

	public String getDiscussed() {
		return EVENT_DISCUSSED == null ? "" : EVENT_DISCUSSED;
	}

	public String getClosed() {
		return EVENT_CLOSED == null ? "" : EVENT_CLOSED;
	}
}
