package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.rendering.StyleRenderer;

public class OWLInstanceDefinition extends URITermDefinition {

	public static final StyleRenderer CLASS_RENDERER = new StyleRenderer("color:rgb(152, 180, 12)");
	
	public OWLInstanceDefinition() {
		this.setCustomRenderer(CLASS_RENDERER);
	}

	@Override
	public String getTermName(Section<? extends KnowWETerm<URI>> s) {
		return s.getOriginalText();
	}

}
