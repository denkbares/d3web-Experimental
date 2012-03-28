package de.knowwe.jurisearch.tree;

import java.util.Collection;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.jurisearch.JuriModel;
import de.d3web.jurisearch.JuriRule;
import de.d3web.we.object.QASetDefinition;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.compile.terminology.TerminologyManager;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.jurisearch.BracketContent;
import de.knowwe.jurisearch.BracketRenderer;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.renderer.StyleRenderer;

class DummyExpression extends AbstractType {

	public static final Object DUMMY = "dummy";

	DummyExpression() {
		SectionFinder sf = new RegexSectionFinder(".*" + BracketContent.BRACKET_OPEN
				+ DUMMY + BracketContent.BRACKET_CLOSE + ".*");
		ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
		csf.addConstraint(SingleChildConstraint.getInstance());
		csf.addConstraint(AtMostOneFindingConstraint.getInstance());
		this.setSectionFinder(csf);
		this.addChildType(new DummyFlag());
		this.addChildType(new DummyQuestion());
	}

	class DummyQuestion extends QASetDefinition<Question> {

		DummyQuestion() {
			AllTextFinderTrimmed allTextFinderTrimmed = new AllTextFinderTrimmed();
			ConstraintSectionFinder csf = new ConstraintSectionFinder(allTextFinderTrimmed);
			csf.addConstraint(SingleChildConstraint.getInstance());

			this.setSectionFinder(csf);
			this.addSubtreeHandler(Priority.HIGHER, new CreateDummyQuestionHandler());
			this.setRenderer(StyleRenderer.Question);
			this.setOrderSensitive(true);
			this.addChildType(new QuestionIdentifier());
		}

		@Override
		public Class<?> getTermObjectClass(Section<? extends SimpleTerm> section) {
			return Question.class;
		}
	}

	class DummyFlag extends AbstractType {

		DummyFlag() {
			SectionFinder sf = new RegexSectionFinder(BracketContent.BRACKET_OPEN
					+ DUMMY + BracketContent.BRACKET_CLOSE);
			ConstraintSectionFinder csf = new ConstraintSectionFinder(sf);
			csf.addConstraint(SingleChildConstraint.getInstance());
			csf.addConstraint(AtMostOneFindingConstraint.getInstance());

			this.setSectionFinder(csf);
			this.setRenderer(new BracketRenderer());
			this.addChildType(new BracketContent());
		}
	}

	class CreateDummyQuestionHandler extends D3webSubtreeHandler<QASetDefinition<Question>> {

		@Override
		public Collection<Message> create(Article article, Section<QASetDefinition<Question>> section) {

			String name = section.get().getTermIdentifier(section);

			Class<?> termObjectClass = section.get().getTermObjectClass(section);
			TerminologyManager terminologyHandler = KnowWEUtils.getTerminologyManager(article);
			terminologyHandler.registerTermDefinition(section, termObjectClass, name);

			Collection<Message> msgs = section.get().canAbortTermObjectCreation(
					article, section);
			if (msgs != null) return msgs;

			KnowledgeBase kb = getKB(article);

			Section<? extends QASetDefinition<Question>> parentQASetSection = section;

			QASet parent = null;
			if (parentQASetSection != null) {
				parent = parentQASetSection.get().getTermObject(article,
						parentQASetSection);
			}
			if (parent == null) {
				parent = kb.getRootQASet();
			}

			QuestionOC dummyQuestion = new QuestionOC(parent, name);

			// add default alternatives
			dummyQuestion.addAlternative(JuriRule.YES);
			dummyQuestion.addAlternative(JuriRule.NO);
			dummyQuestion.addAlternative(JuriRule.MAYBE);

			dummyQuestion.getInfoStore().addValue(JuriModel.DUMMY, true);

			// return success message
			return Messages.asList(Messages.objectCreatedNotice(
					termObjectClass.getSimpleName() + " " + name));

		}

	}
}