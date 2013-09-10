package de.knowwe.kdom.manchester.types;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

public class OWLTermReference extends AbstractType {

	public static final String PATTERN = "\\b([A-Z]|owl)[A-Za-z0-9:]+\\b";

	public OWLTermReference() {
		this.setSectionFinder(new RegexSectionFinder(PATTERN));

		this.addChildType(new PredefinedTermReference());
		this.addChildType(new ImportedTermReference());
		this.addChildType(new LocalTermReference());
	}
}
