package de.knowwe.termObject;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.knowwe.termObject.URIObject.URIObjectType;

public class OWLObjectPropertyDefinition extends URITermDefinition {
	
	public static final StyleRenderer PROPERTY_RENDERER = new StyleRenderer("color:rgb(40, 40, 160)");
	
	public OWLObjectPropertyDefinition() {
		this.setCustomRenderer(PROPERTY_RENDERER);
	}

	@Override
	public String getTermName(Section<? extends KnowWETerm<URIObject>> s) {
		return s.getOriginalText();
	}

	@Override
	protected URIObjectType getURIObjectType() {
		return URIObjectType.objectProperty;
	}
}


