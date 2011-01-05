package de.knowwe.termObject;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.rendering.StyleRenderer;

public class BasicVocabularyReference extends DefaultAbstractKnowWEObjectType{
	
	public static final StyleRenderer REF_RENDERER = new StyleRenderer("font-weight:bold");
	
	
	public BasicVocabularyReference() {
		this.setCustomRenderer(REF_RENDERER);
	}

}
