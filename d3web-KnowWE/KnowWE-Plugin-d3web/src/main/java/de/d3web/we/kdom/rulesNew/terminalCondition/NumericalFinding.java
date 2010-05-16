package de.d3web.we.kdom.rulesNew.terminalCondition;

import java.util.List;

import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumGreaterEqual;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.inference.condition.CondNumLessEqual;
import de.d3web.core.inference.condition.TerminalCondition;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.constraint.SingleChildConstraint;
import de.d3web.we.kdom.objects.QuestionRef;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.OneOfStringEnumUnquotedFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.utils.SplitUtility;

/**
 * A type implementing a cond-num TerminalCondition {@link TerminalCondition} It
 * has a allowed list of comparators
 *
 * syntax: <questionID> <comp> <number> e.g.: mileage evaluation >= 130
 *
 * @author Jochen
 *
 */
public class NumericalFinding extends D3webTerminalCondition<NumericalFinding> {

	private static String[] comparators = {
			"<=", ">=", "==", "<", ">", };

	@Override
	protected void init() {
		this.setSectionFinder(new NumericalFindingFinder());

		// comparator
		Comparator comparator = new Comparator();
		comparator.setSectionFinder(new OneOfStringEnumUnquotedFinder(comparators));
		this.childrenTypes.add(comparator);

		// question
		QuestionRef question = new NumQuestionRefImpl();
		AllTextFinderTrimmed questionFinder = new AllTextFinderTrimmed();
		questionFinder.addConstraint(SingleChildConstraint.getInstance());
		question.setSectionFinder(questionFinder);
		this.childrenTypes.add(question);

		// answer
		Number num = new Number();
		num.setSectionFinder(new AllTextFinderTrimmed());
		this.childrenTypes.add(num);
	}

	class NumericalFindingFinder extends SectionFinder {

		private final AllTextFinderTrimmed textFinder = new AllTextFinderTrimmed();

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section father) {
			for (String comp : comparators) {
				if (SplitUtility.containsUnquoted(text, comp)) {

					return textFinder.lookForSections(text,
							father);
				}
			}

			return null;
		}

	}

	class Comparator extends DefaultAbstractKnowWEObjectType {

	}

	class NumQuestionRefImpl extends QuestionRef {


	}

	@Override
	public TerminalCondition getTerminalCondition(Section<NumericalFinding> s) {
		Section<QuestionRef> qRef = s.findSuccessor(QuestionRef.class);

		Section<Number> numberSec = s.findSuccessor(Number.class);

		String comparator = s.findSuccessor(Comparator.class).getOriginalText();

		Double number = numberSec.get().getNumber(numberSec);

		Question q = qRef.get().getObject(qRef);

		if (number != null && q != null && q instanceof QuestionNum) {

			QuestionNum qnum = (QuestionNum) q;

			if (comparator.equals("<=")) {
				return new CondNumLessEqual(qnum, number);
			}
			else if (comparator.equals(">=")) {
				return new CondNumGreaterEqual(qnum, number);
			}
			else if (comparator.equals("<")) {
				return new CondNumLess(qnum, number);
			}
			else if (comparator.equals(">")) {
				return new CondNumGreater(qnum, number);
			}
			else if (comparator.equals("==")) {
				return new CondNumEqual(qnum, number);
			}

		}
		return null;
	}
}
