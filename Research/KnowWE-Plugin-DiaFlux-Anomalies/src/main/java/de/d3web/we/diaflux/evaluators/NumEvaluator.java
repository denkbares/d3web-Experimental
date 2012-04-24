package de.d3web.we.diaflux.evaluators;


public abstract class NumEvaluator extends TerminalEvaluator {

//	public final static double EPSILON = 0.000001;
	
	/*@Override
	public List<Object> negate(Condition condition) {
		EvaluatorManager evalManager =  EvaluatorManager.getEvalManager();
		List<Object> intervals = new LinkedList<Object>();
		for(Evaluator eval : evalManager.getEvaluator()) {
			if(eval.canEvaluate(condition)) {
				intervals = eval.evaluate(condition);
			}
		}
		List<Object> result = new LinkedList<Object>();
		for(Object interval : intervals) {
			result.addAll(((NumericInterval)interval).getOuterIntervals());
		}
		return result;
	}

	@Override
	public List<Object> merge(List<Condition> conList) {
		EvaluatorManager evalManager =  EvaluatorManager.getEvalManager();
		List<Object> intervals = new LinkedList<Object>();
		List<Object> result = new LinkedList<Object>();
		return result;
	}

	@Override
	public List<Object> intersect(List<Condition> conList) {
		EvaluatorManager evalManager =  EvaluatorManager.getEvalManager();
		List<Object> inter1 = new LinkedList<Object>();
		List<Object> inter2 = new LinkedList<Object>();
		List<Object> result = new LinkedList<Object>();
		return result;
	}*/
}
