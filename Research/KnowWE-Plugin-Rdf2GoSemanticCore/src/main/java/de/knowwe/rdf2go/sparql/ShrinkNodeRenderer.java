package de.knowwe.rdf2go.sparql;

import de.knowwe.core.utils.Strings;

public class ShrinkNodeRenderer implements SparqlResultNodeRenderer {

	private final int maxLength = 100;

	@Override
	public String renderNode(String text, String variable) {
		if (text.length() > maxLength) {
			return Strings.maskHTML("<span title='" + text + "'>"
					+ text.substring(0, maxLength - 3)
					+ "...</span>");
		}
		return text;
	}

	@Override
	public boolean allowFollowUpRenderer() {
		return false;
	}

}
