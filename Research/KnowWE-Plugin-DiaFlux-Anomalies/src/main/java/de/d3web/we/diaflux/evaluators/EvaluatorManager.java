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

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.we.diaflux.datamanagement.EvalResult;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class EvaluatorManager implements Evaluator {

	private static EvaluatorManager evalManager;

	private final List<Evaluator> evaluators = new LinkedList<Evaluator>();

	private EvaluatorManager() {
		// Numeric Evaluators
		evaluators.add(new NumEqualEval());
		evaluators.add(new NumGreaterEval());
		evaluators.add(new NumLessEval());
		evaluators.add(new NumGreaterEqualEval());
		evaluators.add(new NumLessEqualEval());

		// One/Multiple Choice Evaluators
		evaluators.add(new ChoiceEqualEval());

		// Non terminal Evaluators
		evaluators.add(new AndEvaluator());
		evaluators.add(new NotEval());
		evaluators.add(new OrEvaluator());

		//
		evaluators.add(new KnownEval());
		evaluators.add(new UnknownEval());
		evaluators.add(new AnsweredEval());
		evaluators.add(new TrueEvaluator());

	}

	/**
	 * 
	 * @created 08.05.2012
	 * @return
	 */
	public static EvaluatorManager getInstance() {
		if (null == evalManager) {
			evalManager = new EvaluatorManager();
		}
		return evalManager;
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @param evaluator
	 * @return
	 */
	public boolean addEvaluator(Evaluator evaluator) {
		return evaluators.add(evaluator);
	}

	@Override
	public boolean canEvaluate(Condition condition) {
		for (Evaluator evaluator : evaluators) {
			if (evaluator.canEvaluate(condition)) return true;
		}
		return false;
	}

	@Override
	public EvalResult evaluate(Condition condition, KnowledgeBase kb) {
		for (Evaluator evaluator : evaluators) {
			if (evaluator.canEvaluate(condition)) return evaluator.evaluate(condition, kb);
		}
		throw new IllegalArgumentException("Can not evaluate: " + condition);

	}

	/**
	 * 
	 * @created 08.05.2012
	 * @return
	 */
	public List<Evaluator> getEvaluators() {
		return evaluators;
	}
}
