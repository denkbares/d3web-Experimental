package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.CondKnown;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.EvalResult;

public class KnownEval extends TerminalEvaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondKnown.class);
	}

	@Override
	public EvalResult evaluate(Condition condition) {
		// TODO Auto-generated method stub
		return new EvalResult();
	}
}
