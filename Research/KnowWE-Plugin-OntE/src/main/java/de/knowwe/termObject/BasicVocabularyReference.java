package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.knowwe.onte.owl.terminology.URIUtil;

public class BasicVocabularyReference extends AbstractType implements RDFResourceType{
	
	public static final StyleRenderer REF_RENDERER = new StyleRenderer("font-weight:bold");
	
	
	public BasicVocabularyReference() {
		this.setCustomRenderer(REF_RENDERER);
	}


	@Override
	public URI getURI(Section<? extends RDFResourceType> s) {
		if(s.get() instanceof BasicVocabularyReference) {
			return URIUtil.getURI((Section<? extends BasicVocabularyReference>)s);
		}
		return null;
	}

}
