package de.knowwe.rdfs.d3web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.strings.Strings;
import de.d3web.we.knowledgebase.D3webCompiler;
import de.d3web.we.object.D3webTermDefinition;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.ontology.compile.OntologyCompiler;
import de.knowwe.ontology.compile.OntologyHandler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class Rdf2GoTermDefinitionHandler extends OntologyHandler<D3webTermDefinition<NamedObject>> {

	@Override
	public Collection<Message> create(OntologyCompiler compiler, Section<D3webTermDefinition<NamedObject>> section) {
		if (section.hasErrorInSubtree()) return Messages.noMessage();

		Collection<D3webCompiler> compilers = Compilers.getCompilers(section, D3webCompiler.class);
		if (compilers.isEmpty()) {
			return Messages.noMessage();
		}
		D3webCompiler d3webCompiler = compilers.iterator().next();

		NamedObject namedObject = section.get().getTermObject(d3webCompiler, section);
		String externalForm = Rdf2GoD3webUtils.getIdentifierExternalForm(namedObject);

		Rdf2GoCore core = compiler.getRdf2GoCore();
		URI termIdentifierURI = core.createlocalURI(
				externalForm);

		Class<?> termObjectClass = section.get().getTermObjectClass(section);

		// URI hasInstanceURI =
		// Rdf2GoCore.getInstance().createlocalURI("hasInstance");

		List<Statement> statements = new ArrayList<Statement>();

		// lns:TermIdentifier rdf:type lns:TermObjectClass
		Rdf2GoUtils.addStatement(core, termIdentifierURI, RDF.type,
				termObjectClass.getSimpleName(), statements);

		// lns:TermIdentifier rdf:subclassOf lns:parentTermIdentifier
		TerminologyObject[] parents = new TerminologyObject[0];
		if (namedObject instanceof TerminologyObject) {
			parents = ((TerminologyObject) namedObject).getParents();
		}
		else if (namedObject instanceof Choice) {
			parents = new TerminologyObject[] { ((Choice) namedObject).getQuestion() };
		}
		for (TerminologyObject parent : parents) {
			String parentExternalForm = Rdf2GoD3webUtils.getIdentifierExternalForm(parent);
			Rdf2GoUtils.addStatement(core, termIdentifierURI, RDFS.subClassOf,
					parentExternalForm, statements);
		}

		// lns:TermObjectClass lns:hasInstance lns:TermIdentifier
		// statements.add(Rdf2GoCore.getInstance().createStatement(termObjectClassURI,
		// hasInstanceURI,
		// termIdentifierURI));

		String kbName = D3webUtils.getKnowledgeBase(section).getId();

		URI kbNameURI = core.createlocalURI(Strings.encodeURL(kbName));

		// URI isTerminologyObjectOfURI =
		// Rdf2GoCore.getInstance().createlocalURI(
		// "isTerminologyObjectOf");

		// lns:KbName ln:hasTerminologyObject lns:TermIdentifier
		Rdf2GoUtils.addStatement(core, kbNameURI,
				Rdf2GoD3webUtils.getHasTerminologyObjectURI(core), externalForm, statements);

		// lns:TermIdentifier lns:isTerminologyObjectOf lns:KbName
		// statements.add(Rdf2GoCore.getInstance().createStatement(termIdentifierURI,
		// isTerminologyObjectOfURI,
		// kbNameURI));

		core.addStatements(section, Rdf2GoUtils.toArray(statements));

		if (compilers.size() > 1) {
			return Messages.asList(Messages.warning("'"
					+ namedObject
					+ "' is used in multiple knowledge bases, ontology was only created"
					+ " for the version of knowledge base '" + kbName + "'"));
		}
		return Messages.noMessage();
	}

	@Override
	public void destroy(OntologyCompiler compiler, Section<D3webTermDefinition<NamedObject>> section) {
		compiler.getRdf2GoCore().removeStatementsForSection(section);
	}

}
