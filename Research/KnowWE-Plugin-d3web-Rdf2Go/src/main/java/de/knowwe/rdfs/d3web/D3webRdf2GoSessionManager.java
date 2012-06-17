package de.knowwe.rdfs.d3web;

import java.util.HashSet;
import java.util.Set;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;

public class D3webRdf2GoSessionManager {

	private final StatementCache statementCache = new StatementCache();

	private static D3webRdf2GoSessionManager instance = null;

	public static D3webRdf2GoSessionManager getInstance() {
		if (instance == null) instance = new D3webRdf2GoSessionManager();
		return instance;
	}

	private D3webRdf2GoSessionManager() {

	}

	public void addSession(Session session) {
		Set<Statement> statements = new HashSet<Statement>();
		Rdf2GoCore core = Rdf2GoCore.getInstance();
		URI sessionIdURI = core.createlocalURI(
				Strings.encodeURL(session.getId()));
		URI sessionURI = core.createlocalURI(Session.class.getSimpleName());

		// lns:sessionId rdf:type lns:Session
		statements.add(core.createStatement(sessionIdURI, RDF.type, sessionURI));

		Literal timeLiteral = core.createLiteral(Long.toString(session.getCreationDate().getTime()));
		URI hasCreationDateURI = core.createlocalURI("hasCreationDate");

		// lns:sessionId lns:hasCreationDate "time milliseconds"
		statements.add(core.createStatement(sessionIdURI, hasCreationDateURI, timeLiteral));

		URI usesKnowledgeBaseURI = core.createlocalURI("usesKnowledgeBase");
		URI knowledgeBaseIdURI = core.createlocalURI(session.getKnowledgeBase().getId());

		// lns:sessionId lns:usesKnowledgeBase lns:knowledgeBaseId
		statements.add(core.createStatement(sessionIdURI, usesKnowledgeBaseURI, knowledgeBaseIdURI));

		statementCache.addStatements(session, statements);

		// add value facts already present in the session
		for (TerminologyObject valuedObject : session.getBlackboard().getValuedObjects()) {
			Fact valueFact = session.getBlackboard().getValueFact(valuedObject);
			Value value = valueFact.getValue();
			BlankNode factNode = core.createBlankNode();

		}

		// add statements to the core
		core.addStatements(statements);

		// add propagation listener update the Rdf2GoCore with the changes in
		// the session
		session.getPropagationManager().addListener(new Rdf2GoPropagationListener());

	}

	public void removeSession(Session session) {

	}

}
