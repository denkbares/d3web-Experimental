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

import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.owl.inference.OWLSessionObject;
import de.d3web.owl.inference.PSMethodOWL;

/**
 * Assignment which evaluates a logic formula containing an existential
 * quantifier. Depending on the evaluation result a specified @link{QuestionYN}
 * is set to YES or NO.
 *
 * The quantifier of such assignments is always @link{Quantifier.EXISTENTIAL}.
 *
 * @author Sebastian Furth
 * @created Mar 29, 2011
 */
public final class YesNoAssignment extends AbstractAssignment {

	private final QuestionYN target;

	public YesNoAssignment(IRI complexOWLClass, QuestionYN target) {
		super(complexOWLClass, Quantifier.EXISTENTIAL);
		if (target == null) {
			throw new NullPointerException("The target can't be null!");
		}
		this.target = target;
	}

	public QuestionYN getTarget() {
		return target;
	}

	@Override
	public void eval(Session session, OWLSessionObject so) {
		Set<OWLNamedIndividual> individuals = getOWLIndividuals(so);
		// Create new ChoiceValue
		ChoiceValue value = !individuals.isEmpty()
				? new ChoiceValue(target.getAnswerChoiceYes())
				: new ChoiceValue(target.getAnswerChoiceNo());
		// Get old value
		Value oldValue = session.getBlackboard().getValue(target,
				session.getPSMethodInstance(PSMethodOWL.class), this);
		// Set new value if it differs from old value
		if (!value.equals(oldValue)) {
			session.getBlackboard().addValueFact(FactFactory.createFact(session, target, value,
					this, session.getPSMethodInstance(PSMethodOWL.class)));
		}
	}

	@Override
	public String toString() {
		return "YesNoAssignment [quantifier=" + quantifier + ", complexClassIRI="
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
		if (!(obj instanceof YesNoAssignment)) return false;
		YesNoAssignment other = (YesNoAssignment) obj;
		if (target == null) {
			if (other.target != null) return false;
		}
		else if (!target.equals(other.target)) return false;
		return true;
	}

}
