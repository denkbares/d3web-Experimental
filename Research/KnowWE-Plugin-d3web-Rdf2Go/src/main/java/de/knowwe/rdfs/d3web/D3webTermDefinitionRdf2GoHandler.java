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
import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.d3web.we.object.D3webTermDefinition;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class D3webTermDefinitionRdf2GoHandler extends SubtreeHandler<D3webTermDefinition<NamedObject>> {

	@Override
	public Collection<Message> create(Article article, Section<D3webTermDefinition<NamedObject>> section) {

		if (section.hasErrorInSubtree()) return Messages.noMessage();

		Identifier termIdentifier = section.get().getTermIdentifier(section);
		String externalForm = Rdf2GoUtils.getCleanedExternalForm(termIdentifier);
		URI termIdentifierURI = Rdf2GoCore.getInstance().createlocalURI(
				externalForm);

		Class<?> termObjectClass = section.get().getTermObjectClass(section);

		// URI hasInstanceURI =
		// Rdf2GoCore.getInstance().createlocalURI("hasInstance");

		List<Statement> statements = new ArrayList<Statement>();

		// lns:TermIdentifier rdf:type lns:TermObjectClass
		Rdf2GoUtils.addStatement(termIdentifierURI, RDF.type, termObjectClass.getSimpleName(),
				statements);

		// lns:TermIdentifier rdf:subclassOf lns:parentTermIdentifier
		NamedObject termObject = section.get().getTermObject(article, section);
		if (termObject instanceof TerminologyObject) {
			TerminologyObject[] parents = ((TerminologyObject) termObject).getParents();
			for (TerminologyObject parent : parents) {
				String parentExternalForm = Rdf2GoUtils.getCleanedExternalForm(new Identifier(
						parent.getName()));
				Rdf2GoUtils.addStatement(termIdentifierURI,
						RDFS.subClassOf,
						parentExternalForm, statements);
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
		Rdf2GoUtils.addStatement(kbNameURI, D3webRdf2GoURIs.getHasTerminologyObjectURI(),
				externalForm, statements);

		// lns:TermIdentifier lns:isTerminologyObjectOf lns:KbName
		// statements.add(Rdf2GoCore.getInstance().createStatement(termIdentifierURI,
		// isTerminologyObjectOfURI,
		// kbNameURI));

		Rdf2GoCore.getInstance().addStatements(article, Rdf2GoUtils.toArray(statements));

		return Messages.noMessage();
	}

}
