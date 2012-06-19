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

	public void addSession(Session session, boolean commitAfterPropagation) {
		Set<Statement> statements = new HashSet<Statement>();
		Rdf2GoCore core = Rdf2GoCore.getInstance();
		URI sessionIdURI = D3webRdf2GoURIs.getSessionIdURI(session);
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
			addFactAsStatements(session, valuedObject, valueFact.getValue());
		}

		// add statements to the core
		core.addStatements(statements);

		// add propagation listener update the Rdf2GoCore with the changes in
		// the session
		session.getPropagationManager().addListener(
				new Rdf2GoPropagationListener(commitAfterPropagation));

	}

	public void addFactAsStatements(Session session, TerminologyObject terminologyObject, Value value) {
		Rdf2GoCore core = Rdf2GoCore.getInstance();

		Set<Statement> factStatements = new HashSet<Statement>();

		String valueString = value.getValue().toString();

		BlankNode factNode = core.createBlankNode();

		// blank node (Fact) rdf:type lns:Fact
		factStatements.add(core.createStatement(factNode, RDF.type, D3webRdf2GoURIs.getFactURI()));

		// lns:sessionID lns:hasFact blank node (Fact)
		URI sessionIdURI = D3webRdf2GoURIs.getSessionIdURI(session);
		factStatements.add(core.createStatement(sessionIdURI, D3webRdf2GoURIs.getHasFactURI(),
				factNode));

		URI toNameURI = core.createlocalURI(Strings.encodeURL(terminologyObject.getName()));

		// blank node (Fact) lns:hasTerminologyObject lns:toName
		factStatements.add(core.createStatement(factNode,
				D3webRdf2GoURIs.getHasTerminologyObjectURI(), toNameURI));

		URI valueURI = core.createlocalURI(Strings.encodeURL(valueString));

		// blank node (Fact) lns:hasValue lns:value
		factStatements.add(core.createStatement(factNode, D3webRdf2GoURIs.getHasValueURI(),
				valueURI));

		// add fact statements to cache
		getInstance().statementCache.addStatements(session, terminologyObject, factStatements);

		// add fact statements to the core
		core.addStatements(factStatements);
	}

	public void removeFactStatements(Session session, TerminologyObject terminologyObject) {
		Set<Statement> removedStatements = getInstance().statementCache.removeStatements(session,
				terminologyObject);
		Rdf2GoCore.getInstance().removeStatements(removedStatements);
	}

	public void removeSession(Session session) {
		Set<Statement> removedStatements = getInstance().statementCache.removeStatements(session);
		Rdf2GoCore.getInstance().removeStatements(removedStatements);
	}

}
