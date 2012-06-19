package de.knowwe.rdfs.d3web;

import java.util.Collection;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.PropagationListener;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.knowwe.rdf2go.Rdf2GoCore;

public class Rdf2GoPropagationListener implements PropagationListener {

	private final boolean commitAfterPropagation;

	/**
	 * Constructor for the {@link Rdf2GoPropagationListener}.
	 * 
	 * @param commitAfterPropagation set this to true, if you want the Listener
	 *        to commit the changes itself after each propagation
	 */
	public Rdf2GoPropagationListener(boolean commitAfterPropagation) {
		this.commitAfterPropagation = commitAfterPropagation;
	}

	/**
	 * Constructor for the {@link Rdf2GoPropagationListener}. Using this
	 * constructor, the Listener commits the changes in the session after each
	 * propagation.
	 * 
	 */
	public Rdf2GoPropagationListener() {
		this(true);
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
	public void propagationFinished(Session session, Collection<PropagationEntry> entries) {
		for (PropagationEntry entry : entries) {
			TerminologyObject changedObject = entry.getObject();
			D3webRdf2GoSessionManager mgr = D3webRdf2GoSessionManager.getInstance();
			mgr.removeFactStatements(session, changedObject);
			mgr.addFactAsStatements(session, changedObject, entry.getNewValue());
		}
		if (commitAfterPropagation) {
			Thread thread = new Thread() {

				@Override
				public void run() {
					Rdf2GoCore.getInstance().commit();
				}
			};
			thread.start();
		}
	}
}
