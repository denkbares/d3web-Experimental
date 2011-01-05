package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;

public interface RDFResourceType extends KnowWEObjectType {

	public URI getURI(Section<? extends RDFResourceType> s);
}
