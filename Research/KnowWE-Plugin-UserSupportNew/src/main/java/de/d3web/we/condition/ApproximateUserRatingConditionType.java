/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.condition;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.core.inference.condition.CondSolutionConfirmed;
import de.d3web.core.inference.condition.CondSolutionRejected;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.we.kdom.condition.D3webCondition;
import de.d3web.we.kdom.condition.UserRatingType;
import de.d3web.we.object.ApproximateSolutionReference;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.utils.Patterns;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.sectionFinder.StringSectionFinderUnquoted;

/**
 * A condition for user evaluations of solutions.
 * 
 * @author Reinhard Hatko
 * @created 23.11.2010
 */
public class ApproximateUserRatingConditionType extends D3webCondition<ApproximateUserRatingConditionType> {

	enum UserEvaluation {
		CONFIRMED, REJECTED
	}


	@Override
	protected void init() {
		setSectionFinder(new SectionFinder() {

			private Pattern evalPattern;
			private Pattern conditionPattern;

			{
				conditionPattern = Pattern.compile(Patterns.D3IDENTIFIER + "\\s*=\\s*([\\w]+)");
				evalPattern = Pattern.compile("(rejected|confirmed|bestätigt|abgelehnt)",
						Pattern.CASE_INSENSITIVE);

			}

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
				Matcher matcher = conditionPattern.matcher(text);

				if (!matcher.matches())
					return null;
				else {
					String evaluation = matcher.group(1);


					if (evalPattern.matcher(evaluation).matches()) {
						return Arrays.asList(new SectionFinderResult(matcher.start(0),
								matcher.end(0)));

					}
					else return null;

				}

			}
		});


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

		// evaluation
		this.childrenTypes.add(new UserRatingType());

	}

	@Override
	protected Condition createCondition(KnowWEArticle article, Section s) {
		Section<ApproximateSolutionReference> sRef = Sections.findSuccessor(s, ApproximateSolutionReference.class);
		Section<ApproximateUserRatingType> ratingSec = Sections.findSuccessor(s, ApproximateUserRatingType.class);
		if (sRef != null && ratingSec != null) {
			Solution solution = sRef.get().getTermObject(article, sRef);
			UserEvaluation eval = ApproximateUserRatingType.getUserEvaluationType(ratingSec);
			if (solution != null && eval != null) {
				if (eval.equals(UserEvaluation.CONFIRMED)) return new CondSolutionConfirmed(
						solution);
				else if (eval.equals(UserEvaluation.REJECTED)) return new CondSolutionRejected(
						solution);
			}
		}

		return null;
	}

}
