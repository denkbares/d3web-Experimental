package de.knowwe.rdfs.d3web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ontoware.rdf2go.model.Statement;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;

public class StatementCache {

	private final Map<String, Map<String, Statement>> statementCache = new HashMap<String, Map<String, Statement>>();

	public void addStatements(Session session, Set<Statement> statements) {
		Map<String, Statement> statementsOfSession = getStatementOfSession(session);
		for (Statement statement : statements) {
			statementsOfSession.put(session.getId(), statement);
		}
	}

	public void addStatements(Session session, TerminologyObject terminologyObject, Set<Statement> statements) {
		Map<String, Statement> statementsOfSession = getStatementOfSession(session);
		for (Statement statement : statements) {
			statementsOfSession.put(terminologyObject.getName(), statement);
		}
	}

	private Map<String, Statement> getStatementOfSession(Session session) {
		Map<String, Statement> sessionStatements = statementCache.get(session.getId());
		if (sessionStatements == null) {
			sessionStatements = new HashMap<String, Statement>();
			statementCache.put(session.getId(), sessionStatements);
		}
		return sessionStatements;
	}
}
