package de.knowwe.rdfs.d3web;

import java.util.Collection;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.PropagationListener;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;

public class Rdf2GoPropagationListener implements PropagationListener {

	private final boolean commitAfterPropagation;
	private final Rdf2GoSessionManager mgr;

	/**
	 * Constructor for the {@link Rdf2GoPropagationListener}.
	 *
	 * @param commitAfterPropagation set this to true, if you want the Listener
	 *                               to commit the changes itself after each propagation
	 */
	public Rdf2GoPropagationListener(Rdf2GoSessionManager manager, boolean commitAfterPropagation) {
		this.commitAfterPropagation = commitAfterPropagation;
		this.mgr = manager;
	}

	@Override
	public void propagationStarted(Session session, Collection<PropagationEntry> entries) {
		// nothing to do
	}

	@Override
	public void postPropagationStarted(Session session, Collection<PropagationEntry> entries) {
		// nothing to do
	}

	@Override
	public void propagating(Session session, PSMethod psMethod, Collection<PropagationEntry> entries) {
		// nothing to do
	}

	@Override
	public void propagationFinished(Session session, Collection<PropagationEntry> entries) {
		for (PropagationEntry entry : entries) {
			Value value = entry.getNewValue();
			if (value instanceof Indication) {
				continue;
			}

			TerminologyObject changedObject = entry.getObject();
			mgr.removeFactStatements(session, changedObject);
			mgr.addFactAsStatements(session, changedObject, value);
		}
		if (commitAfterPropagation) {
			Thread thread = new Thread() {

				@Override
				public void run() {
					mgr.commit();
				}
			};
			thread.start();
		}
	}
}
