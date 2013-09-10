package de.knowwe.rdfs.d3web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ontoware.rdf2go.model.Statement;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;

public class StatementCache {

	private final Map<String, Map<String, Set<Statement>>> statementCache = new HashMap<String, Map<String, Set<Statement>>>();

	public void addStatements(Session session, Set<Statement> statements) {
		Map<String, Set<Statement>> statementSetsOfSession = getStatementSetsOfSession(session);
		Set<Statement> statementsForSession = statementSetsOfSession.get(session.getId());
		if (statementsForSession == null) {
			statementsForSession = new HashSet<Statement>();
			statementSetsOfSession.put(session.getId(), statementsForSession);
		}
		statementsForSession.addAll(statements);
	}

	public void addStatements(Session session, TerminologyObject terminologyObject, Set<Statement> statements) {
		Map<String, Set<Statement>> statementSetsOfSession = getStatementSetsOfSession(session);
		Set<Statement> statementsForTO = statementSetsOfSession.get(terminologyObject.getName());
		if (statementsForTO == null) {
			statementsForTO = new HashSet<Statement>();
			statementSetsOfSession.put(terminologyObject.getName(), statementsForTO);
		}
		statementsForTO.addAll(statements);

	}

	public Set<Statement> removeStatements(Session session) {
		Map<String, Set<Statement>> statementSetsOfSession = getStatementSetsOfSession(session);
		Set<Statement> removedStatements = new HashSet<Statement>();
		for (Set<Statement> statementSet : statementSetsOfSession.values()) {
			removedStatements.addAll(statementSet);
		}
		statementCache.remove(session.getId());
		return removedStatements;
	}

	public Set<Statement> removeStatements(Session session, TerminologyObject terminologyObject) {
		Map<String, Set<Statement>> statementSetsOfSession = getStatementSetsOfSession(session);
		return statementSetsOfSession.remove(terminologyObject.getName());
	}

	private Map<String, Set<Statement>> getStatementSetsOfSession(Session session) {
		Map<String, Set<Statement>> sessionStatements = statementCache.get(session.getId());
		if (sessionStatements == null) {
			sessionStatements = new HashMap<String, Set<Statement>>();
			statementCache.put(session.getId(), sessionStatements);
		}
		return sessionStatements;
	}
}
