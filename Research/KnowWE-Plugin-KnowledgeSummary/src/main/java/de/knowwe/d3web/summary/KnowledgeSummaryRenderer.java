package de.knowwe.d3web.summary;

import java.util.Collection;
import java.util.List;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyManager;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.we.knowledgebase.D3webCompiler;
import de.d3web.xcl.XCLContributedModelSet;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;

/**
 * KnowledgeSummaryRenderer counts and shows all questions, solutions and rules of  a knowledgeBase
 * @author Veronika Sehne on 23.04.2014.
 */
public class KnowledgeSummaryRenderer extends DefaultMarkupRenderer {
	@Override
	public void renderContents(Section<?> section, UserContext user, RenderResult result) {
		D3webCompiler compiler = Compilers.getCompiler(section, D3webCompiler.class);
		KnowledgeBase knowledgeBase = compiler.getKnowledgeBase();
		TerminologyManager manager = knowledgeBase.getManager();
		List<Question> questions = manager.getQuestions();
		List<Solution> solutions = manager.getSolutions();
		Collection<KnowledgeSlice> knowledgeSlices = knowledgeBase.getAllKnowledgeSlices();
		Collection<Rule> rules;
		int ruleNums = 0;
		int xcl = 0;
		for (KnowledgeSlice knowledgeSlice : knowledgeSlices) {
			if (knowledgeSlice instanceof RuleSet) {
				rules = ((RuleSet) knowledgeSlice).getRules();
				ruleNums = ruleNums + rules.size();
			}
			if (knowledgeSlice instanceof XCLContributedModelSet) {
				xcl = xcl + ((XCLContributedModelSet) knowledgeSlice).getModels().size();
			}
		}
		int questionNums = 0;
		int questionTexts = 0;
		int questionDates = 0;
		int questionChoices = 0;
		int questionOCs = 0;
		int questionMCs = 0;
		for (Question question : questions) {
			if (question.getName().equals("now") || question.getName().equals("start")) {
				continue;
			}
			if (question instanceof QuestionNum) {
				questionNums++;
			}
			else if (question instanceof QuestionText) {
				questionTexts++;
			}
			else if (question instanceof QuestionDate) {
				questionDates++;
			}
			else if (question instanceof QuestionChoice) {
				questionChoices++;
				if (question instanceof QuestionOC) {
					questionOCs++;
				}
				else if (question instanceof QuestionMC) {
					questionMCs++;
				}
			}
		}

		result.append("Questions: " + (questions.size() - 2) + " ( + start and now)");
		result.appendHtml("<br>");
		result.append("\tQuestionNums: " + questionNums);
		result.appendHtml("<br>");
		result.append("\tQuestionTexts: " + questionTexts);
		result.appendHtml("<br>");
		result.append("\tQuestionDates: " + questionDates);
		result.appendHtml("<br>");
		result.append("\tQuestionChoices: " + questionChoices);
		result.appendHtml("<br>");
		result.append("\t\tQuestionOCs: " + questionOCs);
		result.appendHtml("<br>");
		result.append("\t\tQuestionMCs: " + questionMCs);
		result.appendHtml("<br>");
		result.append("Solutions: " + solutions.size());
		result.appendHtml("<br>");
		result.append("Rules: " + ruleNums);
		result.appendHtml("<br>");
		result.append("XCL: " + xcl);
	}
}
