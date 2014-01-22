package de.knowwe.ontology.turtle.edit;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.kdom.renderer.CompositeRenderer;
import de.knowwe.kdom.sectionFinder.StringSectionFinder;
import de.knowwe.termbrowser.DroppableTargetSurroundingRenderer;


public class TurtleDragDropCommaType extends AbstractType {

	public TurtleDragDropCommaType() {
		this.setSectionFinder(new StringSectionFinder(","));
		this.setRenderer(new CompositeRenderer(new DroppableTargetSurroundingRenderer()));
	}
}
