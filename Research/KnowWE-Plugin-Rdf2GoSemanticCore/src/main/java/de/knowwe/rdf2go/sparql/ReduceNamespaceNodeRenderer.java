package de.knowwe.rdf2go.sparql;

import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class ReduceNamespaceNodeRenderer implements SparqlResultNodeRenderer {

	@Override
	public String renderNode(String text, String variable) {
		return Rdf2GoUtils.reduceNamespace(text);
	}

	@Override
	public boolean allowFollowUpRenderer() {
		return true;
	}

}
