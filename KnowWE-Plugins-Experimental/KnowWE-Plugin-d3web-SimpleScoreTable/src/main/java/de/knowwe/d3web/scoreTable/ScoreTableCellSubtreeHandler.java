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
import de.d3web.we.knowledgebase.D3webCompiler;
import de.d3web.we.reviseHandler.D3webHandler;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.KnowWEUtils;

public class ScoreTableCellSubtreeHandler implements D3webHandler<ScoreCell> {

	private static final String ruleStoreKey = "rule-store-key";

	public ScoreTableCellSubtreeHandler() {
		// this.setIgnorePackageCompile(true);
	}

	@Override
	public Collection<Message> create(D3webCompiler compiler,
									  Section<ScoreCell> section) {

		List<Message> messages = new ArrayList<>();

		if (section.getText().trim().isEmpty()) {
			return messages;
		}

		// line of current score-cell
		Section<ContentLine> line = Sections.ancestor(section, ContentLine.class);

		// condition at beginning of that line
		Section<CondCell> condition = Sections.successor(line, CondCell.class);

		// column index to calculate solutionSection cell
		int columnIndex = Sections.successors(line, ScoreCell.class).indexOf(section);
		Section<SimpleScoreTable> table = Sections.ancestor(section,
				SimpleScoreTable.class);
		Section<HeaderLine> headerLine = Sections.child(table, HeaderLine.class);

		if (headerLine == null) {
			messages.add(Messages.error("No valid table found"));
			return messages;
		}

		List<Section<SolutionCell>> solutionCells = Sections.successors(headerLine,
				SolutionCell.class);
		Section<? extends Type> solutionCell = solutionCells.get(columnIndex);
		Section<Solution> solutionSection = Sections.successor(solutionCell, Solution.class);

		if (solutionSection == null) {
			messages.add(Messages.noSuchObjectError("Solution not found"));
		}
		if (condition == null) {
			messages.add(Messages.noSuchObjectError("Condition not found"));
		}

		if (solutionSection != null && condition != null) {
			de.d3web.core.knowledge.terminology.Solution solution = solutionSection.get().getTermObject(compiler,
					solutionSection);

			if (solution == null) return messages;

			Condition d3webCond = KDOMConditionFactory.createCondition(compiler, condition);

			Score score = D3webUtils.getScoreForString(section.getText().trim());
			if (score == null) return messages;

			ActionHeuristicPS action = new ActionHeuristicPS();
			action.setSolution(solution);
			action.setScore(score);

			Rule rule = RuleFactory.createRule(action, d3webCond,
					null, PSMethodHeuristic.class);
			if (rule == null) {
				messages.add(Messages.error("Unable to create rule for line '" + condition
						+ "' and column '" + solutionSection + "'"));
			}
			else {
				KnowWEUtils.storeObject(compiler, section, ruleStoreKey, rule);
			}
		}

		return messages;
	}

}
