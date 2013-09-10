package de.knowwe.compile.test;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.strings.Strings;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.rdf2go.Rdf2GoCore;

public class Utils {

	public static URI getURI(Section<? extends Term> s) {
		String baseUrl = Rdf2GoCore.getInstance().getLocalNamespace();
		String name = Strings.encodeURL(s.get().getTermName(s));
		URI uri = new URIImpl(baseUrl + name);
		return uri;
	}
}
