package de.knowwe.ophtovisD3;

import java.util.ArrayList;
import java.util.Collection;

import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.ontology.compile.OntologyCompilerFinishedEvent;

public class DatabaseChangeListener implements EventListener {

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		ArrayList<Class<? extends Event>> events = new ArrayList<Class<? extends
				Event>>(1);
		events.add(OntologyCompilerFinishedEvent.class);
		return events;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof OntologyCompilerFinishedEvent) {
			OntologyCompilerFinishedEvent e = (OntologyCompilerFinishedEvent) event;
			System.out.println(e.toString());
			GraphBuilder.treeIsHere = false;
			GraphBuilder.buildGraphExperimental("unterkonzept", "");
		}

	}

}
