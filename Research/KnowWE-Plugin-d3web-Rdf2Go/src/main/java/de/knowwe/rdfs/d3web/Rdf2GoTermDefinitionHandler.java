package de.knowwe.rdfs.d3web;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.strings.Identifier;
import de.d3web.we.object.D3webTermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.CompilerMessage;
import de.knowwe.ontology.compile.OntologyCompileScript;
import de.knowwe.ontology.compile.OntologyCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class Rdf2GoTermDefinitionHandler extends OntologyCompileScript<D3webTermDefinition<NamedObject>> {

	@Override
	public void compile(OntologyCompiler compiler, Section<D3webTermDefinition<NamedObject>> section) throws CompilerMessage {

		Identifier termIdentifier = section.get().getTermIdentifier(section);

		compiler.getTerminologyManager().registerTermDefinition(
				compiler, section, section.get().getTermObjectClass(section), termIdentifier);

		String externalForm = Rdf2GoUtils.getCleanedExternalForm(termIdentifier);

		Rdf2GoCore core = compiler.getRdf2GoCore();
		URI termIdentifierURI = core.createlocalURI(externalForm);

		Class<?> termObjectClass = section.get().getTermObjectClass(section);

		// URI hasInstanceURI =
		// Rdf2GoCore.getInstance().createlocalURI("hasInstance");

		List<Statement> statements = new ArrayList<Statement>();

		// lns:TermIdentifier rdf:type lns:TermObjectClass
		Rdf2GoUtils.addStatement(core, termIdentifierURI, RDF.type,
				termObjectClass.getSimpleName(), statements);

		core.addStatements(section, Rdf2GoUtils.toArray(statements));
	}

	@Override
	public void destroy(OntologyCompiler compiler, Section<D3webTermDefinition<NamedObject>> section) {
		compiler.getRdf2GoCore().removeStatementsForSection(section);
		Identifier termIdentifier = section.get().getTermIdentifier(section);
		compiler.getTerminologyManager().unregisterTermDefinition(
				compiler, section, section.get().getTermObjectClass(section), termIdentifier);
	}

}
