package de.knowwe.kdom.turtle;

import org.ontoware.rdf2go.model.node.Node;

import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.knowwe.termObject.RDFResourceType;

public class DataTypeValue extends AbstractType implements RDFResourceType {

	public DataTypeValue() {
		this.setCustomRenderer(new StyleRenderer("font-weight:bold"));
	}

	@Override
	public Node getNode(Section<? extends RDFResourceType> s) {
		return Rdf2GoCore.getInstance().createLiteral(s.getOriginalText());
	}
}
