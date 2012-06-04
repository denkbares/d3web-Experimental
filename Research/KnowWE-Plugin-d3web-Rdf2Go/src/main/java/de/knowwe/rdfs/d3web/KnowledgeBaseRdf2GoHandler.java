package de.knowwe.rdfs.d3web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.d3web.we.object.D3webTermDefinition;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;

public class KnowledgeBaseRdf2GoHandler extends SubtreeHandler<D3webTermDefinition<?>> {

	@Override
	public Collection<Message> create(Article article, Section<D3webTermDefinition<?>> section) {

		if (section.hasErrorInSubtree()) return Messages.noMessage();

		String kbName = D3webUtils.getKnowledgeBase(article).getId();

		URI articleNameURI = Rdf2GoCore.getInstance().createlocalURI(
				Strings.encodeURL(section.getTitle()));
		URI hasInstanceURI = Rdf2GoCore.getInstance().createlocalURI("hasInstance");
		URI articleURI = Rdf2GoCore.getInstance().createlocalURI("Article");

		URI kbNameURI = Rdf2GoCore.getInstance().createlocalURI(Strings.encodeURL(kbName));
		URI definesURI = Rdf2GoCore.getInstance().createlocalURI(
				"defines");

		URI isDefinedByURI = Rdf2GoCore.getInstance().createlocalURI(
				"isDefinedBy");

		List<Statement> statements = new ArrayList<Statement>();

		// Subject: lns:ArticleName Predicate: lns:defines Object:
		// lns:KbName
		statements.add(Rdf2GoCore.getInstance().createStatement(
				articleNameURI, definesURI, kbNameURI));

		// Subject: lns:KbName Predicate: lns:isDefinedByURI Object:
		// lns:ArticleName
		statements.add(Rdf2GoCore.getInstance().createStatement(kbNameURI,
				isDefinedByURI, articleNameURI));

		// Subject: lns:ArticleName Predicate: rdf:type Object:
		// lns:Article
		statements.add(Rdf2GoCore.getInstance().createStatement(articleNameURI,
				RDF.type, articleURI));

		// Subject: lns:ArticleName Predicate: lns:hasInstance Object:
		// lns:Article
		statements.add(Rdf2GoCore.getInstance().createStatement(articleURI,
				hasInstanceURI, articleNameURI));

		Rdf2GoCore.getInstance().addStatements(statements, section);
		return Messages.noMessage();
	}

}
