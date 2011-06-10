package de.knowwe.compile.test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.knowwe.rdf2go.Rdf2GoCore;

public class Utils {

	public static URI getURI(Section<? extends KnowWETerm> s) {
		URI uri = null;
		String baseUrl = Rdf2GoCore.localns;
		try {
			String name = URLEncoder.encode(s.get().getTermIdentifier(s), "UTF-8");
			uri = new URIImpl(baseUrl + name);
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;

	}
}
