package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.EvalResult;


public interface Evaluator {

	public boolean canEvaluate(Condition condition);
	
	public EvalResult evaluate(Condition condition);
		
}
