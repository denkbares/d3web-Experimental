package de.knowwe.rdf2go.sparql;

import java.net.URLDecoder;

import de.knowwe.core.user.UserContext;
import de.knowwe.rdf2go.Rdf2GoCore;

public class DecodeUrlNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(String text, String variable, UserContext user, Rdf2GoCore core) {
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
