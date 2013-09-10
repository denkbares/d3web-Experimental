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

import java.util.ArrayList;
import java.util.Collection;

import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.defi.logger.DefiLoggerUtils;
import de.knowwe.defi.logger.DefiPageEventLogger;
import de.knowwe.defi.logger.DefiPageLogLine;


/**
 * 
 * @author dupke
 * @created 31.07.2013
 */
public class DefiPageEventListener implements EventListener {

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		ArrayList<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>(1);
		events.add(DefiPageEvent.class);

		return events;
	}

	@Override
	public void notify(Event event) {
			DefiPageEvent defiPageEvent = (DefiPageEvent) event;
			DefiPageLogLine line = new DefiPageLogLine();
			line.setUser(defiPageEvent.getUser());
			line.setPage(defiPageEvent.getPage());
			line.setStartDate(DefiLoggerUtils.getCurrentDate());
			line.setStartTime(DefiLoggerUtils.getCurrentTime());
			DefiPageEventLogger.logEntry(line);
	}

}
