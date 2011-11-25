package de.d3web.we.condition;

import java.util.List;

import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.we.kdom.condition.D3webCondition;
import de.d3web.we.kdom.condition.SolutionStateType;
import de.d3web.we.object.ApproximateSolutionReference;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.utils.SplitUtility;
import de.knowwe.core.utils.StringFragment;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.sectionFinder.StringSectionFinderUnquoted;

/**
 * 
 * A condition to check for solution states @see CondDState
 * 
 * @author Jochen
 * @created 26.10.2010
 */
public class ApproximateSolutionStateCond extends D3webCondition<ApproximateSolutionStateCond> {

	public ApproximateSolutionStateCond() {

		this.sectionFinder = new SolutionStateCondFinder();

		// comparator
		AnonymousType comparator = new AnonymousType("equals");
		comparator.setSectionFinder(new StringSectionFinderUnquoted("="));
		this.childrenTypes.add(comparator);

		// solution
		ApproximateSolutionReference sol = new ApproximateSolutionReference();
		ConstraintSectionFinder solutionFinder = new ConstraintSectionFinder(
				new AllTextFinderTrimmed());
		solutionFinder.addConstraint(SingleChildConstraint.getInstance());
		sol.setSectionFinder(solutionFinder);
		this.childrenTypes.add(sol);

		// answer
		ApproximateSolutionStateType rating = new ApproximateSolutionStateType();
		rating.setSectionFinder(new AllTextFinderTrimmed());
		this.childrenTypes.add(rating);
	}

	class SolutionStateCondFinder implements SectionFinder {

		private final AllTextFinderTrimmed textFinder = new AllTextFinderTrimmed();

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section father, Type type) {
			if (SplitUtility.containsUnquoted(text, "=")) {
				List<StringFragment> list = SplitUtility.splitUnquoted(text, "=");
				// Hotfix for AOB when there is nothing behind the "="
				if (list.size() < 2) return null;
				String answer = list.get(1).getContent().trim();

				// check if solution-state can be found
				boolean isSolutionState = false;
				for (String value : SolutionStateType.getPossibleStringValues()) {
					if (answer.trim().equalsIgnoreCase(value)) {
						isSolutionState = true;
						break;
					}

				}

				// return it if answer is NOT a number
				if (isSolutionState) {
					return textFinder.lookForSections(text, father, type);
				}
			}
			return null;
		}

	}

	@Override
	protected Condition createCondition(KnowWEArticle article, Section<ApproximateSolutionStateCond> s) {
		Section<ApproximateSolutionReference> sRef = Sections.findSuccessor(s,
				ApproximateSolutionReference.class);
		Section<SolutionStateType> state = Sections.findSuccessor(s,
				SolutionStateType.class);
		if (sRef != null && state != null) {
			Solution solution = sRef.get().getTermObject(article, sRef);
			Rating.State solutionState = SolutionStateType.getSolutionState(state);
			if (solution != null && solutionState != null) {
				return new CondDState(solution, new Rating(solutionState));
			}
		}

		return null;
	}

}
