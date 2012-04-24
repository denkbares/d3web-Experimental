package de.d3web.we.diaflux.evaluators;

import java.util.LinkedList;
import java.util.List;


public class EvaluatorManager {
	
	private static  EvaluatorManager evalManager;
	
	private List<Evaluator> EvalList = new LinkedList<Evaluator>();
	
	private EvaluatorManager() {
		//Numeric Evaluators
		EvalList.add(new NumEqualEval());
		EvalList.add(new NumGreaterEval());
		EvalList.add(new NumLessEval());
		EvalList.add(new NumEqualGreatEval());
		EvalList.add(new NumEqualLessEval());
		
		//One/Multiple Choice Evaluators
		EvalList.add(new EqualEval());

		//Non terminal Evaluators
		EvalList.add(new AndEval());
		EvalList.add(new NotEval());
		EvalList.add(new OrEval());
		
		//
		EvalList.add(new KnownEval());
		EvalList.add(new UnknownEval());
		EvalList.add(new AnsweredEval());
		
	}

	public static  EvaluatorManager getEvalManager() {
		if(null == evalManager) {
			evalManager = new EvaluatorManager();
		}
		return evalManager;
	}
	
	public boolean addEvaluator(Evaluator evaluator) {
		return EvalList.add(evaluator);
	}
	
	public List<Evaluator> getEvaluator() {
		return EvalList;
	}
}
