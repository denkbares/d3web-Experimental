package de.knowwe.rdf2go.sparql;

import de.knowwe.core.utils.Strings;

public class DecodeUrlNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(String text, String variable) {
		return Strings.decodeURL(text);
	}

	@Override
	public boolean allowFollowUpRenderer() {
		return true;
	}

}
