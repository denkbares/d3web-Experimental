package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.CondNumLessEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.datamanagement.NumValue;

public class NumEqualLessEval extends NumEvaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondNumLessEqual.class);
	}

	@Override
	public EvalResult evaluate(Condition condition) {
		EvalResult result = new EvalResult();
//		NumDomain domain = new NumDomain();
		Domain<NumValue> domain = new Domain<NumValue>();
		CondNumLessEqual conLess = (CondNumLessEqual) condition;
		double d = conLess.getConditionValue();
		domain.add(new NumValue(Double.NEGATIVE_INFINITY, d, false, true));
		String var = conLess.getQuestion().getName();
		/*List<Object> result = new LinkedList<Object>();
		result.add(new NumericInterval(Double.NEGATIVE_INFINITY, d, false, true));*/
		result.add(var, domain);
		return result;
	}



}
