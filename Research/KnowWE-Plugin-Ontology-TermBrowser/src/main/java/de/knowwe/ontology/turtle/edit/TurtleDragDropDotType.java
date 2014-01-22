package de.knowwe.ontology.turtle.edit;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.kdom.renderer.CompositeRenderer;
import de.knowwe.kdom.sectionFinder.StringSectionFinder;
import de.knowwe.termbrowser.DroppableTargetSurroundingRenderer;


public class TurtleDragDropDotType extends AbstractType {

	public TurtleDragDropDotType() {
		this.setSectionFinder(new StringSectionFinder("."));
		this.setRenderer(new CompositeRenderer(new DroppableTargetSurroundingRenderer()));
	}
}
