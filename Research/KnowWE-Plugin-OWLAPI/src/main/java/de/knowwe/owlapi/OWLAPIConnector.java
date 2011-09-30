/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.owlapi;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * This class enables KnowWE to connect to the OWLAPI. In general everything can
 * be done using KnowWE's global ontology. This ontology is accessible via a
 * static method.
 *
 * If you want a clean ontology for a special purpose you can create it by
 * specifying it's base URI. The created ontology can be accessed with it's base
 * URI.
 *
 * Every OWLAPIConnecton instance offers convenience methods to access the
 * encapsulated instance of an @link{OWLReasoner} and to add and remove axioms
 * from the underlying ontology.
 *
 * If you need more features, the underlying ontology can be accessed via the
 * encapsulated instance of the @link{OWLOntologyManger} class.
 *
 * @author Sebastian Furth
 * @created May 24, 2011
 */
public class OWLAPIConnector {

	private static final Map<IRI, OWLAPIConnector> connectors = new HashMap<IRI, OWLAPIConnector>();
	private static final IRI globalBaseIRI = IRI.create(Rdf2GoCore.basens);

	/**
	 * Returns an OWLAPIConnector instance granting access to KnowWE's global
	 * ontology. The returned ontology has the base-URI that is specified by the
	 * basens in the @link{Rdf2GoCore} class as base-URI.
	 *
	 * This ontology should store everything that is not intended for a special
	 * purpose. If you are not sure, you are probably right by choosing the
	 * returned ontology as the target for your axioms and as source for your
	 * reasoning.
	 *
	 * @created May 24, 2011
	 * @return OWLAPIConnector instance for KnowWE's global ontology.
	 */
	public static synchronized OWLAPIConnector getGlobalInstance() {
		return getInstance(globalBaseIRI);
	}

	/**
	 * Returns an OWLAPIConnector instance granting access to an ontology with
	 * the specified base IRI.
	 *
	 * An IRI can be created from an @link{String} or an @link{URI} by the
	 * <strong>createIRI</strong> method of the @link{IRI} class.
	 *
	 * If the desired OWLAPIConnector object doesn't exist yet, it will be
	 * created and initialized.
	 *
	 * @created May 24, 2011
	 * @param baseIRI the IRI identifying the OWLAPIConnector/OWLOntology
	 * @return OWLAPIConnector instance for a specified ontology/IRI.
	 */
	public static synchronized OWLAPIConnector getInstance(IRI baseIRI) {
		OWLAPIConnector instance = connectors.get(baseIRI);
		return instance != null ? instance : createInstance(baseIRI);
	}

	private static OWLAPIConnector createInstance(IRI baseIRI) {
		try {
			OWLAPIConnector instance;
			instance = new OWLAPIConnector(baseIRI);
			connectors.put(baseIRI, instance);
			return instance;
		}
		catch (OWLOntologyCreationException e) {
			Logger.getLogger(OWLAPIConnector.class.getSimpleName()).severe(
					"Unable to create new OWLOntology instance for IRI: " + baseIRI);
		}
		return null;
	}

	private final OWLOntologyManager manager;
	private final OWLReasoner reasoner;
	private final OWLOntology ontology;
	private final OWLReasonerFactory factory;

	private OWLAPIConnector(IRI baseIRI) throws OWLOntologyCreationException {
		if (baseIRI == null) {
			throw new NullPointerException("The base IRI can't be null!");
		}
		this.manager = OWLManager.createOWLOntologyManager();
		this.ontology = manager.createOntology(baseIRI);

		ResourceBundle properties = ResourceBundle.getBundle("owlapi");
		String reasoner = properties.getString("owlapi.reasoner");

		if (reasoner.equals("pellet")) {
			this.factory = com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory.getInstance();
			this.reasoner = factory.createReasoner(ontology);
		}
		else {
			factory = new org.semanticweb.HermiT.Reasoner.ReasonerFactory();
			this.reasoner = factory.createReasoner(ontology);
		}
	}

	/**
	 * Returns the @link{OWLOntologyManager} instance responsible for the
	 * maintenance of this connectors's @link{OWLOntology} instance.
	 *
	 * The @link{OWLOntologyManager} enables you to do almost everything with
	 * the underlying instance of the @link{OWLOntology} class.
	 *
	 * @created May 24, 2011
	 * @return OWLOntologyManager instance.
	 */
	public OWLOntologyManager getManager() {
		return manager;
	}

	/**
	 * Returns an @link{OWLReasoner} object which enables reasoning on the
	 * ontology accessible by this OWLAPIConnector instance.
	 *
	 * Please note that the reasoner is flushed / synchronized before it is
	 * returned. Thus the reasoner will take all changes done until now into
	 * account!
	 *
	 * If you do any changes on the ontology after calling this method, you have
	 * to synchronize the reasoner manually by calling it's flush method.
	 *
	 * @created May 24, 2011
	 * @return OWLReasoner instance.
	 */
	public OWLReasoner getReasoner() {
		// make sure the reasoner is up to date!
		reasoner.flush();
		return reasoner;
	}

	/**
	 * Returns the @link{OWLOntology} instance managed by this OWLAPIConnector
	 * instance.
	 *
	 * @created May 24, 2011
	 * @return OWLOntology instance.
	 */
	public OWLOntology getOntology() {
		return ontology;
	}

	/**
	 * Returns the @link{OWLReasonerFactory} instance responsible for the
	 * creation of {@link OWLReasoner} instances.
	 *
	 * @created September 27, 2011
	 * @return OWLReasonerFactory instance.
	 */
	public OWLReasonerFactory getFactory() {
		return factory;
	}

	/**
	 * Adds the specified @link{OWLAxiom}s to the @link{OWLOntology} instance
	 * accessible by this OWLAPIConnector instance.
	 *
	 * @created May 24, 2011
	 * @param axioms the OWLAxioms to be added to the ontology.
	 */
	public void addAxioms(Set<OWLAxiom> axioms) {
		if (axioms != null) {
			manager.addAxioms(ontology, axioms);
		}
	}

	/**
	 * Removes the specified @link{OWLAxiom}s from the @link{OWLOntology}
	 * instance accessible by this OWLAPIConnector instance.
	 *
	 * @created May 24, 2011
	 * @param axiom the OWLAxioms to be removed from the ontology.
	 */
	public void removeAxioms(Set<OWLAxiom> axioms) {
		if (axioms != null) {
			manager.removeAxioms(ontology, axioms);
		}
	}

}
