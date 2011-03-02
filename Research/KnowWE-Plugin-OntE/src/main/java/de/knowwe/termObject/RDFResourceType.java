package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.Node;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;

public interface RDFResourceType extends Type {

	public Node getNode(Section<? extends RDFResourceType> s);
}
