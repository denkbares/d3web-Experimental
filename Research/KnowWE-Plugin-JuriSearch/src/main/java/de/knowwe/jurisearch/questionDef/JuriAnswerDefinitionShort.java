package de.knowwe.jurisearch.questionDef;

import java.util.List;

import de.d3web.jurisearch.JuriRule;
import de.d3web.we.object.AnswerDefinition;
import de.d3web.we.object.QuestionDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.jurisearch.questionDef.QuestionDefinitionArea.QuestionDefinitionContent;
import de.knowwe.jurisearch.questionDef.QuestionDefinitionArea.QuestionTermDefinitionLine.QAreaQuestionDefinition;

public class JuriAnswerDefinitionShort extends AnswerDefinition {

	public static final char YES = 'j';
	public static final char NO = 'n';
	public static final char MAYBE = 'v';

	public JuriAnswerDefinitionShort() {
		this.setSectionFinder(new RegexSectionFinder("[" + YES + NO + MAYBE + "]"));
	}

	@Override
	public int getPosition(Section<? extends AnswerDefinition> s) {
		Section<QuestionDefinitionContent> qdc = Sections.findAncestorOfType(s,
				QuestionDefinitionContent.class);
		List<Section<JuriAnswerDefinitionShort>> answers = Sections.findSuccessorsOfType(qdc,
				JuriAnswerDefinitionShort.class);
		return answers.indexOf(s);
	}

	@Override
	public String getAnswerName(Section<? extends AnswerDefinition> answerDefinition) {
		char c = KnowWEUtils.trimQuotes(answerDefinition.getText()).charAt(0);
		switch (c) {
		case YES:
			return JuriRule.YES.getName();
		case NO:
			return JuriRule.NO.getName();
		case MAYBE:
			return JuriRule.MAYBE.getName();
		default:
			return null;
		}
	}

	@Override
	public Section<? extends QuestionDefinition> getQuestionSection(Section<? extends AnswerDefinition> s) {
		Section<QuestionDefinitionContent> qdc = Sections.findAncestorOfType(s,
				QuestionDefinitionContent.class);
		return Sections.findSuccessor(qdc, QAreaQuestionDefinition.class);
	}
}