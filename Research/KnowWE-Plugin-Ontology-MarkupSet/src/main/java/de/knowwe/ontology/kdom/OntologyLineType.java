package de.knowwe.ontology.kdom;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.basicType.LineBreak;
import de.knowwe.kdom.dashtree.LineEndComment;
import de.knowwe.kdom.sectionFinder.LineSectionFinder;

public class OntologyLineType extends AbstractType {

	public OntologyLineType(Type... childrenTypes) {
		this.setSectionFinder(new LineSectionFinder());
		this.addChildType(new LineEndComment());
		this.addChildType(new LineBreak());
		for (Type childType : childrenTypes) {
			this.addChildType(childType);
		}
	}

}
