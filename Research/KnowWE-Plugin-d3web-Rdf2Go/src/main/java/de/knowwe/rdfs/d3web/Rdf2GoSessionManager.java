package de.knowwe.rdfs.d3web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class Rdf2GoSessionManager {

	private final Map<TerminologyObject, Set<Statement>> statementCache = new HashMap<TerminologyObject, Set<Statement>>();

	private final Rdf2GoCore core;

	public Rdf2GoSessionManager(Rdf2GoCore core) {
		this.core = core;
	}

	public void commit() {
		this.core.commit();
	}

	public void addSession(Session session, boolean commitAfterPropagation) {
		Set<Statement> statements = new HashSet<Statement>();
		URI sessionIdURI = Rdf2GoD3webUtils.getSessionIdURI(core, session);
		URI sessionURI = core.createlocalURI(Session.class.getSimpleName());

		// lns:sessionId rdf:type lns:Session
		Rdf2GoUtils.addStatement(core, sessionIdURI, RDF.type, sessionURI, statements);

		Literal timeLiteral = core.createLiteral(Long.toString(session.getCreationDate().getTime()));
		URI hasCreationDateURI = core.createlocalURI("hasCreationDate");

		// lns:sessionId lns:hasCreationDate "time milliseconds"
		Rdf2GoUtils.addStatement(core, sessionIdURI, hasCreationDateURI, timeLiteral,
				statements);

		URI usesKnowledgeBaseURI = core.createlocalURI("usesKnowledgeBase");

		// lns:sessionId lns:usesKnowledgeBase lns:knowledgeBaseId
		Rdf2GoUtils.addStatement(core, sessionIdURI,
				usesKnowledgeBaseURI, session.getKnowledgeBase().getId(), statements);

		statementCache.put(null, statements);

		// add value facts already present in the session
		for (TerminologyObject valuedObject : session.getBlackboard().getValuedObjects()) {
			Fact valueFact = session.getBlackboard().getValueFact(valuedObject);
			addFactAsStatements(session, valuedObject, valueFact.getValue());
		}

		// add statements to the core
		core.addStatements(Rdf2GoUtils.toArray(statements));

		// add propagation listener update the Rdf2GoCore with the changes in
		// the session
		session.getPropagationManager().addListener(
				new Rdf2GoPropagationListener(this, commitAfterPropagation));

	}

	public void addFactAsStatements(Session session, TerminologyObject terminologyObject, Value value) {

		Set<Statement> statements = new HashSet<Statement>();

		BlankNode factNode = core.createBlankNode();

		// blank node (Fact) rdf:type lns:Fact
		Rdf2GoUtils.addStatement(core, factNode, RDF.type, Rdf2GoD3webUtils.getFactURI(core),
				statements);

		// lns:sessionID lns:hasFact blank node (Fact)
		URI sessionIdURI = Rdf2GoD3webUtils.getSessionIdURI(core, session);
		Rdf2GoUtils.addStatement(core, sessionIdURI, Rdf2GoD3webUtils.getHasFactURI(core),
				factNode, statements);

		// blank node (Fact) lns:hasTerminologyObject lns:toName
		Rdf2GoUtils.addStatement(core, factNode,
				Rdf2GoD3webUtils.getHasTerminologyObjectURI(core), terminologyObject.getName(),
				statements);

		Node valueNode = getValueNode(value);

		// blank node (Fact) lns:hasValue lns:value
		Rdf2GoUtils.addStatement(core, factNode,
				Rdf2GoD3webUtils.getHasValueURI(core), valueNode, statements);

		// add fact statements to cache
		statementCache.put(terminologyObject, statements);

		// add fact statements to the core
		core.addStatements(Rdf2GoUtils.toArray(statements));
	}

	private Node getValueNode(Value value) {
		Object object = value.getValue();
		if (object instanceof Integer) {
			return core.createDatatypeLiteral(object.toString(), XSD._integer);
		}
		else if (object instanceof Double) {
			return core.createDatatypeLiteral(object.toString(), XSD._double);
		}
		else if (object instanceof Float) {
			return core.createDatatypeLiteral(object.toString(), XSD._float);
		}
		else if (object instanceof Long) {
			return core.createDatatypeLiteral(object.toString(), XSD._long);
		}
		return core.createDatatypeLiteral(object.toString(), XSD._string);
	}

	public void removeFactStatements(Session session, TerminologyObject terminologyObject) {
		core.removeStatements(statementCache.get(terminologyObject));
	}

	public void removeSession(Session session) {
		for (Set<Statement> set : statementCache.values()) {
			core.removeStatements(set);
		}
	}

}
