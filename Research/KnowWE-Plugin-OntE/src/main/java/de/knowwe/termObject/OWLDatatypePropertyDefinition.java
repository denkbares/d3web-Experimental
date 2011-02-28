package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.knowwe.termObject.URIObject.URIObjectType;

public class OWLDatatypePropertyDefinition extends URITermDefinition {

	public OWLDatatypePropertyDefinition() {
		this.setCustomRenderer(OWLObjectPropertyDefinition.PROPERTY_RENDERER);
	}

	@Override
	public String getTermName(Section<? extends KnowWETerm<URIObject>> s) {
		return s.getOriginalText();
	}
	
	@Override
	protected URIObjectType getURIObjectType() {
		return URIObjectType.datatypeProperty;
	}

}



