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
 * @created 19.11.2013
 */
public class DefiPageRateClosedEvent extends Event {

	private final String EVENT_ID;
	private final String EVENT_USER;
	private final String EVENT_DISCUSSED;

	public DefiPageRateClosedEvent(String id, String user, String discussed) {
		EVENT_ID = id;
		EVENT_USER = user;
		EVENT_DISCUSSED = discussed;
	}

	public String getId() {
		return EVENT_ID;
	}

	public String getUser() {
		return EVENT_USER;
	}

	public String getDiscussed() {
		return EVENT_DISCUSSED;
	}
}
