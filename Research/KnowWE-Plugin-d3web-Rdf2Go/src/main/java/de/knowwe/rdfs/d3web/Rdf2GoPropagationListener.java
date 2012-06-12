package de.knowwe.rdfs.d3web;

import java.util.Collection;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.PropagationListener;
import de.d3web.core.session.Session;

public class Rdf2GoPropagationListener implements PropagationListener {

	@Override
	public void propagationStarted(Session session, Collection<PropagationEntry> entries) {
		// nothing to do
	}

	@Override
	public void postPropagationStarted(Session session, Collection<PropagationEntry> entries) {
		// nothing to do
	}

	@Override
	public void propagationFinished(Session session, Collection<PropagationEntry> entries) {
		// System.out.println("######");
		// for (PropagationEntry entry : entries) {
		// System.out.println(entry);
		// }
		// String id = session.getId();
		// URI sessionIdURI = Rdf2GoCore.getInstance().createlocalURI(
		// Strings.encodeURL(id));
		// URI hasFindingURI =
		// Rdf2GoCore.getInstance().createlocalURI("hasFinding");
		// BlankNode findingBlankNode =
		// Rdf2GoCore.getInstance().createBlankNode();
		//
		// for (PropagationEntry entry : entries) {
		// entry.g
		// }

	}
}
