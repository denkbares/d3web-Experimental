package de.knowwe.rdf2go.sparql;

import java.net.URLDecoder;

import de.knowwe.core.user.UserContext;

public class DecodeUrlNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(String text, String variable, UserContext user) {
		try {
			return URLDecoder.decode(text, "UTF-8");

		}
		catch (Exception e) {
			return text;
		}
	}

	@Override
	public boolean allowFollowUpRenderer() {
		return true;
	}

}
