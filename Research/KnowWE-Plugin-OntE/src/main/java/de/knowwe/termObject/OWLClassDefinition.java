package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;

public class OWLClassDefinition extends URITermDefinition {

	public OWLClassDefinition() {
		this.setCustomRenderer(FontColorRenderer.getRenderer(FontColorRenderer.COLOR4));
	}

	@Override
	public String getTermName(Section<? extends KnowWETerm<URI>> s) {
		return s.getOriginalText();
	}

}

