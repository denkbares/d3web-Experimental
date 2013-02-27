package de.knowwe.ontology.kdom.clazz;

import java.util.Collection;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.ontology.kdom.individual.NamespaceIndividualReference;
import de.knowwe.rdf2go.Rdf2GoCore;

public class ClassDefinition extends AbstractType {

	public ClassDefinition() {
		this.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(new NamespaceIndividualReference());
		this.addSubtreeHandler(Priority.LOW, new ClassHandler());
	}

	private class ClassHandler extends SubtreeHandler<ClassDefinition> {

		@Override
		public Collection<Message> create(Article article, Section<ClassDefinition> section) {
			if (section.hasErrorInSubtree()) return Messages.noMessage();

			Rdf2GoCore core = Rdf2GoCore.getInstance(article);

			Section<NamespaceIndividualReference> individualSection = Sections.findChildOfType(
					section, NamespaceIndividualReference.class);

			String individualName = individualSection.get().getIndividual(individualSection);
			String abbreviation = individualSection.get().getAbbreviation(individualSection);
			String namespace = core.getNameSpaces().get(abbreviation);

			URI individualURI = core.createURI(namespace, individualName);

			Statement classStatement = core.createStatement(individualURI, RDF.type, RDFS.Class);
			core.addStatements(classStatement);

			return Messages.noMessage();
		}

	}

}
