package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.EvalResult;


public class OrEval extends NonTerminalEvaluator{
	

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondOr.class);
	}

	/*@Override
	public boolean evaluate(Condition condition, Object value) {
		
		Condition cond1 = ((CondNot)condition).getTerms().get(0);
		Condition cond2 = ((CondNot)condition).getTerms().get(1);
		for(Evaluator eval1 : evalManager.getEvaluator()) {
			if(eval1.canEvaluate(cond1)) {
				for(Evaluator eval2 : evalManager.getEvaluator()) {
					if(eval2.canEvaluate(cond2)) {
						return (eval1.evaluate(cond1, value) || eval2.evaluate(cond2, value));
					}
				}
			}
		}
		return false;
	}*/

	@Override
	public EvalResult evaluate(Condition condition) {
		EvalResult result = new EvalResult();
		EvaluatorManager evalManager =  EvaluatorManager.getEvalManager();
		CondOr condOr = (CondOr) condition;
		for(Condition cond : condOr.getTerms()) {
			for(Evaluator eval : evalManager.getEvaluator()) {
				if(eval.canEvaluate(cond)) {
					EvalResult eRes = eval.evaluate(cond);
					result = result.merge(eRes);
				}
			}
		}
		return result;
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
