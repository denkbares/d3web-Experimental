package de.knowwe.compile.test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.rdf2go.Rdf2GoCore;

public class Utils {

	public static URI getURI(Section<? extends SimpleTerm> s) {
		URI uri = null;
		String baseUrl = Rdf2GoCore.localns;
		try {
			String name = URLEncoder.encode(KnowWEUtils.getTermIdentifier(s), "UTF-8");
			uri = new URIImpl(baseUrl + name);
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;

	}
}
