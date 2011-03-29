/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.owl.assignment;

import org.semanticweb.owlapi.model.IRI;

import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;
import de.d3web.owl.inference.OWLSessionObject;

/**
 * Assigns the individuals belonging to a specified complex OWL class as values
 * to a d3web @link{AbstractTerminologyObject}. Therefore the target must be
 * either a @link{QuestionMC} or the size of the collection of individuals has
 * to be 1.
 *
 * The quantifier of such assignments is always @link{Quantifier.UNIVERSAL}.
 *
 * @author Sebastian Furth
 * @created Mar 29, 2011
 */
public final class ChoiceValueAssignment extends AbstractAssignment {

	private final QuestionChoice target;

	public ChoiceValueAssignment(IRI complexOWLClass, QuestionChoice target) {
		super(complexOWLClass, Quantifier.UNIVERSAL);
		if (target == null) {
			throw new NullPointerException("The target can't be null!");
		}
		this.target = target;
	}

	@Override
	public void assign(Session session, OWLSessionObject so) {
		// Set<OWLNamedIndividual> individuals = getOWLIndividuals(so);
		// TODO: Case QuestionOC
		// TODO: Case QuestionMC
		// TODO? Case QuestionXYZ
	}

	public QuestionChoice getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return "ChoiceValueAssignment [quantifier=" + quantifier + ", complexClassIRI="
				+ complexClassIRI + ", target=" + target + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (!(obj instanceof ChoiceValueAssignment)) return false;
		ChoiceValueAssignment other = (ChoiceValueAssignment) obj;
		if (target == null) {
			if (other.target != null) return false;
		}
		else if (!target.equals(other.target)) return false;
		return true;
	}

}
