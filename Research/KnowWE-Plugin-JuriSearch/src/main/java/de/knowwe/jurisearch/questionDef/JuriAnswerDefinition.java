package de.knowwe.jurisearch.questionDef;

import java.util.List;

import de.d3web.we.object.QuestionDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.jurisearch.EmbracedContent;
import de.knowwe.jurisearch.questionDef.QuestionDefinitionArea.QuestionDefinitionContent;
import de.knowwe.jurisearch.questionDef.QuestionDefinitionArea.QuestionTermDefinitionLine.QAreaQuestionDefinition;
import de.knowwe.kdom.sectionFinder.EmbracedContentFinder;

public class JuriAnswerDefinition extends de.d3web.we.object.AnswerDefinition {

	JuriAnswerDefinition() {
		this.setSectionFinder(new EmbracedContentFinder(EmbracedContent.BRACKET_OPEN,
				EmbracedContent.BRACKET_CLOSE, true));
	}

	@Override
	public int getPosition(Section<? extends de.d3web.we.object.AnswerDefinition> s) {
		Section<QuestionDefinitionContent> qdc = Sections.findAncestorOfType(s,
				QuestionDefinitionContent.class);
		List<Section<JuriAnswerDefinition>> answers = Sections.findSuccessorsOfType(qdc,
				JuriAnswerDefinition.class);
		return answers.indexOf(s);
	}

	@Override
	public Section<? extends QuestionDefinition> getQuestionSection(Section<? extends de.d3web.we.object.AnswerDefinition> s) {
		Section<QuestionDefinitionContent> qdc = Sections.findAncestorOfType(s,
				QuestionDefinitionContent.class);
		return Sections.findSuccessor(qdc, QAreaQuestionDefinition.class);
	}
}