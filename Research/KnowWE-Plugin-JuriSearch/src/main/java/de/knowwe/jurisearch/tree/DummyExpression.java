package de.knowwe.jurisearch.tree;

import java.util.Collection;

import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.jurisearch.JuriModel;
import de.d3web.jurisearch.JuriRule;
import de.d3web.we.object.QASetDefinition;
import de.d3web.we.object.QuestionDefinition;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.jurisearch.BracketRenderer;
import de.knowwe.jurisearch.EmbracedContent;
import de.knowwe.jurisearch.Error;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.EmbracedContentFinder;

class DummyExpression extends AbstractType {

	public static final Object DUMMY = "dummy";
	public static final String NEGATION_FLAG = "nein";

	DummyExpression() {
		SectionFinder sf = new RegexSectionFinder(".*" + EmbracedContent.BRACKET_OPEN_REGEX
				+ DUMMY + EmbracedContent.BRACKET_CLOSE_REGEX + ".*");
		ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
		csf.addConstraint(SingleChildConstraint.getInstance());
		csf.addConstraint(AtMostOneFindingConstraint.getInstance());
		this.setSectionFinder(csf);
		this.addChildType(new DummyFlag());
		this.addChildType(new Operator());
		this.addChildType(new NegationFlag());

		// if dummy, all other brackets are ignored
		Error error = new Error();
		error.setSectionFinder(new EmbracedContentFinder(EmbracedContent.BRACKET_OPEN,
				EmbracedContent.BRACKET_CLOSE));
		this.addChildType(error);

		this.addChildType(new DummyQuestion());
	}

	class DummyQuestion extends QuestionDefinition {

		DummyQuestion() {
			AllTextFinderTrimmed allTextFinderTrimmed = new AllTextFinderTrimmed();
			ConstraintSectionFinder csf = new ConstraintSectionFinder(allTextFinderTrimmed);
			csf.addConstraint(SingleChildConstraint.getInstance());

			this.setSectionFinder(csf);
			this.addSubtreeHandler(Priority.HIGHER, new CreateDummyQuestionHandler());
			this.setRenderer(StyleRenderer.Question);

			this.addChildType(new QuestionIdentifier());
		}

		@Override
		public QuestionType getQuestionType(Section<QuestionDefinition> s) {
			return QuestionDefinition.QuestionType.OC;
		}

		@Override
		public int getPosition(Section<QuestionDefinition> s) {
			return 0;
		}

		@Override
		public Section<? extends QASetDefinition> getParentQASetSection(Section<? extends QuestionDefinition> qdef) {
			return null;
		}
	}

	class DummyFlag extends AbstractType {

		DummyFlag() {
			SectionFinder sf = new RegexSectionFinder(EmbracedContent.BRACKET_OPEN_REGEX
					+ DUMMY + EmbracedContent.BRACKET_CLOSE_REGEX);
			ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
			csf.addConstraint(SingleChildConstraint.getInstance());
			csf.addConstraint(AtMostOneFindingConstraint.getInstance());

			this.setSectionFinder(csf);
			this.setRenderer(new BracketRenderer());
			this.addChildType(new EmbracedContent());
		}
	}

	class NegationFlag extends AbstractType {

		NegationFlag() {
			SectionFinder sf = new RegexSectionFinder(EmbracedContent.BRACKET_OPEN_REGEX
					+ NEGATION_FLAG + EmbracedContent.BRACKET_CLOSE_REGEX);
			ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
			csf.addConstraint(SingleChildConstraint.getInstance());
			csf.addConstraint(AtMostOneFindingConstraint.getInstance());

			this.setSectionFinder(csf);
			this.setRenderer(new BracketRenderer());
		}
	}

	class CreateDummyQuestionHandler extends D3webSubtreeHandler<QuestionDefinition> {

		@Override
		public Collection<Message> create(Article article, Section<QuestionDefinition> section) {
			QuestionOC dummyQuestion = (QuestionOC) section.get().getTermObject(article, section);

			// add default alternatives
			dummyQuestion.addAlternative(JuriRule.YES);
			dummyQuestion.addAlternative(JuriRule.NO);
			dummyQuestion.addAlternative(JuriRule.MAYBE);

			dummyQuestion.getInfoStore().addValue(JuriModel.DUMMY, true);

			// return success message
			return null;
		}

	}
}