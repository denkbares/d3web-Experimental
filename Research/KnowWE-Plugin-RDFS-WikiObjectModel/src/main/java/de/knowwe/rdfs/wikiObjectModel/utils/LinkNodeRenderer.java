package de.knowwe.rdfs.wikiObjectModel.utils;

import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.sparql.SparqlResultNodeRenderer;

public class LinkNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(String text, String variable) {
		return Strings.maskHTML("<a href=\"" + text + "\">")
				+ Rdf2GoCore.getInstance().reduceNamespace(text)
				+ Strings.maskHTML("</a>");
	}

	@Override
	public boolean allowFollowUpRenderer() {
		return true;
	}

}
