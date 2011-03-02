package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.GlobalTermReference;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.objects.TermReference;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;

public class OWLTermReference extends GlobalTermReference<URIObject> implements RDFResourceType {

	public static final StyleRenderer REF_RENDERER = new StyleRenderer(
			"color:rgb(25, 180, 120)");

	public OWLTermReference() {
		super(URIObject.class);
		this.setCustomRenderer(REF_RENDERER);
	}


	@Override
	public String getTermName(Section<? extends KnowWETerm<URIObject>> s) {
		// dirty hack for colons '::'
		// TODO: fix
		if (s.getOriginalText().endsWith("::"))
			return s.getOriginalText().substring(0, s.getOriginalText().length() - 2);

		return s.getOriginalText();

	}

	@Override
	public String getTermObjectDisplayName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public URI getNode(Section<? extends RDFResourceType> s) {
		if (s.get() instanceof TermReference) {
			TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
			Section<? extends TermDefinition> definingSection = terminologyHandler.getTermDefiningSection(
					s.getArticle(), ((TermReference) s.get()).getTermName(s),
					KnowWETerm.GLOBAL);
			if (definingSection == null) return null;
			//KnowWEArticle main = KnowWEEnvironment.getInstance().getArticleManager(KnowWEEnvironment.DEFAULT_WEB).getArticle("Main");
			
			Object termObject = definingSection.get().getTermObject(null,
					definingSection);
			if (termObject instanceof URIObject) {
				return ((URIObject) termObject).getURI();
			}
		}
		return null;
	}

}
