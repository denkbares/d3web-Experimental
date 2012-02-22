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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;
import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.KDOMConditionFactory;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
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
 * 
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class CausalDiagnosisScore extends AbstractType {

	public CausalDiagnosisScore() {
		this.sectionFinder = new AllTextSectionFinder();
		this.addSubtreeHandler(Priority.LOWEST, new CausalDiagnosisSubtreeHandler());

		this.addChildType(new TableDescriptionType());

		// cut the optional closing }
		AnonymousTypeInvisible closing = new AnonymousTypeInvisible("closing-bracket");
		closing.setSectionFinder(new StringSectionFinderUnquoted("}"));
		this.addChildType(closing);

		InnerTable iTable = new InnerTable();
		iTable.removeChild(3);
		TableHeaderLine header = new TableHeaderLine();
		TableHeaderCell headerCell = new TableHeaderCell();
		headerCell.addChildType(new CausalScoreSolutionDefinition());
		header.removeChild(2);
		header.addChildType(headerCell);
		iTable.addChildTypeAtPosition(header, 3);

		this.addChildType(iTable);
	}

	/**
	 * Handles the creation of XCLRelations from CausalDiagnosisScoreMarkup
	 * 
	 * @author Johannes Dienst
	 * @created 10.11.2011
	 */
	public class CausalDiagnosisSubtreeHandler extends GeneralSubtreeHandler<CausalDiagnosisScore> {

		private static final String NO_WEIGHT = "Weightless";

		@Override
		public Collection<Message> create(KnowWEArticle article, Section<CausalDiagnosisScore> scoreSec)
		{

			Section<InnerTable> innerTable =
					Sections.findChildOfType(scoreSec, InnerTable.class);
			if (Sections.findSuccessorsOfType(innerTable, TableCell.class).isEmpty()) return null;

			article = KnowWEUtils.getCompilingArticles(scoreSec).iterator().next();
			KnowledgeBase kb = D3webUtils.getKnowledgeBase(scoreSec.getWeb(), article.getTitle());

			// Create all Conditions and Weights: 1st and 2end column
			// TODO First Cell is no Question: Removed it! But what if empty?
			// TODO no checks or whatsoever. Write security check!
			List<Section<TableCellFirstColumn>> firstColumn = Sections.findSuccessorsOfType(innerTable, TableCellFirstColumn.class);
			LinkedList<Condition> conditionList = new LinkedList<Condition>();
			for (Section<TableCellFirstColumn> cell : firstColumn) {
				Section<CompositeCondition> cond = Sections.findChildOfType(cell, CompositeCondition.class);
				Condition d3Cond = KDOMConditionFactory.createCondition(article, cond);
				conditionList.add(d3Cond);
			}

			// Get all solutions from TableHeaderLine
			// TODO Check if header misses 1st Tablecell
			List<Section<TableHeaderCell>> headerCells = Sections.findSuccessorsOfType(innerTable, TableHeaderCell.class);

			// Collect cells for columns
			// TODO Check if header misses 1st Tablecell
			int cellCount = Sections.findSuccessorsOfType(innerTable, TableLine.class).size();

			// Do for every column: Create Scoring rules
			List<Section<TableCell>> column = null;
			for (int i = 1; i < cellCount; i++)
			{
				column = TableUtils.getColumnCells(i, Sections.findChildOfType(scoreSec, InnerTable.class));

				// Get Solution from headerCells and create it, if necessary in kb
				Section<TableHeaderCell> solutionCell = headerCells.get(i);
				String solText = solutionCell.getText();
				solText = solText.replaceAll("[\\r\\n\\{\\s]", "");
				Solution solution = kb.getManager().searchSolution(solText);
				if (solution == null)
				{
					solution = new Solution(kb.getRootSolution(), solText);
					kb.getManager().putTerminologyObject(solution);
				}

				for (Section<TableCell> cell : column)
				{
					// create one Scoring-Rule per Cell
					String weight = cell.getText().trim();
					if (!weight.equals(""))
					{
						ActionHeuristicPS action = new ActionHeuristicPS();
						Score score = Score.N1;
						try
						{
							score = de.d3web.core.io.utilities.Util.getScore(weight);
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
						}
						action.setScore(score);
						action.setSolution(solution);
						Rule rule = new Rule(PSMethodAbstraction.class);
						rule.setCondition(conditionList.get(i-1));
						rule.setAction(action);
					}
				}


			}
			return null;
		}

	}

}
