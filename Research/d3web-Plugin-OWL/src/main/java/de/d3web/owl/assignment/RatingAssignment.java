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

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.owl.inference.OWLSessionObject;
import de.d3web.owl.inference.PSMethodOWL;

/**
 * Assigns a d3web @link{Rating} to @link{Solution}s with the names of the
 * individuals belonging to a specified complex OWL class.
 *
 * The quantifier of such assignments is always @link{Quantifier.UNIVERSAL}.
 *
 * @author Sebastian Furth
 * @created Mar 29, 2011
 */
public class RatingAssignment extends AbstractAssignment {

	private final Rating rating;

	public RatingAssignment(IRI complexOWLClass, Rating rating) {
		super(complexOWLClass, Quantifier.UNIVERSAL);
		if (rating == null) {
			throw new NullPointerException("The value can't be null.");
		}
		this.rating = rating;
	}

	@Override
	public void assign(Session session, OWLSessionObject so) {
		Set<OWLNamedIndividual> individuals = getOWLIndividuals(so);
		// Get corresponding d3web Solutions
		TerminologyUtil util = TerminologyUtil.getInstance(session.getKnowledgeBase());
		Set<Solution> solutions = util.getSolutionsFor(individuals);
		// Set the new ratings in the blackboard
		Blackboard blackboard = session.getBlackboard();
		if (!solutions.isEmpty()) {
			for (Solution solution : solutions) {
				// Get old value
				Value oldValue = session.getBlackboard().getValue(solution,
						session.getPSMethodInstance(PSMethodOWL.class), this);
				// Set new value if it differs from old value
				if (!rating.equals(oldValue)) {
					blackboard.addValueFact(FactFactory.createFact(session, solution, rating,
							this, session.getPSMethodInstance(PSMethodOWL.class)));
				}
			}
		}
		else {
			// Iterate over all solutions and remove their rating if the source
			// of this value is this assignment
			for (Solution valuedSolution : blackboard.getValuedSolutions()) {
				blackboard.removeValueFact(valuedSolution, this);
			}
		}
	}

	public Rating getRating() {
		return rating;
	}

	@Override
	public String toString() {
		return "RatingAssignment [quantifier=" + quantifier + ", complexClassIRI="
				+ complexClassIRI + ", rating=" + rating + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((rating == null) ? 0 : rating.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (!(obj instanceof RatingAssignment)) return false;
		RatingAssignment other = (RatingAssignment) obj;
		if (rating == null) {
			if (other.rating != null) return false;
		}
		else if (!rating.equals(other.rating)) return false;
		return true;
	}

}
