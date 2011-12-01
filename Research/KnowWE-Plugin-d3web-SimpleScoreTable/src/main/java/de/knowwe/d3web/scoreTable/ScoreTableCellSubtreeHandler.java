package de.knowwe.d3web.scoreTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.manage.RuleFactory;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;
import de.d3web.we.kdom.condition.KDOMConditionFactory;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.KnowWEUtils;

public class ScoreTableCellSubtreeHandler extends D3webSubtreeHandler<ScoreCell> {

	private static final String ruleStoreKey = "rule-store-key";

	public ScoreTableCellSubtreeHandler() {
		// this.setIgnorePackageCompile(true);
	}

	@Override
	public Collection<Message> create(KnowWEArticle article,
			Section<ScoreCell> section) {

		List<Message> messages = new ArrayList<Message>();

		if (section.getOriginalText().trim().length() == 0) {
			return messages;
		}

		// line of current score-cell
		Section<ContentLine> line = Sections.findAncestorOfType(section, ContentLine.class);

		// condition at beginning of that line
		Section<CondCell> condition = Sections.findSuccessor(line, CondCell.class);

		// column index to calculate solution cell
		int columnIndex = Sections.findSuccessorsOfType(line, ScoreCell.class).indexOf(section);
		Section<SimpleScoreTable> table = Sections.findAncestorOfExactType(section,
				SimpleScoreTable.class);
		Section<HeaderLine> headerLine = Sections.findChildOfType(table, HeaderLine.class);

		List<Section<SolutionCell>> solutionCells = Sections.findSuccessorsOfType(headerLine,
				SolutionCell.class);
		Section<? extends Type> solutionCell = solutionCells.get(columnIndex);
		Section<Solution> solution = Sections.findSuccessor(solutionCell, Solution.class);

		if (solution == null) {
			messages.add(Messages.noSuchObjectError("Solution not found"));
		}
		if (condition == null) {
			messages.add(Messages.noSuchObjectError("Condition not found"));
		}

		if (solution != null && condition != null) {
			de.d3web.core.knowledge.terminology.Solution s = solution.get().getTermObject(article,
					solution);

			Condition d3webCond = KDOMConditionFactory.createCondition(article, condition);

			Score score = D3webUtils.getScoreForString(section.getOriginalText().trim());
			if (solution == null || score == null) return messages;
			ActionHeuristicPS a = new ActionHeuristicPS();
			a.setSolution(s);
			a.setScore(score);

			Rule r = RuleFactory.createRule(a, d3webCond,
					null, PSMethodHeuristic.class);
			if (r != null) {
				KnowWEUtils.storeObject(article, section, ruleStoreKey, r);
				return Messages.asList(Messages.objectCreatedNotice(
								"Rule"));
			}
		}

		return messages;
	}

}
