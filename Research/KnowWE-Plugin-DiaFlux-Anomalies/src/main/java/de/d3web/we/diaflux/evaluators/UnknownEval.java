package de.d3web.we.diaflux.evaluators;


import de.d3web.core.inference.condition.CondUnknown;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.EvalResult;

public class UnknownEval extends TerminalEvaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		// Class<?> conClass = condition.getClass();
		return condition.getClass().equals(CondUnknown.class);
	}

	@Override
	public EvalResult evaluate(Condition condition) {
		// TODO Auto-generated method stub
		return new EvalResult();
	}
}
