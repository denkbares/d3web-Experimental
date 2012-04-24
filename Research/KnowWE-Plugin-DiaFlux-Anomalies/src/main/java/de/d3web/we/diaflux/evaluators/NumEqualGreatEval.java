package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.CondNumGreaterEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.datamanagement.NumValue;

public class NumEqualGreatEval extends NumEvaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondNumGreaterEqual.class);
	}

	@Override
	public EvalResult evaluate(Condition condition) {
		EvalResult result = new EvalResult();
//		NumDomain domain = new NumDomain();
		Domain<NumValue> domain = new Domain<NumValue>();
		CondNumGreaterEqual conGr = (CondNumGreaterEqual) condition;
		double d = conGr.getConditionValue();
		domain.add(new NumValue(d, Double.POSITIVE_INFINITY, true, false));
		String var = conGr.getQuestion().getName(); // how to get var-Name
		result.add(var, domain);
		return result;
	}
	
	/*public Domain evaluate2(Condition condition) {
		NumDomain result = new NumDomain();
		double d = ((CondNumGreaterEqual) condition).getConditionValue();
		result.add(new NumericInterval(d, Double.POSITIVE_INFINITY, true, false));
		return result;
	}*/

}
