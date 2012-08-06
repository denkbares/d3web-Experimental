/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaflux.evaluators;

import java.util.HashSet;
import java.util.Set;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.datamanagement.MOValue;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class EqualEval extends TerminalEvaluator {

	/**
	 *
	 */
	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondEqual.class);
	}

	/**
	 *
	 */
	@Override
	public EvalResult evaluate(Condition condition) {
		EvalResult result = new EvalResult();
		CondEqual conEqual = (CondEqual) condition;
		Value value = conEqual.getValue();
		String name = conEqual.getQuestion().getName();
		Set<String> possibleValues = new HashSet<String>();
		Set<String> actualValues = new HashSet<String>();

		if (value instanceof ChoiceValue) {
			Domain<MOValue> domain = new Domain<MOValue>();
			ChoiceValue cValue = (ChoiceValue) value;
			actualValues.add(cValue.getChoiceID().getText());
			MOValue mo = new MOValue(possibleValues, actualValues, true);
			domain.add(mo);
			result.add(name, domain);

		}
		if (value instanceof MultipleChoiceValue) {
			throw new IllegalStateException("multiple choice values are not implemented yet");
		}
		return result;
	}

}
