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
package de.d3web.owl.assignment;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import de.d3web.owl.OWLOntologyUtil;
import de.d3web.owl.inference.OWLSessionObject;

/**
 * Abstract implementation of the @link{Assignment} interface. Offers some
 * methods needed by almost all assignments.
 * 
 * @author Sebastian Furth
 * @created Mar 29, 2011
 */
public abstract class AbstractAssignment implements Assignment {

	// Just for convenience and code beautification
	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	/** Specifies the IRI of the complex OWL class */
	protected final IRI complexClassIRI;

	/** A Quantifier used for the processing of the individuals */
	protected final Quantifier quantifier;

	public AbstractAssignment(IRI complexClassIRI, Quantifier quantifier) {
		if (complexClassIRI == null) {
			throw new NullPointerException("The complex OWL class can't be null!");
		}
		if (quantifier == null) {
			throw new NullPointerException("The quantifier can't be null!");
		}
		this.complexClassIRI = complexClassIRI;
		this.quantifier = quantifier;
	}

	protected Set<OWLNamedIndividual> getOWLIndividuals(OWLSessionObject so) {
		if (so != null) {
			OWLOntologyUtil util = so.getOntologyUtil();
			OWLClass complexClass = util.getOWLClassFor(complexClassIRI);
			if (complexClass != null) {
				OWLReasoner reasoner = so.getReasoner();
				NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(complexClass, false);
				return individuals.getFlattened();
			}
			else {
				return Collections.emptySet();
			}
		}
		else {
			logger.severe("OWLSessionObject is null! Unable to do assignments!");
			return Collections.emptySet();
		}
	}

	public IRI getComplexOWLClass() {
		return complexClassIRI;
	}

	public Quantifier getQuantifier() {
		return quantifier;
	}

	@Override
	public String toString() {
		return "AbstractAssignment [quantifier=" + quantifier + ", complexClassIRI="
				+ complexClassIRI + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((complexClassIRI == null) ? 0 : complexClassIRI.hashCode());
		result = prime * result + ((quantifier == null) ? 0 : quantifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof AbstractAssignment)) return false;
		AbstractAssignment other = (AbstractAssignment) obj;
		if (complexClassIRI == null) {
			if (other.complexClassIRI != null) return false;
		}
		else if (!complexClassIRI.equals(other.complexClassIRI)) return false;
		if (quantifier != other.quantifier) return false;
		return true;
	}


}
