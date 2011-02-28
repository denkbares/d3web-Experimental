package de.knowwe.termObject;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.knowwe.termObject.URIObject.URIObjectType;

public class OWLClassDefinition extends URITermDefinition {

	public static final StyleRenderer CLASS_RENDERER = new StyleRenderer("color:rgb(125, 80, 102)");
	
	public OWLClassDefinition() {
		this.setCustomRenderer(CLASS_RENDERER);
	}

	@Override
	public String getTermName(Section<? extends KnowWETerm<URIObject>> s) {
		return s.getOriginalText();
	}
	
	@Override
	protected URIObjectType getURIObjectType() {
		return URIObjectType.Class;
	}


}

