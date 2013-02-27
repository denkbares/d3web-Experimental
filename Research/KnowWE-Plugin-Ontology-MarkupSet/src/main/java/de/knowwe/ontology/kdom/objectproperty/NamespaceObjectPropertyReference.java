package de.knowwe.ontology.kdom.objectproperty;

import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.ontology.kdom.namespace.AbbreviationPrefixReference;
import de.knowwe.ontology.kdom.namespace.AbbreviationReference;
import de.knowwe.rdf2go.Rdf2GoCore;

public class NamespaceObjectPropertyReference extends SimpleReference {

	public NamespaceObjectPropertyReference() {
		super(TermRegistrationScope.LOCAL, NamespaceObjectPropertyDefinition.class);
		this.addChildType(new AbbreviationPrefixReference());
		this.addChildType(new ObjectPropertyReference());
		this.setSectionFinder(new AllTextFinderTrimmed());
	}

	@Override
	public String getTermName(Section<? extends SimpleTerm> section) {
		String namespace = getAbbreviation(section);
		String propertyName = getProperty(section);
		return namespace + ":" + propertyName;
	}

	public String getProperty(Section<? extends SimpleTerm> section) {
		Section<ObjectPropertyReference> individualSection = Sections.findChildOfType(section,
				ObjectPropertyReference.class);
		String propertyName = individualSection.get().getTermName(individualSection);
		return propertyName;
	}

	public String getAbbreviation(Section<? extends SimpleTerm> section) {
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

}
