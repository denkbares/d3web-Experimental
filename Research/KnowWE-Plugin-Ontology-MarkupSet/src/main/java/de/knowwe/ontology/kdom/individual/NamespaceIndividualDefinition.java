package de.knowwe.ontology.kdom.individual;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.ontology.kdom.namespace.AbbreviationPrefixReference;
import de.knowwe.ontology.kdom.namespace.AbbreviationReference;
import de.knowwe.rdf2go.Rdf2GoCore;

public class NamespaceIndividualDefinition extends SimpleDefinition {

	public NamespaceIndividualDefinition() {
		super(TermRegistrationScope.LOCAL, NamespaceIndividualDefinition.class);
		this.addChildType(new AbbreviationPrefixReference());
		this.addChildType(new IndividualDefinition());
		this.addSubtreeHandler(new NamespaceIndividualHandler());
		this.setSectionFinder(new AllTextFinderTrimmed());
	}

	@Override
	public String getTermName(Section<? extends SimpleTerm> section) {
		String namespace = getAbbreviation(section);
		String individualName = getIndividual(section);
		return namespace + ":" + individualName;
	}

	private String getIndividual(Section<? extends SimpleTerm> section) {
		Section<IndividualDefinition> individualSection = Sections.findChildOfType(section,
				IndividualDefinition.class);
		String individualName = individualSection.get().getTermName(individualSection);
		return individualName;
	}

	private String getAbbreviation(Section<? extends SimpleTerm> section) {
		Section<AbbreviationPrefixReference> abbreviationPrefixSection = Sections.findChildOfType(
				section, AbbreviationPrefixReference.class);
		String abbreviation;
		if (abbreviationPrefixSection == null) {
			abbreviation = Rdf2GoCore.LNS_ABBREVIATION;
		}
		else {
			Section<AbbreviationReference> abbreviationSection = Sections.findChildOfType(
					abbreviationPrefixSection, AbbreviationReference.class);
			abbreviation = abbreviationSection.get().getTermName(abbreviationSection);
		}
		return abbreviation;
	}

	private static class NamespaceIndividualHandler extends SubtreeHandler<NamespaceIndividualDefinition> {

		@Override
		public Collection<Message> create(Article article, Section<NamespaceIndividualDefinition> section) {
			Rdf2GoCore core = Rdf2GoCore.getInstance(article);
			String namespace = core.getNameSpaces().get(section.get().getAbbreviation(section));
			if (namespace == null) return Messages.noMessage();
			String individual = section.get().getIndividual(section);
			URI individualURI = core.createURI(namespace, individual);
			core.addStatements(core.createStatement(individualURI, RDF.type, OWL.Thing));
			return Messages.noMessage();
		}
	}
}
