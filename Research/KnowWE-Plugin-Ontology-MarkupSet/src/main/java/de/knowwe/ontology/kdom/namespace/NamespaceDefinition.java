package de.knowwe.ontology.kdom.namespace;

import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;

public class NamespaceDefinition extends SimpleDefinition {

	public NamespaceDefinition() {
		super(TermRegistrationScope.LOCAL, String.class);
		this.setSectionFinder(new AllTextFinderTrimmed());
	}

}