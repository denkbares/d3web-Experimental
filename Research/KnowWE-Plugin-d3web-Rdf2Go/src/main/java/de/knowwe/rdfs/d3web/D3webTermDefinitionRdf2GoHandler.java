package de.knowwe.rdfs.d3web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.strings.Strings;
import de.d3web.strings.Identifier;
import de.d3web.we.object.D3webTerm;
import de.d3web.we.object.D3webTermDefinition;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class D3webTermDefinitionRdf2GoHandler extends SubtreeHandler<D3webTermDefinition<?>> {

	@Override
	public Collection<Message> create(Article article, Section<D3webTermDefinition<?>> section) {

		if (section.hasErrorInSubtree()) return Messages.noMessage();

		Identifier termIdentifier = section.get().getTermIdentifier(section);
		URI termIdentifierURI = Rdf2GoCore.getInstance().createlocalURI(
				Strings.encodeURL(termIdentifier.toExternalForm()));

		Class<?> termObjectClass = section.get().getTermObjectClass(section);
		URI termObjectClassURI = Rdf2GoCore.getInstance().createlocalURI(
				termObjectClass.getSimpleName());

		// URI hasInstanceURI =
		// Rdf2GoCore.getInstance().createlocalURI("hasInstance");

		List<Statement> statements = new ArrayList<Statement>();

		// lns:TermIdentifier rdf:type lns:TermObjectClass
		statements.add(Rdf2GoCore.getInstance().createStatement(termIdentifierURI, RDF.type,
				termObjectClassURI));

		// lns:TermIdentifier rdf:subclassOf lns:parentTermIdentifier
		Section<?> termDefiningSection = KnowWEUtils.getTerminologyManager(article).getTermDefiningSection(
				termIdentifier);
		if (termDefiningSection.get() instanceof D3webTerm) {
			@SuppressWarnings("rawtypes")
			Section<D3webTerm> d3webTermSection = Sections.cast(termDefiningSection,
					D3webTerm.class);
			@SuppressWarnings("unchecked")
			NamedObject termObject = d3webTermSection.get().getTermObject(article, d3webTermSection);
			if (termObject instanceof TerminologyObject) {
				TerminologyObject[] parents = ((TerminologyObject) termObject).getParents();
				for (TerminologyObject parent : parents) {
					String externalForm = new Identifier(parent.getName()).toExternalForm();
					URI parentTermidentifierURI = Rdf2GoCore.getInstance().createlocalURI(
							Strings.encodeURL(externalForm));
					statements.add(Rdf2GoCore.getInstance().createStatement(termIdentifierURI,
							RDFS.subClassOf,
							parentTermidentifierURI));
				}
			}
		}

		// lns:TermObjectClass lns:hasInstance lns:TermIdentifier
		// statements.add(Rdf2GoCore.getInstance().createStatement(termObjectClassURI,
		// hasInstanceURI,
		// termIdentifierURI));

		String kbName = D3webUtils.getKnowledgeBase(article).getId();

		URI kbNameURI = Rdf2GoCore.getInstance().createlocalURI(Strings.encodeURL(kbName));

		// URI isTerminologyObjectOfURI =
		// Rdf2GoCore.getInstance().createlocalURI(
		// "isTerminologyObjectOf");

		// lns:KbName ln:hasTerminologyObject lns:TermIdentifier
		statements.add(Rdf2GoCore.getInstance().createStatement(kbNameURI,
				D3webRdf2GoURIs.getHasTerminologyObjectURI(),
				termIdentifierURI));

		// lns:TermIdentifier lns:isTerminologyObjectOf lns:KbName
		// statements.add(Rdf2GoCore.getInstance().createStatement(termIdentifierURI,
		// isTerminologyObjectOfURI,
		// kbNameURI));

		Rdf2GoCore.getInstance().addStatements(article, Rdf2GoUtils.toArray(statements));

		return Messages.noMessage();
	}
}
