package de.knowwe.kdom.manchester;

import java.util.ArrayList;
import java.util.Collection;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 *
 *
 * @author smark
 * @created 14.09.2011
 */
public class AxiomStorageSubtree {

	private final Collection<OWLAxiom> axioms;

	/**
	 *
	 */
	private static AxiomStorageSubtree instance;

	/**
	 *
	 */
	private AxiomStorageSubtree() {
		axioms = new ArrayList<OWLAxiom>();
	}

	/**
	 *
	 *
	 * @created 14.09.2011
	 * @return
	 */
	public static synchronized AxiomStorageSubtree getInstance() {
		if (instance == null) {
			instance = new AxiomStorageSubtree();
		}
		return instance;
	}

	public void addAxiom(OWLAxiom axiom) {
		axioms.add(axiom);
	}

	public Collection<OWLAxiom> getAxioms() {
		return axioms;
	}

	public void clear() {
		if (!axioms.isEmpty()) {
			axioms.clear();
		}
	}
}
