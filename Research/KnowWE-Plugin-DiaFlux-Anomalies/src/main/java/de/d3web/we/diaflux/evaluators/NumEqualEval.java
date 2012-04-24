package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.datamanagement.NumValue;

public class NumEqualEval extends NumEvaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondNumEqual.class);
	}

	@Override
	public EvalResult evaluate(Condition condition) {

		CondNumEqual conEq = (CondNumEqual) condition;
		EvalResult result = new EvalResult();
		double d = conEq.getConditionValue();
//		NumDomain domain = new NumDomain();
//		Domain<NumericInterval> domain = new Domain<NumericInterval>();
		Domain<NumValue> domain = new Domain<NumValue>();
		domain.add(new NumValue(d, d, true, true));
		String var = conEq.getQuestion().getName();
		conEq.getTerminalObjects();
		result.add(var, domain);
		return result;
	}

}
