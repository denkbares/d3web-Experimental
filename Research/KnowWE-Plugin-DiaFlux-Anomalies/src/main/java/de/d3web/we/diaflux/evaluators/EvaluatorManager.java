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

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class EvaluatorManager {

	private static EvaluatorManager evalManager;

	private final List<Evaluator> EvalList = new LinkedList<Evaluator>();

	/**
	 * 
	 */
	private EvaluatorManager() {
		// Numeric Evaluators
		EvalList.add(new NumEqualEval());
		EvalList.add(new NumGreaterEval());
		EvalList.add(new NumLessEval());
		EvalList.add(new NumEqualGreatEval());
		EvalList.add(new NumEqualLessEval());

		// One/Multiple Choice Evaluators
		EvalList.add(new EqualEval());

		// Non terminal Evaluators
		EvalList.add(new AndEval());
		EvalList.add(new NotEval());
		EvalList.add(new OrEval());

		//
		EvalList.add(new KnownEval());
		EvalList.add(new UnknownEval());
		EvalList.add(new AnsweredEval());

	}

	/**
	 * 
	 * @created 08.05.2012
	 * @return
	 */
	public static EvaluatorManager getEvalManager() {
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
		return EvalList.add(evaluator);
	}

	/**
	 * 
	 * @created 08.05.2012
	 * @return
	 */
	public List<Evaluator> getEvaluator() {
		return EvalList;
	}
}
