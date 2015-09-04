package de.knowwe.rdfs.d3web;

import java.util.ArrayList;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.d3web.core.knowledge.terminology.NamedObject;
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

		URI termIdentifierURI = Rdf2GoD3webUtils.registerTermDefinition(compiler, section);
		Class<?> termObjectClass = section.get().getTermObjectClass(section);

		Rdf2GoCore core = compiler.getRdf2GoCore();
		List<Statement> statements = new ArrayList<>();

		// lns:TermIdentifier rdf:type lns:TermObjectClass
		Rdf2GoUtils.addStatement(core, termIdentifierURI, RDF.type, termObjectClass.getSimpleName(), statements);

		String termName = section.get().getTermName(section);
		Rdf2GoUtils.addStatement(core, termIdentifierURI, RDFS.label, core.createLiteral(termName), statements);

		core.addStatements(section, Rdf2GoUtils.toArray(statements));
	}

	@Override
	public void destroy(OntologyCompiler compiler, Section<D3webTermDefinition<NamedObject>> section) {
		compiler.getRdf2GoCore().removeStatements(section);
		Rdf2GoD3webUtils.unregisterTermDefinition(compiler, section);
	}

}
