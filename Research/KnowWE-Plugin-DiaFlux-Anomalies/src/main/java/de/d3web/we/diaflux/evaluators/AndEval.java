package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.EvalResult;


public class AndEval extends NonTerminalEvaluator{

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondAnd.class);
	}

	@Override
	public EvalResult evaluate(Condition condition) {
		EvalResult result = new EvalResult();
//		List<List<Object>> evalList = new LinkedList<List<Object>>();
		EvaluatorManager evalManager =  EvaluatorManager.getEvalManager();
		CondAnd condAnd = (CondAnd) condition;
		for(Condition con : condAnd.getTerms()) {
			for(Evaluator eval : evalManager.getEvaluator()) {
				if(eval.canEvaluate(con)) {
//					evalList.add(eval.evaluate(con));
					EvalResult eRes = eval.evaluate(con);
					result = result.intersect(eRes);
//					result = result.restrictWith(eRes);
				}
			}
		}
		return result;
		
		
	}
	
	/*public EvalResult evaluate2(Condition condition) {
		List<List<Object>> evalList = new LinkedList<List<Object>>();
		EvaluatorManager evalManager =  EvaluatorManager.getEvalManager();
		CondAnd condAnd = (CondAnd) condition;
		for(Condition con : condAnd.getTerms()) {
			for(Evaluator eval : evalManager.getEvaluator()) {
				if(eval.canEvaluate(con)) {
					evalList.add(eval.evaluate(con));
				}
			}
		}
		return null;
		
		
	}

	@Override
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
