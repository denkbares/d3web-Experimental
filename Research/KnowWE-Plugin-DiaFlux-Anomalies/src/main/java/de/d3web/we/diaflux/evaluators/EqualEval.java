package de.d3web.we.diaflux.evaluators;

import java.util.HashSet;
import java.util.Set;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.datamanagement.MCValue;
import de.d3web.we.diaflux.datamanagement.MOValue;

public class EqualEval extends TerminalEvaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondEqual.class);
	}

	// TODO richtige values rausfinden
	@Override
	public EvalResult evaluate(Condition condition) {
		EvalResult result = new EvalResult();
//		Domain<MOValue> domain = new Domain<MOValue>();
		CondEqual conEqual = (CondEqual) condition;
		Value value = conEqual.getValue();
		String name = conEqual.getQuestion().getName();
		Set<String> possibleValues = new HashSet<String>();
		Set<String> actualValues = new HashSet<String>();

		if(value instanceof ChoiceValue) {
			Domain<MOValue> domain = new Domain<MOValue>();
//			Domain<OCValue> domain = new Domain<OCValue>();
			ChoiceValue cValue = (ChoiceValue) value;
			actualValues.add(cValue.getChoiceID().getText());
//			OCValue oc = new OCValue(possibleValues, actualValues);
			MOValue mo = new MOValue(possibleValues, actualValues, true);
			domain.add(mo);
			result.add(name, domain);

		}
		if(value instanceof MultipleChoiceValue) {
			MultipleChoiceValue cValue = (MultipleChoiceValue) value;

			Domain<MCValue> domain = new Domain<MCValue>();
			result.add(name, domain);
		}
//		if(value instanceof ChoiceValue) {
//			ChoiceValue cValue = (ChoiceValue) value;
//		}
//		if(conEqual.getQuestion() instanceof QuestionMC) {
//			domain.add(new MCValue(possibleValues, actualValues));
//
//		} else if(conEqual.getQuestion() instanceof QuestionOC) {
//			domain.add(new OCValue(possibleValues, actualValues));
//
//		}
		return result;
	}

}
