/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.owlapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.rdf2go.InsertStatementsEvent;
import de.knowwe.rdf2go.RemoveStatementsEvent;

/**
 * RDF2GoEventListener which listens to Events fired by the @link{Rdf2GoCore},
 * namely to @link{InsertStatementsEvent}s and @link{RemoveStatementsEvent}s.
 * 
 * In either case the provided statements will be translated to @link{OWLAxioms}
 * and then removed from KnowWE's global @link{OWLOntology}
 * 
 * @author Sebastian Furth
 * @created May 25, 2011
 */
public class RDF2GoEventListener implements EventListener {

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		List<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>(2);
		events.add(InsertStatementsEvent.class);
		events.add(RemoveStatementsEvent.class);
		return events;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof InsertStatementsEvent) {
			InsertStatementsEvent insertEvent = (InsertStatementsEvent) event;
			RDF2GoSync.synchronize(insertEvent.getStatements(), RDF2GoSync.Mode.ADD);
		}
		else if (event instanceof RemoveStatementsEvent) {
			RemoveStatementsEvent removeEvent = (RemoveStatementsEvent) event;
			RDF2GoSync.synchronize(removeEvent.getStatements(), RDF2GoSync.Mode.REMOVE);
		}

	}

}
