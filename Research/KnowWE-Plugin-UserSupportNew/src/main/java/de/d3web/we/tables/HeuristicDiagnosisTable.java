/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.we.tables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondMofN;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;
import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.KDOMConditionFactory;
import de.d3web.we.kdom.xcl.list.ListSolutionType;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.AnonymousTypeInvisible;
import de.knowwe.kdom.sectionFinder.StringSectionFinderUnquoted;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;

/**
 * 
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class HeuristicDiagnosisTable extends ITable {

	public HeuristicDiagnosisTable() {
		this.sectionFinder = new AllTextSectionFinder();
		this.addSubtreeHandler(Priority.LOWEST, new HeuristicDiagnosisTableSubtreeHandler());
		this.addChildType(new ListSolutionType());

		// cut the optional closing }
		AnonymousTypeInvisible closing = new AnonymousTypeInvisible("closing-bracket");
		closing.setSectionFinder(new StringSectionFinderUnquoted("}"));
		this.addChildType(closing);

		this.addChildType(new InnerTable());
	}

	/**
	 * Handles the creation of rules from HeuristicDiagnosisTableMarkup
	 * 
	 * @author Johannes Dienst
	 * @created 10.11.2011
	 */
	public class HeuristicDiagnosisTableSubtreeHandler extends GeneralSubtreeHandler<HeuristicDiagnosisTable> {

		@Override
		public Collection<Message> create(KnowWEArticle article, Section<HeuristicDiagnosisTable> heuristicSec) {

			Section<InnerTable> innerTable =
					Sections.findChildOfType(heuristicSec, InnerTable.class);
			if (Sections.findSuccessorsOfType(innerTable, TableCell.class).isEmpty()) return null;

			// TODO Right KnowledgeBase?
			// Section<HeuristicDiagnosisTableMarkup> mark =
			// Sections.findAncestorOfExactType(
			// heuristicSec, HeuristicDiagnosisTableMarkup.class);
			// String packageName = DefaultMarkupType.getAnnotation(mark,
			// "package");
			// KnowledgeBase kb = D3webUtils.getKB(heuristicSec.getWeb(),
			// packageName + " - master");;
			KnowWEArticle compilingArticle = KnowWEUtils.getCompilingArticles(heuristicSec).iterator().next();
			KnowledgeBase kb = D3webUtils.getKnowledgeBase(heuristicSec.getWeb(),
					compilingArticle.getTitle());

			// First create solution if necessary
			// Create Rules: 1. Create Solution if necessary
			Solution solution = TableUtils.findSolutionInKB(heuristicSec, kb);

			// Create all Conditions: 1st column
			// TODO First + Second Cell is no Question: Removed it! But what if
			// empty?
			// TODO no checks or whatsoever. Write security check!
			List<Section<TableCellFirstColumn>> firstColumn =
					Sections.findSuccessorsOfType(innerTable, TableCellFirstColumn.class);
			firstColumn.remove(0);
			firstColumn.remove(0);

			LinkedList<Condition> conditionList = new LinkedList<Condition>();
			for (Section<TableCellFirstColumn> cell : firstColumn) {
				Section<CompositeCondition> cond = Sections.findChildOfType(cell,
						CompositeCondition.class);
				Condition d3Cond = KDOMConditionFactory.createCondition(cell.getArticle(), cond);
				conditionList.add(d3Cond);
			}

			// Collect cells for columns
			// TODO Check if header misses 1st Tablecell
			int cellCount = TableUtils.getMaximumTableCellCount(innerTable);

			// Do for every heuristicRule
			LinkedList<Section<TableCell>> column = null;
			List<Section<TableHeaderCell>> headerCells = Sections.findSuccessorsOfType(
					heuristicSec, TableHeaderCell.class);

			for (int i = 1; i < cellCount; i++) {
				column = new LinkedList<Section<TableCell>>(
						TableUtils.getColumnCells(
								i, Sections.findChildOfType(heuristicSec, InnerTable.class)));

				// get conjunction type
				String conjunctionType = headerCells.get(i).getText().trim();

				// get scoring
				String scoring = column.removeFirst().getText().trim();

				// create rule choices
				Section<TableCell> cell = null;
				List<Integer> choices = new ArrayList<Integer>();
				for (int j = 0; j < column.size(); j++) {
					cell = column.removeFirst();
					String cellText = cell.getText().trim();

					if (cellText.equals("+")) choices.add(1);
					else if (cellText.equals("-")) choices.add(0);
					else choices.add(null);
				}

				// Build condition
				Condition condition = null;
				List<Condition> terms = new ArrayList<Condition>();
				for (int j = 0; j < choices.size(); j++) {
					if (choices.get(j) == null || conditionList.get(j) == null) continue;
					else if (choices.get(j) == 1) terms.add(conditionList.get(j));
					else if (choices.get(j) == 0) terms.add(new CondNot(conditionList.get(j)));
				}

				// There are three possible conjunction Types
				// AND
				// OR
				// x from y with x<=y
				if (conjunctionType.equals("AND")) {
					condition = new CondAnd(terms);
				}

				else if (conjunctionType.equals("OR")) {
					condition = new CondOr(terms);
				}

				else if (conjunctionType.contains("FROM")) {
					String[] split = conjunctionType.split("FROM");
					int m = Integer.parseInt(split[0].trim());
					int n = Integer.parseInt(split[1].trim());
					condition = new CondMofN(terms, m, n);
				}

				// TODO add right score here
				ActionHeuristicPS action = new ActionHeuristicPS();
				action.setSolution(solution);
				Score score = Score.N1;

				try {
					score = de.d3web.core.io.utilities.Util.getScore(scoring);
				}
				catch (IOException e) {
					// Do nothing!
				}
				action.setScore(score);

				// Create Rule
				RuleSet ruleSet = new RuleSet();
				if ((condition != null) && (action != null)) {
					Rule rule = new Rule(PSMethodAbstraction.class);
					rule.setAction(action);
					rule.setCondition(condition);
					rule.setException(null);
					ruleSet.addRule(rule);
				}

			}
			return null;
		}

	}

}
