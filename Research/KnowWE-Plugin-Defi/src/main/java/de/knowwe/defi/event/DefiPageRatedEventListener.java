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
import de.knowwe.defi.logger.DefiPageRateEventLogger;

/**
 * 
 * @author dupke
 * @created 19.11.2013
 */
public class DefiPageRatedEventListener implements EventListener {

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		ArrayList<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>(1);
		events.add(DefiPageRatedEvent.class);
		events.add(DefiPageRateClosedEvent.class);

		return events;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof DefiPageRatedEvent) {
			DefiPageRateEventLogger.logEvent((DefiPageRatedEvent) event);
		} else if (event instanceof DefiPageRateClosedEvent) {
			DefiPageRateEventLogger.closeLogline((DefiPageRateClosedEvent) event);
		}
	}

}