package de.knowwe.termObject;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.GlobalTermReference;
import de.d3web.we.kdom.objects.KnowWETerm;

public class BasicVocabularyTermReference extends GlobalTermReference<URI> {

	private URI uri;

	public BasicVocabularyTermReference(URI uri) {
		super(URI.class);
		this.uri = uri;
	}

	public URI getURI() {
		return uri;
	}

	@Override
	public String getTermObjectDisplayName() {
		return uri.toString();
	}

	@Override
	public String getTermName(Section<? extends KnowWETerm<URI>> s) {
		return s.getOriginalText();

	}

}
