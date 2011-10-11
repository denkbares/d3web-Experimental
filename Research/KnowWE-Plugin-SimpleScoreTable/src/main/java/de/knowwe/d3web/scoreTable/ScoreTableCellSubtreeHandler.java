package de.knowwe.d3web.scoreTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.manage.RuleFactory;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;
import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.KDOMConditionFactory;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.report.message.NoSuchObjectError;
import de.knowwe.report.message.ObjectCreatedMessage;

public class ScoreTableCellSubtreeHandler extends D3webSubtreeHandler<ScoreCell>{

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article,
			Section<ScoreCell> section) {
		
		List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>();
		
		// line of current score-cell
		Section<ContentLine> line = Sections.findAncestorOfType(section, ContentLine.class);
		
		// condition at beginning of that line
		Section<CondCell> condition = Sections.findSuccessor(line, CondCell.class);
		
		//column index to calculate solution cell
		int columnIndex = line.getChildren().indexOf(section);
		Section<SimpleScoreTable> table = Sections.findAncestorOfExactType(section, SimpleScoreTable.class);
		Section<HeaderLine> headerLine = Sections.findChildOfType(table, HeaderLine.class);
		Section<? extends Type> solutionCell = headerLine.getChildren().get(columnIndex);
		Section<Solution> solution = Sections.findSuccessor(solutionCell, Solution.class);
		
		
		if(solution == null) {
			messages.add(new NoSuchObjectError("Solution not found"));
		}
		if( condition == null) {
			messages.add(new NoSuchObjectError("Condition not found"));
		}

		if(solution != null && condition != null) {
			de.d3web.core.knowledge.terminology.Solution s = solution.get().getTermObject(article, solution);
			
			Condition d3webCond = KDOMConditionFactory.createCondition(article, condition);
			
			Score score = D3webUtils.getScoreForString(section.getOriginalText());
			if (solution == null || score == null) return null;
			ActionHeuristicPS a = new ActionHeuristicPS();
			a.setSolution(s);
			a.setScore(score);
			
			Rule r = RuleFactory.createRule(a, d3webCond,
					null, a.get().getActionPSContext());
			if (r != null) {
				KnowWEUtils.storeObject(article, s, ruleStoreKey, r);
				return Arrays
						.asList((KDOMReportMessage) new ObjectCreatedMessage(
								"Rule"));
			}
		}
		
		
		return messages;
	}

}
