package de.knowwe.rdfs.d3web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.d3web.we.object.D3webTermDefinition;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;

public class D3webTermDefinitionRdf2GoHandler extends SubtreeHandler<D3webTermDefinition<?>> {

	@Override
	public Collection<Message> create(Article article, Section<D3webTermDefinition<?>> section) {

		if (section.hasErrorInSubtree()) return Messages.noMessage();

		TermIdentifier termIdentifier = section.get().getTermIdentifier(section);
		URI termIdentifierURI = Rdf2GoCore.getInstance().createlocalURI(
				Strings.encodeURL(termIdentifier.toExternalForm()));

		Class<?> termObjectClass = section.get().getTermObjectClass(section);
		URI termObjectClassURI = Rdf2GoCore.getInstance().createlocalURI(
				termObjectClass.getSimpleName());

		URI hasInstanceURI = Rdf2GoCore.getInstance().createlocalURI("hasInstance");

		List<Statement> statements = new ArrayList<Statement>();

		// Subject: lns:TermIdentifier Predicate: rdf:type Object:
		// lns:TermObjectClass
		statements.add(Rdf2GoCore.getInstance().createStatement(termIdentifierURI, RDF.type,
				termObjectClassURI));

		// Subject: lns:TermObjectClass Predicate: lns:hasInstance Object:
		// lns:TermIdentifier
		statements.add(Rdf2GoCore.getInstance().createStatement(termObjectClassURI, hasInstanceURI,
				termIdentifierURI));

		String kbName = D3webUtils.getKnowledgeBase(article).getId();

		URI kbNameURI = Rdf2GoCore.getInstance().createlocalURI(Strings.encodeURL(kbName));
		URI hasTerminologyObjectURI = Rdf2GoCore.getInstance().createlocalURI(
				"hasTerminologyObject");

		URI isTerminologyObjectOfURI = Rdf2GoCore.getInstance().createlocalURI(
				"isTerminologyObjectOf");

		// Subject: lns:KbName Predicate: hasTerminologyObject Object:
		// lns:TermIdentifier
		statements.add(Rdf2GoCore.getInstance().createStatement(kbNameURI, hasTerminologyObjectURI,
				termIdentifierURI));

		// Subject: lns:TermIdentifier Predicate: isTerminologyObjectOf Object:
		// lns:KbName
		statements.add(Rdf2GoCore.getInstance().createStatement(termIdentifierURI,
				isTerminologyObjectOfURI,
				kbNameURI));

		Rdf2GoCore.getInstance().addStatements(statements, section);

		return Messages.noMessage();
	}

}
