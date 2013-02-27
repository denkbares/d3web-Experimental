package de.knowwe.ontology.kdom.individual;

import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.ontology.kdom.namespace.AbbreviationPrefixReference;
import de.knowwe.ontology.kdom.namespace.AbbreviationReference;
import de.knowwe.rdf2go.Rdf2GoCore;

public class NamespaceIndividualReference extends SimpleReference {

	public NamespaceIndividualReference() {
		super(TermRegistrationScope.LOCAL, NamespaceIndividualDefinition.class);
		this.addChildType(new AbbreviationPrefixReference());
		this.addChildType(new IndividualReference());
		this.setSectionFinder(new AllTextFinderTrimmed());
	}

	@Override
	public String getTermName(Section<? extends SimpleTerm> section) {
		String namespace = getAbbreviation(section);
		String individualName = getIndividual(section);
		return namespace + ":" + individualName;
	}

	public String getIndividual(Section<? extends SimpleTerm> section) {
		Section<IndividualReference> individualSection = Sections.findChildOfType(section,
				IndividualReference.class);
		String individualName = individualSection.get().getTermName(individualSection);
		return individualName;
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
