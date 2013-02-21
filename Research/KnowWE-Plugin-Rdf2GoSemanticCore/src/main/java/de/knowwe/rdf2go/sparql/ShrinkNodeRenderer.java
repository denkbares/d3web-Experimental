package de.knowwe.rdf2go.sparql;

import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;

public class ShrinkNodeRenderer implements SparqlResultNodeRenderer {

	private final int maxLength = 75;

	@Override
	public String renderNode(String text, String variable, UserContext user) {
		if (text.length() > maxLength) {
			String titleText = text.replaceAll("\"", "&#34;");
			titleText = text.replaceAll("'", "&#39;");
			RenderResult result = new RenderResult(user);
			result.appendHTML("<span title='" + titleText + "'>");
			result.append(text.substring(0, maxLength - 3) + "...");
			result.appendHTML("</span>");
			return result.toStringRaw();
		}
		return text;
	}

	@Override
	public boolean allowFollowUpRenderer() {
		return false;
	}

}
