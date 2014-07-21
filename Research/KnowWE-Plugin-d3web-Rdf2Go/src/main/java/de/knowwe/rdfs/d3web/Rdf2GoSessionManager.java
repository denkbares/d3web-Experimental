package de.knowwe.rdfs.d3web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class Rdf2GoSessionManager {

	private final Map<TerminologyObject, Set<Statement>> statementCache = new HashMap<TerminologyObject, Set<Statement>>();
	private final Map<TerminologyObject, BlankNode> factNodeCache = new HashMap<TerminologyObject, BlankNode>();
	private final Map<Object, Resource> agentNodeCache = new HashMap<Object, Resource>();

	protected final Rdf2GoCore core;

	public Rdf2GoSessionManager(Rdf2GoCore core) {
		this.core = core;

		// we make sure prov is available as a namespace
		if (core.getNamespaces().get("prov") == null) {
			core.addNamespace("prov", "http://www.w3.org/ns/prov#");
		}
	}

	public void addSessionToRdf2GoCore(Session session) {
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
		String id = session.getKnowledgeBase().getId();
		if (id == null) id = "noId";
		Rdf2GoUtils.addStatement(core, sessionIdURI, usesKnowledgeBaseURI, id, statements);

		statementCache.put(null, statements);

		// add value facts already present in the session
		for (TerminologyObject valuedObject : session.getBlackboard().getValuedObjects()) {
			Fact valueFact = session.getBlackboard().getValueFact(valuedObject);
			addFactAsStatements(session, valueFact);
		}

		// add statements to the core
		core.addStatements(Rdf2GoUtils.toArray(statements));
	}

	public void addFactAsStatements(Session session, Fact fact) {

		Set<Statement> statements = generateFactStatements(session, fact);

		// add fact statements to cache
		statementCache.put(fact.getTerminologyObject(), statements);

		// add fact statements to the core
		core.addStatements(Rdf2GoUtils.toArray(statements));
	}

	protected Set<Statement> generateFactStatements(Session session, Fact fact) {
		Set<Statement> statements = new HashSet<Statement>();

		BlankNode factNode = getFactNode(fact);

		// blank node (Fact) rdf:type lns:Fact
		Rdf2GoUtils.addStatement(core, factNode, RDF.type, Rdf2GoD3webUtils.getFactURI(core),
				statements);

		// lns:sessionID lns:hasFact blank node (Fact)
		URI sessionIdURI = Rdf2GoD3webUtils.getSessionIdURI(core, session);
		Rdf2GoUtils.addStatement(core, sessionIdURI, Rdf2GoD3webUtils.getHasFactURI(core),
				factNode, statements);

		// blank node (Fact) lns:hasTerminologyObject lns:ObjectName
		Rdf2GoUtils.addStatement(core, factNode,
				Rdf2GoD3webUtils.getHasTerminologyObjectURI(core), fact.getTerminologyObject().getName(),
				statements);

		Literal valueLiteral = getValueLiteral(fact.getValue());

		// blank node (Fact) lns:hasValue "value"
		Rdf2GoUtils.addStatement(core, factNode,
				Rdf2GoD3webUtils.getHasValueURI(core), valueLiteral, statements);

		// add PROV statements
		Set<TerminologyObject> activeDerivationSources = fact.getPSMethod()
				.getActiveDerivationSources(fact.getTerminologyObject(), session);

		for (TerminologyObject activeDerivationSource : activeDerivationSources) {
			Fact valueFact = session.getBlackboard().getValueFact(activeDerivationSource);
			if (valueFact == null) continue;
			BlankNode sourceFactNode = getFactNode(valueFact);
			// blank node (Fact) prov:wasDerivedFrom lns:sourceFactNode
			Rdf2GoUtils.addStatement(core, factNode, getProvURI("wasDerivedFrom"), sourceFactNode, statements);
		}

		// Resource (Fact) prov:wasAttributedTo blank node (Agent)
		Resource agentNode = getAgentNode(fact);
		Rdf2GoUtils.addStatement(core, factNode, getProvURI("wasAttributedTo"), agentNode, statements);

		// blank node (Agent) rdf:type lns:PSMethod/Section...
		Rdf2GoUtils.addStatement(core, agentNode, RDF.type, getAgentType(fact), statements);

		return statements;
	}

	private URI getAgentType(Fact fact) {
		Object source = fact.getSource();
		if (source instanceof PSMethod) {
			return core.createlocalURI(source.getClass().getSimpleName());
		}
		else if (source instanceof Section<?>) {
			return core.createlocalURI(((Section) source).get().getName());
		}
		return core.createlocalURI(source.toString());
	}

	private Resource getAgentNode(Fact fact) {
		Object source = fact.getSource();
		Resource agentNode = agentNodeCache.get(source);
		if (agentNode == null) {
			if (source instanceof Section<?>) {
				agentNode = core.createlocalURI(((Section) source).getID());
			}
			else {
				agentNode = core.createBlankNode();
			}
			agentNodeCache.put(source, agentNode);
		}
		return agentNode;
	}

	private URI getProvURI(String name) {
		return core.createURI("prov", name);
	}

	private BlankNode getFactNode(Fact fact) {
		BlankNode factNode = factNodeCache.get(fact.getTerminologyObject());
		if (factNode == null) {
			factNode = core.createBlankNode();
			factNodeCache.put(fact.getTerminologyObject(), factNode);
		}
		return factNode;
	}

	private Literal getValueLiteral(Value value) {
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

	public void removeFactStatements(TerminologyObject terminologyObject) {
		Set<Statement> statements = statementCache.get(terminologyObject);
		if (statements != null) core.removeStatements(statements);
	}

	public void removeSessionFromRdf2GoCore() {
		for (Set<Statement> set : statementCache.values()) {
			core.removeStatements(set);
		}
	}

}
