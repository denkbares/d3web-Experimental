package de.knowwe.jurisearch.questionDef;

import java.util.Collection;
import java.util.regex.Pattern;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.jurisearch.questionDef.QuestionDefinitionArea.QuestionDefinitionContent;
import de.knowwe.jurisearch.questionDef.QuestionDefinitionArea.QuestionTermDefinitionLine.QAreaQuestionDefinition;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.renderer.StyleRenderer;

public class ExplanationText extends AbstractType {

	public ExplanationText() {
		ConstraintSectionFinder csf = new ConstraintSectionFinder(
				new RegexSectionFinder("(Erläuterung:)?(.*)",
						Pattern.MULTILINE | Pattern.DOTALL, 2));
		csf.addConstraint(AtMostOneFindingConstraint.getInstance());
		this.setSectionFinder(csf);
		this.setRenderer(new StyleRenderer("color:blue;"));
		this.addSubtreeHandler(new ExplanationSubtreeHandler());
	}

	class ExplanationSubtreeHandler extends
			D3webSubtreeHandler<ExplanationText> {

		@Override
		public Collection<Message> create(Article article,
				Section<ExplanationText> section) {
			// TODO Auto-generated method stub
			String text = section.getText();
			String html = Environment.getInstance().getWikiConnector().renderWikiSyntax(text, null);

			Section<QuestionDefinitionContent> qdc = Sections.findAncestorOfType(section,
					QuestionDefinitionContent.class);
			Section<QAreaQuestionDefinition> qDef = Sections.findSuccessor(qdc,
					QAreaQuestionDefinition.class);

			Question question = qDef.get().getTermObject(article, qDef);
			question.getInfoStore().addValue(ProKEtProperties.POPUP, html);

			return null;
		}
	}
}