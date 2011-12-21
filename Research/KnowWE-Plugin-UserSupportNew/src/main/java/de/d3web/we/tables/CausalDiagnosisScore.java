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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.KDOMConditionFactory;
import de.d3web.we.kdom.xcl.list.ListSolutionType;
import de.d3web.we.utils.D3webUtils;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;
import de.d3web.xcl.XCLRelationType;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.kdom.AnonymousTypeInvisible;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.sectionFinder.StringSectionFinderUnquoted;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;

/**
 * 
 * TODO Add standard scores TODO Add ReportMessages
 * 
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class CausalDiagnosisScore extends AbstractType {

	public CausalDiagnosisScore() {
		this.sectionFinder = new AllTextSectionFinder();
		this.addSubtreeHandler(Priority.LOW, new CausalDiagnosisSubtreeHandler());

		this.addChildType(new ListSolutionType());

		// cut the optional closing }
		AnonymousTypeInvisible closing = new AnonymousTypeInvisible("closing-bracket");
		closing.setSectionFinder(new StringSectionFinderUnquoted("}"));
		this.addChildType(closing);

		this.addChildType(new InnerTable());
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

			// TODO Right KnowledgeBase?
			Section<CausalDiagnosisScoreMarkup> mark = Sections.findAncestorOfExactType(
					scoreSec, CausalDiagnosisScoreMarkup.class);
			String packageName = DefaultMarkupType.getAnnotation(mark, "package");
			KnowledgeBase kb = D3webUtils.getKB(scoreSec.getWeb(), packageName + " - master");

			// Create all Conditions and Weights: 1st and 2end column
			// TODO First Cell is no Question: Removed it! But what if empty?
			// TODO no checks or whatsoever. Write security check!
			List<Section<TableCellFirstColumn>> firstColumn = Sections.findSuccessorsOfType(innerTable, TableCellFirstColumn.class);
			firstColumn.remove(0);
			LinkedList<Condition> conditionList = new LinkedList<Condition>();
			for (Section<TableCellFirstColumn> cell : firstColumn) {
				Section<CompositeCondition> cond = Sections.findChildOfType(cell, CompositeCondition.class);
				Condition d3Cond = KDOMConditionFactory.createCondition(cell.getArticle(), cond);
				conditionList.add(d3Cond);
			}

			// Weights
			List<Section<TableCell>> secondColumn = TableUtils.getColumnCells(
					1, Sections.findChildOfType(scoreSec, InnerTable.class));
			secondColumn.remove(0);
			LinkedList<String> weightList = new LinkedList<String>();
			for (Section<TableCell> cell : secondColumn)
			{
				String weightText = cell.getText().trim();
				if (weightText.equals(""))
				{
					weightList.add(NO_WEIGHT);
					continue;
				}
				weightList.add(weightText);
			}

			// Collect cells for columns
			// TODO Check if header misses 1st Tablecell
			int cellCount = TableUtils.getMaximumTableCellCount(innerTable);

			// Do for every column/XCLRelation
			LinkedList<Section<TableCell>> column = null;
			for (int i = 2; i < cellCount; i++)
			{
				column = new LinkedList<Section<TableCell>>(
						TableUtils.getColumnCells(
								i, Sections.findChildOfType(scoreSec, InnerTable.class)));

				// Get Solution and create it, if necessary in kb
				Section<TableCell> solutionCell = column.removeFirst();
				String solText = solutionCell.getText();
				solText = solText.replaceAll("[\\r\\n\\{\\s]", "");
				Solution solution = kb.getManager().searchSolution(solText);
				if (solution == null)
				{
					solution = new Solution(kb.getRootSolution(), solText);
					kb.getManager().putTerminologyObject(solution);
				}

				// Create XCLModel
				XCLModel model = new XCLModel(solution);

				// One column can contain Cells with the following symbols
				// Standard: [+]
				// Necessary to derive solution: [!]
				// Excludes this solution: [--]
				// Suffices to derive solution: [++]
				Section<TableCell> cell = null;
				for (int j = 0; j < column.size(); j++)
				{
					cell = column.removeFirst();
					String cellText = cell.getText().trim();

					// No text -> no Relation
					if (cellText.equals("") ||
							conditionList.get(j) == null) continue;

					XCLRelation rel = XCLRelation.createXCLRelation(conditionList.get(j));

					// Normal
					if (cellText.equals("+"))
					{
						// TODO what if it is no double?
						rel.setWeight(Double.parseDouble(weightList.get(j)));
						model.addRelation(rel);
					}

					// Necessary
					else if (cellText.equals("!"))
					{
						model.addRelation(rel, XCLRelationType.requires);
					}

					// Excluded
					else if (cellText.equals("--"))
					{
						model.addRelation(rel, XCLRelationType.contradicted);
					}

					// Suffices
					else if (cellText.equals("++"))
					{
						model.addRelation(rel, XCLRelationType.sufficiently);
					}

				}

			}
			return null;
		}

	}

}
