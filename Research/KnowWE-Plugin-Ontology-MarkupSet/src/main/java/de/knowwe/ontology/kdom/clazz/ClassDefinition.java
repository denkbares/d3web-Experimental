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
import de.knowwe.ontology.kdom.individual.IndividualReference;
import de.knowwe.ontology.kdom.namespace.AbbreviationPrefixReference;
import de.knowwe.ontology.kdom.namespace.AbbreviationReference;
import de.knowwe.rdf2go.Rdf2GoCore;

public class ClassDefinition extends AbstractType {

	public ClassDefinition() {
		this.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(new AbbreviationPrefixReference());
		this.addChildType(new IndividualReference());
		this.addSubtreeHandler(Priority.LOW, new ClassHandler());
	}

	private class ClassHandler extends SubtreeHandler<ClassDefinition> {

		@Override
		public Collection<Message> create(Article article, Section<ClassDefinition> section) {
			if (section.hasErrorInSubtree()) return Messages.noMessage();
			Rdf2GoCore core = Rdf2GoCore.getInstance(article);

			Section<IndividualReference> individualSection = Sections.findChildOfType(section,
					IndividualReference.class);
			String individualName = individualSection.get().getTermName(individualSection);

			Section<AbbreviationReference> abbreviationSection = Sections.findSuccessor(section,
					AbbreviationReference.class);

			String namespace = getNamespace(core, abbreviationSection);
			URI individualURI = core.createURI(namespace, individualName);

			Statement classStatement = core.createStatement(individualURI, RDF.type, RDFS.Class);
			core.addStatements(article, classStatement);

			return Messages.noMessage();
		}

		private String getNamespace(Rdf2GoCore core, Section<AbbreviationReference> abbreviationSection) {
			if (abbreviationSection == null) {
				return core.getLocalNamespace();
			}
			else {
				return core.getNameSpaces().get(
						abbreviationSection.get().getTermName(abbreviationSection));
			}

		}
	}

}
