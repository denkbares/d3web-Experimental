package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.Section;

public interface RDFResourceType extends Type {

	public URI getURI(Section<? extends RDFResourceType> s);
}
