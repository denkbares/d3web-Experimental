package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.GlobalTermReference;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;

public class OWLTermReference extends GlobalTermReference<URI> {

	public static final StyleRenderer REF_RENDERER = new StyleRenderer("color:rgb(25, 180, 120)");

	public OWLTermReference() {
		super(URI.class);
		this.setCustomRenderer(REF_RENDERER);
	}

	public URI getURI(Section<OWLTermReference> s) {
		TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
		Section<? extends TermDefinition> definingSection = terminologyHandler.getTermDefiningSection(s.getArticle(), s.get().getTermName(s), KnowWETerm.GLOBAL);
		Object termObject = definingSection.get().getTermObject(s.getArticle(), definingSection);
		if(termObject instanceof URI) {
			return (URI) termObject;
		}
		return null;
	}



	@Override
	public String getTermName(Section<? extends KnowWETerm<URI>> s) {
		//dirty hack for colons '::'
		//TODO: fix
		if(s.getOriginalText().endsWith("::")) return s.getOriginalText().substring(0, s.getOriginalText().length()-2);
		
		return s.getOriginalText();

	}

	@Override
	public String getTermObjectDisplayName() {
		return this.getClass().getSimpleName();
	}


}
