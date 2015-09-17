package de.knowwe.rdfs.d3web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.scoring.HeuristicRating;
import de.d3web.strings.Identifier;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class Rdf2GoSessionHandler {

	private final Map<String, Statement[]> statementCache = new HashMap<>();
	private Map<String, BlankNode> factNodeCache = new HashMap<>();
	private Map<Object, Resource> agentNodeCache = new HashMap<>();

	private Session session;

	private final String sessionId;
	protected final Rdf2GoCore core;
	private final boolean addProvExplanation;

	public Rdf2GoSessionHandler(Session session, Rdf2GoCore core, boolean addProvExplanation) {
		this.session = session;
		this.sessionId = session.getId();
		this.core = core;
		this.addProvExplanation = addProvExplanation;

		// we make sure prov is available as a namespace
		if (core.getNamespaces().get("prov") == null) {
			core.addNamespace("prov", "http://www.w3.org/ns/prov#");
		}
	}

	public void addSessionToRdf2GoCore() {
		ArrayList<Statement> statementsList = new ArrayList<>();
		URI sessionIdURI = Rdf2GoD3webUtils.getSessionIdURI(core, session.getId());
		URI sessionURI = core.createlocalURI(Session.class.getSimpleName());

		// lns:sessionId rdf:type lns:Session
		Rdf2GoUtils.addStatement(core, sessionIdURI, RDF.type, sessionURI, statementsList);

		Literal timeLiteral = core.createLiteral(Long.toString(session.getCreationDate().getTime()));
		URI hasCreationDateURI = core.createlocalURI("hasCreationDate");

		// lns:sessionId lns:hasCreationDate "time milliseconds"
		Rdf2GoUtils.addStatement(core, sessionIdURI, hasCreationDateURI, timeLiteral,
				statementsList);

		URI usesKnowledgeBaseURI = core.createlocalURI("usesKnowledgeBase");

		// lns:sessionId lns:usesKnowledgeBase lns:knowledgeBaseId
		String id = session.getKnowledgeBase().getId();
		if (id == null) id = "noId";
		Rdf2GoUtils.addStatement(core, sessionIdURI, usesKnowledgeBaseURI, id, statementsList);

		Statement[] statements = Rdf2GoUtils.toArray(statementsList);
		statementCache.put(null, statements);

		// add value facts already present in the session
		for (TerminologyObject valuedObject : session.getBlackboard().getValuedObjects()) {
			Fact valueFact = session.getBlackboard().getValueFact(valuedObject);
			addFactAsStatements(session, valueFact);
		}

		// add statements to the core
		synchronized (core) {
			core.addStatements(statements);
		}

		// free memory
		factNodeCache = null;
		agentNodeCache = null;
		session = null;
	}

	private void addFactAsStatements(Session session, Fact fact) {

		Collection<Statement> statementCollection = generateFactStatements(session, fact);
		Statement[] statements = Rdf2GoUtils.toArray(statementCollection);

		// add fact statementCollection to cache
		statementCache.put(fact.getTerminologyObject().getName(), statements);

		// add fact statementCollection to the core
		synchronized (core) {
			core.addStatements(statements);
		}
	}

	protected Collection<Statement> generateFactStatements(Session session, Fact fact) {
		List<Statement> statements = new ArrayList<>();

		BlankNode factNode = getFactNode(fact);

		// blank node (Fact) rdf:type lns:Fact
		Rdf2GoUtils.addStatement(core, factNode, RDF.type, Rdf2GoD3webUtils.getFactURI(core),
				statements);

		// lns:sessionID lns:hasFact blank node (Fact)
		URI sessionIdURI = Rdf2GoD3webUtils.getSessionIdURI(core, session.getId());
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
		if (addProvExplanation) {
			addProvStatements(session, fact, statements, factNode);
		}
		return statements;
	}

	private void addProvStatements(Session session, Fact fact, Collection<Statement> statements, BlankNode factNode) {
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
		BlankNode factNode = factNodeCache.get(fact.getTerminologyObject().getName());
		if (factNode == null) {
			factNode = core.createBlankNode();
			factNodeCache.put(fact.getTerminologyObject().getName(), factNode);
		}
		return factNode;
	}

	private Literal getValueLiteral(Value value) {
		if (value instanceof NumValue) {
			return Rdf2GoUtils.createDoubleLiteral(core, ((NumValue) value).getDouble());
		}
		else if (value instanceof DateValue) {
			return Rdf2GoUtils.createDateTimeLiteral(core, ((DateValue) value).getDate());
		}
		else if (value instanceof MultipleChoiceValue) {
			Collection<ChoiceID> choiceIDs = ((MultipleChoiceValue) value).getChoiceIDs();
			String[] strings = new String[choiceIDs.size()];
			int i = 0;
			for (ChoiceID choiceID : choiceIDs) {
				strings[i++] = choiceID.toString();
			}
			String parsableMCValue = Identifier.concatParsable(", ", strings);
			return core.createDatatypeLiteral(parsableMCValue, XSD._string);
		}
		else if (value instanceof HeuristicRating) {
			return Rdf2GoUtils.createDoubleLiteral(core, ((HeuristicRating) value).getScore());
		}
		else if (value instanceof Unknown) {
			return core.createDatatypeLiteral(Unknown.getInstance().getValue().toString(), XSD._string);
		}
		return core.createDatatypeLiteral(value.toString(), XSD._string);
	}

	public void removeSessionFromRdf2GoCore() {
		synchronized (core) {
			for (Statement[] statements : statementCache.values()) {
				core.removeStatements(Arrays.asList(statements));
			}
		}
	}

	public String getSessionId() {
		return this.sessionId;
	}

}
