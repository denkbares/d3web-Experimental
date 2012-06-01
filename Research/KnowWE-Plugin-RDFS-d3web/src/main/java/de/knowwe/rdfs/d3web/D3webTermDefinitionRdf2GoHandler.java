package de.knowwe.rdfs.d3web;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.d3web.we.object.D3webTermDefinition;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.rdf2go.Rdf2GoCore;

public class D3webTermDefinitionRdf2GoHandler extends SubtreeHandler<D3webTermDefinition<?>> {

	@Override
	public Collection<Message> create(Article article, Section<D3webTermDefinition<?>> section) {

		TermIdentifier termIdentifier = section.get().getTermIdentifier(section);
		URI termIdentifierURI = Rdf2GoCore.getInstance().createlocalURI(
				termIdentifier.toExternalForm());

		Class<?> termObjectClass = section.get().getTermObjectClass(section);
		URI termObjectClassURI = Rdf2GoCore.getInstance().createlocalURI(
				termObjectClass.getSimpleName());

		Rdf2GoCore.getInstance().addStatement(
				Rdf2GoCore.getInstance().createStatement(termIdentifierURI, RDF.type,
						termObjectClassURI), section);

		return Messages.noMessage();
	}

}
