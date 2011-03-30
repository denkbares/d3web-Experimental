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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.owl.inference.OWLSessionObject;
import de.d3web.owl.inference.PSMethodOWL;

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

	// Just for convenience and code beautification
	private final Logger logger = Logger.getLogger(this.getClass().getName());

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
		Set<OWLNamedIndividual> individuals = getOWLIndividuals(so);
		// Get corresponding d3web Solutions
		TerminologyUtil util = TerminologyUtil.getInstance(session.getKnowledgeBase());
		Set<Choice> choices = util.getChoicesFor(individuals, target);
		if (target instanceof QuestionOC) {
			assignChoicesToQuestionOC(session, choices);
		}
		else if (target instanceof QuestionMC) {
			assignChoicesToQuestionMC(session, choices);
		}
	}

	private void assignChoicesToQuestionOC(Session session, Set<Choice> choices) {
		if (choices != null && choices.size() == 1) {
			session.getBlackboard().addValueFact(FactFactory.createFact(session, target,
					new ChoiceValue(choices.toArray(new Choice[1])[0]),
					this, session.getPSMethodInstance(PSMethodOWL.class)));
		}
		else {
			logger.warning("Ambigous amount of values: "
					+ target.getName() + " can only take one value, "
					+ getComplexOWLClass() + " has "
					+ choices.size() + " values. No value has been set.");
		}
	}

	private void assignChoicesToQuestionMC(Session session, Set<Choice> choices) {
		// We have to do this, because the d3web API uses List<Choice> instead
		// of Collection<Choice>
		List<Choice> choiceList = new ArrayList<Choice>(choices);
		session.getBlackboard().addValueFact(
				FactFactory.createFact(session, target,
						MultipleChoiceValue.fromChoices(choiceList), this,
						session.getPSMethodInstance(PSMethodOWL.class)));
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
