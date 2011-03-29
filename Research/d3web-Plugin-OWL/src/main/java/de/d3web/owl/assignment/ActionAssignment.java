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

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import de.d3web.core.inference.PSAction;
import de.d3web.core.session.Session;
import de.d3web.owl.inference.OWLSessionObject;
import de.d3web.owl.inference.PSMethodOWL;

/**
 * Assignment which evaluates a logic formula containing an existential
 * quantifier. Depending on the evaluation result a specified @link{PSAction} is
 * executed or not.
 *
 * The quantifier of such assignments is always @link{Quantifier.EXISTENTIAL}.
 *
 * @author Sebastian Furth
 * @created Mar 29, 2011
 */
public final class ActionAssignment extends AbstractAssignment {

	private final PSAction action;

	public ActionAssignment(IRI complexOWLClass, PSAction action) {
		super(complexOWLClass, Quantifier.EXISTENTIAL);
		if (action == null) {
			throw new NullPointerException("The action can't be null!");
		}
		this.action = action;
	}

	public PSAction getAction() {
		return action;
	}

	@Override
	public void assign(Session session, OWLSessionObject so) {
		Set<OWLNamedIndividual> individuals = getOWLIndividuals(so);
		if (individuals.size() > 0) {
			action.doIt(session, this, session.getPSMethodInstance(PSMethodOWL.class));
		}
	}

	@Override
	public String toString() {
		return "ActionAssignment [quantifier=" + quantifier + ", complexClassIRI="
				+ complexClassIRI + ", action=" + action + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (!(obj instanceof ActionAssignment)) return false;
		ActionAssignment other = (ActionAssignment) obj;
		if (action == null) {
			if (other.action != null) return false;
		}
		else if (!action.equals(other.action)) return false;
		return true;
	}

}
