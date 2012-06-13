package de.knowwe.rdfs.d3web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ontoware.rdf2go.model.Statement;

import de.d3web.core.session.Session;

public class D3webRdf2GoSessionManager {

	private final Map<String, Set<Statement>> statementCache = new HashMap<String, Set<Statement>>();

	private static D3webRdf2GoSessionManager instance = null;

	public static D3webRdf2GoSessionManager getInstance() {
		if (instance == null) instance = new D3webRdf2GoSessionManager();
		return instance;
	}

	private D3webRdf2GoSessionManager() {

	}

	public void addSession(Session session) {

	}

	public void removeSession(Session session) {

	}
}
