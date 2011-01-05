package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;

public class OWLDatatypePropertyDefinition extends URITermDefinition {

	public OWLDatatypePropertyDefinition() {
		this.setCustomRenderer(OWLObjectPropertyDefinition.PROPERTY_RENDERER);
	}

	@Override
	public String getTermName(Section<? extends KnowWETerm<URI>> s) {
		return s.getOriginalText();
	}

}



