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

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.datamanagement.OCDomain;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 28.06.2012
 */
public class ChoiceEqualEval extends AbstractEvaluator {

	@Override
	protected Class<? extends Condition> getEvaluationClass() {
		return CondEqual.class;
	}

	@Override
	public EvalResult evaluate(Condition condition, KnowledgeBase kb) {
		CondEqual condEqual = (CondEqual) condition;

		if (condEqual.getQuestion() instanceof QuestionOC) {
			return evaluateOC(condEqual);
		}
		else {
			return evaluateMC(condEqual);
		}

	}

	private EvalResult evaluateMC(CondEqual condEqual) {
		throw new UnsupportedOperationException();
	}

	private EvalResult evaluateOC(CondEqual condEqual) {
		ChoiceValue value = (ChoiceValue) condEqual.getValue();
		QuestionOC question = (QuestionOC) condEqual.getQuestion();

		return new EvalResult(question, new OCDomain(question, value.getChoice(question)));
	}

}
