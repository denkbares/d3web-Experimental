/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaflux.datamanagement;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionMC;


/**
 * TODO finish
 * 
 * @author Reinhard Hatko
 * @created 28.06.2012
 */
public class MCDomain implements Domain {

	private final Set<Choice> choices;
	private final QuestionMC questionMC;

	public MCDomain(QuestionMC question) {
		this(question, question.getAllAlternatives());
	}

	public MCDomain(QuestionMC question, Collection<Choice> choices) {
		questionMC = question;
		this.choices = new HashSet<>();
		this.choices.addAll(choices);
	}


	@Override
	public MCDomain add(Domain d) {
		MCDomain domain = (MCDomain) d;
		Collection<Choice> result = new HashSet<>();
		result.addAll(domain.getChoices());
		result.addAll(this.getChoices());

		return new MCDomain(getQuestion(), result);
	}

	@Override
	public MCDomain negate() {
		Collection<Choice> result = new HashSet<>(getQuestion().getAllAlternatives());
		result.removeAll(getChoices());
		return new MCDomain(getQuestion(), result);
	}

	@Override
	public boolean contains(Domain d) {
		MCDomain domain = (MCDomain) d;
		return this.choices.containsAll(domain.getChoices());
	}

	@Override
	public boolean intersects(Domain domain) {
		return !this.intersect(domain).isEmpty();
	}

	@Override
	public boolean isEmpty() {
		return choices.isEmpty();
	}

	@Override
	public MCDomain intersect(Domain d) {
		MCDomain domain = (MCDomain) d;
		Collection<Choice> result = new LinkedList<>(this.getChoices());
		result.retainAll(domain.getChoices());
		return new MCDomain(getQuestion(), result);
	}

	public Collection<Choice> getChoices() {
		return Collections.unmodifiableSet(choices);
	}

	public QuestionMC getQuestion() {
		return questionMC;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((choices == null) ? 0 : choices.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MCDomain other = (MCDomain) obj;
		if (choices == null) {
			if (other.choices != null) return false;
		}
		else if (!choices.equals(other.choices)) return false;
		return true;
	}

}
