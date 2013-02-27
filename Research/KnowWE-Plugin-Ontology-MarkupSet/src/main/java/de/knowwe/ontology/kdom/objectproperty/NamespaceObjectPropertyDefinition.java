package de.knowwe.ontology.kdom.objectproperty;

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

public class NamespaceObjectPropertyDefinition extends SimpleDefinition {

	public NamespaceObjectPropertyDefinition() {
		super(TermRegistrationScope.LOCAL, NamespaceObjectPropertyDefinition.class);
		this.addChildType(new AbbreviationPrefixReference());
		this.addChildType(new ObjectPropertyDefinition());
		this.addSubtreeHandler(new NamespaceObjectPropertyHandler());
		this.setSectionFinder(new AllTextFinderTrimmed());
	}

	@Override
	public String getTermName(Section<? extends SimpleTerm> section) {
		String namespace = getAbbreviation(section);
		String propertyName = getObjectProperty(section);
		return namespace + ":" + propertyName;
	}

	private String getObjectProperty(Section<? extends SimpleTerm> section) {
		Section<ObjectPropertyDefinition> propertySection = Sections.findChildOfType(section,
				ObjectPropertyDefinition.class);
		String propertyName = propertySection.get().getTermName(propertySection);
		return propertyName;
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

	private static class NamespaceObjectPropertyHandler extends SubtreeHandler<NamespaceObjectPropertyDefinition> {

		@Override
		public Collection<Message> create(Article article, Section<NamespaceObjectPropertyDefinition> section) {
			Rdf2GoCore core = Rdf2GoCore.getInstance(article);
			String namespace = core.getNameSpaces().get(section.get().getAbbreviation(section));
			if (namespace == null) return Messages.noMessage();
			String property = section.get().getObjectProperty(section);
			URI propertyURI = core.createURI(namespace, property);
			core.addStatements(core.createStatement(propertyURI, RDF.type, OWL.ObjectProperty));
			return Messages.noMessage();
		}
	}
}
