package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.datamanagement.NumValue;

public class NumLessEval extends NumEvaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondNumLess.class);
	}


	@Override
	public EvalResult evaluate(Condition condition) {
		EvalResult result = new EvalResult();
		/*List<Object> result = new LinkedList<Object>();
		double d = ((CondNumLess) condition).getConditionValue();
		result.add(new NumericInterval(Double.NEGATIVE_INFINITY, d, false, false));*/
		Domain<NumValue> domain = new Domain<NumValue>();
		CondNumLess conLess = (CondNumLess) condition;
		double d = conLess.getConditionValue();
		domain.add(new NumValue(Double.NEGATIVE_INFINITY, d, false, false));
		String var = conLess.getQuestion().getName();
		result.add(var, domain);
		return result;
	}

}
