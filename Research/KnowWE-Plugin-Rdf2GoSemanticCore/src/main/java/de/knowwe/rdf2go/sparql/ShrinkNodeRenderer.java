package de.knowwe.rdf2go.sparql;

import de.knowwe.core.utils.Strings;

public class ShrinkNodeRenderer implements SparqlResultNodeRenderer {

	private final int maxLength = 75;

	@Override
	public String renderNode(String text, String variable) {
		if (text.length() > maxLength) {
			String titleText = text.replaceAll("\"", "&#34;");
			titleText = text.replaceAll("'", "&#39;");
			return Strings.maskHTML("<span title='" + titleText + "'>"
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
