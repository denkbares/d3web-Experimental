package de.knowwe.rdfs.wikiObjectModel.utils;

import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.sparql.SparqlResultNodeRenderer;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class LinkNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(String text, String variable) {
		return Strings.maskHTML("<a href=\"" + text + "\">")
				+ Rdf2GoUtils.reduceNamespace(text)
				+ Strings.maskHTML("</a>");
	}

	@Override
	public boolean allowFollowUpRenderer() {
		return true;
	}

}
