/*
 * Copyright (C) 2010 denkbares GmbH, Wuerzburg
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
package de.d3web.we.ci4ke.groovy;

import java.util.ArrayList;
import java.util.Collection;

import de.d3web.we.ci4ke.testing.DynamicCITestManager;
import de.d3web.we.event.Event;
import de.d3web.we.event.EventListener;
import de.d3web.we.event.InitEvent;

/**
 * Registers the {@link GroovyDynamicCITestHandler} in the
 * {@link DynamicCITestManager} on system startup (aka {@link InitEvent}).
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 22.11.2010
 */
public class RegisterGroovyCITestHandlerOnInitEvent implements EventListener {

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		ArrayList<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>(1);
		events.add(InitEvent.class);
		return events;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof InitEvent) {
			// Ensure that the GroovyDynamicCITestHandler is registered:
			DynamicCITestManager.INSTANCE.registerDynamicCITestHandler(
					GroovyDynamicCITestHandler.INSTANCE);
		}
	}

}
