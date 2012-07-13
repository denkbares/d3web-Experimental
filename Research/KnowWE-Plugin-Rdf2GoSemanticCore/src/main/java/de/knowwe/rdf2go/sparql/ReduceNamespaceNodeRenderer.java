package de.knowwe.rdf2go.sparql;

import de.knowwe.rdf2go.Rdf2GoCore;

public class ReduceNamespaceNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(String text, String variable) {
		return Rdf2GoCore.getInstance().reduceNamespace(text);
	}

	@Override
	public boolean allowFollowUpRenderer() {
		return true;
	}

}
