package de.knowwe.ontology.kdom;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.kdom.sectionFinder.LineSectionFinder;

public class OntologyLineType extends AbstractType {

	public OntologyLineType(Type... childrenTypes) {
		this.setSectionFinder(new LineSectionFinder());
		for (Type childType : childrenTypes) {
			this.addChildType(childType);
		}
	}

}
