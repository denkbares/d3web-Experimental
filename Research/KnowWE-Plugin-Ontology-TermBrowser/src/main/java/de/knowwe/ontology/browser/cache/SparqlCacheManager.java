package de.knowwe.ontology.browser.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.event.EventManager;
import de.knowwe.rdf2go.InsertStatementsEvent;
import de.knowwe.rdf2go.ModifiedCoreDataEvent;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.Rdf2GoCoreDestroyEvent;
import de.knowwe.rdf2go.RemoveStatementsEvent;


public class SparqlCacheManager implements EventListener {

	private final Map<Rdf2GoCore, SparqlCache> caches = new HashMap<Rdf2GoCore, SparqlCache>();

	private static SparqlCacheManager instance = null;

	public static SparqlCacheManager getInstance() {
		if (instance == null) {
			instance = new SparqlCacheManager();
		}
		return instance;
	}

	private SparqlCacheManager() {
		EventManager.getInstance().registerListener(this);
	}

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		Collection<Class<? extends Event>> eventTypes = new ArrayList<Class<? extends Event>>();
		eventTypes.add(RemoveStatementsEvent.class);
		eventTypes.add(InsertStatementsEvent.class);
		eventTypes.add(Rdf2GoCoreDestroyEvent.class);
		return eventTypes;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof Rdf2GoCoreDestroyEvent) {
			Rdf2GoCore rdf2GoCore = ((Rdf2GoCoreDestroyEvent) event).getRdf2GoCore();
			this.caches.remove(rdf2GoCore);
			return;
		}
		Rdf2GoCore core = ((ModifiedCoreDataEvent) event).getCore();
		SparqlCache sparqlCache = caches.get(core);
		if (sparqlCache != null) {
			sparqlCache.clear();
		}
	}

	public SparqlCache getCachedSparqlEndpoint(Rdf2GoCore core) {
		SparqlCache sparqlCache = caches.get(core);
		if (sparqlCache == null) {
			sparqlCache = new SparqlCache(core);
			caches.put(core, sparqlCache);
		}
		return sparqlCache;
	}
}
