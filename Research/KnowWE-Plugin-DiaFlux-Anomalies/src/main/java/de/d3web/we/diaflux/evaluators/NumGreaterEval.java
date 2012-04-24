package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.datamanagement.NumValue;


public class NumGreaterEval extends NumEvaluator{

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondNumGreater.class);
	}


	@Override
	public EvalResult evaluate(Condition condition) {
		EvalResult result = new EvalResult();
//		NumDomain domain = new NumDomain();
		Domain<NumValue> domain = new Domain<NumValue>();
		CondNumGreater conGr = (CondNumGreater) condition;
		double d = conGr.getConditionValue();
		domain.add(new NumValue(d, Double.POSITIVE_INFINITY, false, false));
		String var = conGr.getQuestion().getName(); // how to get var-Name
		/*
		for(TerminologyObject obj : conGr.getTerminalObjects()){
			QuestionNum qnum = (QuestionNum) obj;
		}*/
		result.add(var, domain);
		/*List<Object> result = new LinkedList<Object>();
		double d = ((CondNumGreater) condition).getConditionValue();
		result.add(new NumericInterval(d, Double.POSITIVE_INFINITY, false, false));*/
		return result;
	}

}
