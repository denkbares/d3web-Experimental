package de.knowwe.jurisearch.tree;

import de.d3web.we.object.AnswerReference;
import de.d3web.we.object.QuestionReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.jurisearch.BracketContent;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.sectionFinder.EmbracedContentFinder;

public class AnswerIdentifier extends AnswerReference {

	public AnswerIdentifier() {
		super();

		SectionFinder sf = new EmbracedContentFinder(BracketContent.BRACKET_OPEN_CHAR,
				BracketContent.BRACKET_CLOSE_CHAR, true);
		ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
		csf.addConstraint(SingleChildConstraint.getInstance());
		csf.addConstraint(AtMostOneFindingConstraint.getInstance());

		this.setSectionFinder(csf);
	}

	@Override
	public Section<QuestionReference> getQuestionSection(Section<? extends AnswerReference> s) {
		Section<JuriTreeExpression> exp = Sections.findAncestorOfType(s, JuriTreeExpression.class);
		Section<QuestionReference> ref = Sections.findChildOfType(exp, QuestionReference.class);
		return ref;
	}

}
