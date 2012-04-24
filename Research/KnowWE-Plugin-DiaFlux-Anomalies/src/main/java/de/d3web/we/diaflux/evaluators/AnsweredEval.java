package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.CondAnswered;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.EvalResult;

public class AnsweredEval extends TerminalEvaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		// Class<?> conClass = condition.getClass();
		return condition.getClass().equals(CondAnswered.class);
	}

	@Override
	public EvalResult evaluate(Condition condition) {
		// TODO Auto-generated method stub
		return new EvalResult();
	}
}
