package de.d3web.we.diaflux.evaluators;

import java.util.HashSet;
import java.util.Set;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.datamanagement.MOValue;

public class EqualEval extends TerminalEvaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondEqual.class);
	}

	// TODO richtige values rausfinden
	@Override
	public EvalResult evaluate(Condition condition) {

		CondEqual conEqual = (CondEqual) condition;
		Value value = conEqual.getValue();
		String name = conEqual.getQuestion().getName();
		Set<String> possibleValues = new HashSet<String>();
		Set<String> actualValues = new HashSet<String>();

		if (value instanceof ChoiceValue) {
			ChoiceValue cValue = (ChoiceValue) value;
			actualValues.add(cValue.getChoiceID().getText());
		}
		else if (value instanceof MultipleChoiceValue) {
			MultipleChoiceValue cValue = (MultipleChoiceValue) value;
			for (ChoiceID choiceID : cValue.getChoiceIDs()) {
				actualValues.add(choiceID.getText());
			}
		}

		Domain<MOValue> domain = new Domain<MOValue>();
		MOValue mo = new MOValue(possibleValues, actualValues, true);
		domain.add(mo);

		EvalResult result = new EvalResult();
		result.add(name, domain);
		return result;
	}

}
