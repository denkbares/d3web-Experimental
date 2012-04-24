package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondNum;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.EvalResult;


public class NumInterEval implements Evaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		if(condition.getClass().equals(CondAnd.class)) {
			CondAnd cond = (CondAnd) condition;
			return ((cond.getTerms().get(0) instanceof CondNum) && (cond.getTerms().get(1) instanceof CondNum));
		}
		return false;
	}


	@Override
	public EvalResult evaluate(Condition condition) {
		// TODO Auto-generated method stub
		return null;
	}

	/*@Override
	public List<Object> negate(Condition condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> intersect(List<Condition> conList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> merge(List<Condition> conList) {
		// TODO Auto-generated method stub
		return null;
	}*/

}
