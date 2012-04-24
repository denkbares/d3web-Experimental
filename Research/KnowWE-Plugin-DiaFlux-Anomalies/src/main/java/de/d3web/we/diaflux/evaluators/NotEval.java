package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.EvalResult;

public class NotEval extends NonTerminalEvaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondNot.class);
	}

	@Override
	public EvalResult evaluate(Condition condition) {
		EvalResult result = new EvalResult();
		EvaluatorManager evalManager = EvaluatorManager.getEvalManager();
		Condition cond = ((CondNot) condition).getTerms().get(0);
		for (Evaluator eval : evalManager.getEvaluator()) {
			if (eval.canEvaluate(cond)) {
				result = eval.evaluate(cond).negate();
			}
		}
		// if Objects NumericIntervall invert all Intervalls

		return result;
	}

	/*
	 * @Override public List<Object> negate(Condition condition) { List<Object>
	 * result = new LinkedList<Object>(); EvaluatorManager evalManager =
	 * EvaluatorManager.getEvalManager(); Condition cond =
	 * ((CondNot)condition).getTerms().get(0); for(Evaluator eval :
	 * evalManager.getEvaluator()) { if(eval.canEvaluate(cond)) { result =
	 * eval.evaluate(cond); } } return result; }
	 * 
	 * 
	 * @Override public List<Object> intersect(List<Condition> conList) { //
	 * TODO Auto-generated method stub return null; }
	 * 
	 * 
	 * @Override public List<Object> merge(List<Condition> conList) { // TODO
	 * Auto-generated method stub return null; }
	 */

}
