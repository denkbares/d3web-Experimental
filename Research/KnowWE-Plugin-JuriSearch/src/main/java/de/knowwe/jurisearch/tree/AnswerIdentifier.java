package de.knowwe.jurisearch.tree;

import de.d3web.we.object.AnswerReference;
import de.d3web.we.object.QuestionReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;

public class AnswerIdentifier extends AnswerReference {

	public AnswerIdentifier() {
		super();

		SectionFinder sf = new RegexSectionFinder("[^\\[\\]]+");

		// SectionFinder sf = new EmbracedContentFinder('(', ')', true);
		ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
		csf.addConstraint(SingleChildConstraint.getInstance());
		csf.addConstraint(AtMostOneFindingConstraint.getInstance());
		this.setSectionFinder(csf);
	}

	@Override
	public Section<QuestionReference> getQuestionSection(Section<? extends AnswerReference> s) {
		Section<JuriTreeExpression> exp = Sections.findAncestorOfType(s,
				JuriTreeExpression.class);
		Section<QuestionReference> ref = Sections.findChildOfType(exp, QuestionReference.class);
		return ref;
	}

}