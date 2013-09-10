/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.knowwe.onte.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;

/**
 *
 *
 * @author Stefan Mark
 * @created 17.10.2011
 */
public class OWLApiAxiomCacheEventListener implements EventListener {

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		List<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>(1);
		events.add(OWLApiAxiomCacheUpdateEvent.class);
		return events;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof OWLApiAxiomCacheUpdateEvent) {
			OWLApiAxiomCacheUpdateEvent e = (OWLApiAxiomCacheUpdateEvent) event;
			OWLApiAxiomCache.getInstance().addToCache(e.getSection(), e.getAxiom());
		}
	}
}
