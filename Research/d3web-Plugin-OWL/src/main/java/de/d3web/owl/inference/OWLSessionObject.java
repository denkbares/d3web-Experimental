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
package de.d3web.owl.inference;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.owl.OWLOntologyUtil;

/**
 * Stores the OWLOntology instance created for this session as well as some
 * related objects.
 * 
 * @author Sebastian Furth
 * @created Mar 24, 2011
 */
public class OWLSessionObject implements SessionObject {

	private final OWLOntology ontology;
	private final OWLReasoner reasoner;
	private final OWLOntologyUtil util;

	/**
	 * Creates a new OWLSessionObject which encapsulates the OWLOntology of this
	 * session and some related objects, initialized with this ontology.
	 * 
	 * @param theSourceObject
	 * @param ontology the ontology of this session.
	 */
	public OWLSessionObject(OWLOntology ontology) {
		if (ontology == null) {
			throw new NullPointerException("The ontology can't be null!");
		}
		this.ontology = ontology;
		this.util = new OWLOntologyUtil(ontology);
		// initialize the reasoner
		OWLReasonerFactory factory = new Reasoner.ReasonerFactory();
		this.reasoner = factory.createReasoner(ontology);
	}

	/**
	 * Returns the OWLOntology instance of this session.
	 * 
	 * @created Mar 24, 2011
	 * @return the ontology of this session.
	 */
	public OWLOntology getOntology() {
		return ontology;
	}

	/**
	 * Returns the OWLReasoner instance of this session.
	 * 
	 * @created Mar 28, 2011
	 * @return the reasoner of this session.
	 */
	public OWLReasoner getReasoner() {
		return reasoner;
	}

	/**
	 * Returns an OntologyUtil instance initialized with the ontology of this
	 * session.
	 * 
	 * @created Mar 28, 2011
	 * @return
	 */
	public OWLOntologyUtil getOntologyUtil() {
		return util;
	}
}
