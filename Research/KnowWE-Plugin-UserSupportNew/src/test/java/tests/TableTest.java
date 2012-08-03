/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package tests;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import utils.TestArticleManager;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.kdom.rules.action.SetQuestionValue;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.usersupport.tables.CausalDiagnosisScore;
import de.knowwe.usersupport.tables.CausalDiagnosisScoreMarkup;
import de.knowwe.usersupport.tables.DecisionTable;
import de.knowwe.usersupport.tables.DecisionTableMarkup;
import de.knowwe.usersupport.tables.HeuristicDiagnosisTable;
import de.knowwe.usersupport.tables.HeuristicDiagnosisTableMarkup;
import de.knowwe.usersupport.tables.InnerTable;
import de.knowwe.usersupport.tables.TableCell;
import de.knowwe.usersupport.tables.TableCellFirstColumn;
import de.knowwe.usersupport.tables.TableDescriptionType;
import de.knowwe.usersupport.tables.TableHeaderCell;
import de.knowwe.usersupport.tables.TableHeaderLine;
import de.knowwe.usersupport.tables.TableLine;
import de.knowwe.usersupport.tables.TableNormalCell;
import de.knowwe.usersupport.tables.TableSolutionType;


/**
 * 
 * @author Johannes Dienst
 * @created 28.10.2011
 */
public class TableTest extends TestCase {

	private final String TESTSUITEARTICLE = "src/test/resources/TableMarkup.txt";
	private Section<Article> articleSec = null;

	private Section<CausalDiagnosisScoreMarkup> causalTable = null;
	private final int causalHeaderLineCount = 1;
	private final int causalHeaderCellCount = 4;
	private final int causalLineCount = 2;
	private final int causalCellCount = 12;
	private final int causalCellFirstColumnCount = 2;

	private Section<DecisionTableMarkup> decisionTable = null;
	private final int decisionHeaderLineCount = 1;
	private final int decisionHeaderCellCount = 5;
	private final int decisionLineCount = 6;
	private final int decisionCellCount = 35;
	private final int decisionCellFirstColumnCount = 2;
	private final String actionsKeyword = "Actions";
	private final int setQuestionValueCount = 3;

	private Section<HeuristicDiagnosisTableMarkup> heuristicTable = null;
	private final int heuristicHeaderLineCount = 1;
	private final int heuristicHeaderCellCount = 5;
	private final int heuristicLineCount = 9;
	private final int heuristicCellCount = 50;
	private final int heuristicCellFirstColumnCount = 8;

	@Override
	protected void setUp() throws IOException {
		InitPluginManager.init();
		Article article = TestArticleManager.getArticle(TESTSUITEARTICLE);
		articleSec = article.getRootSection();
		causalTable = Sections.findSuccessor(articleSec, CausalDiagnosisScoreMarkup.class);
		decisionTable = Sections.findSuccessor(articleSec, DecisionTableMarkup.class);
		heuristicTable = Sections.findSuccessor(articleSec, HeuristicDiagnosisTableMarkup.class);
	}

	@Test
	public void testCausalDiagnosisScoreMarkup() {
		Section<CausalDiagnosisScore> score =
				Sections.findSuccessor(causalTable, CausalDiagnosisScore.class);
		assertNotNull(score);

		Section<TableDescriptionType> solution = Sections.findSuccessor(score, TableDescriptionType.class);
		assertNotNull(solution);
		assertEquals("Kausaler Score {\r\n", solution.getText());

		Section<InnerTable> innerTable =
				Sections.findSuccessor(score, InnerTable.class);
		assertNotNull(innerTable);

		List<Section<TableHeaderLine>> headerlines = Sections.findSuccessorsOfType(innerTable, TableHeaderLine.class);
		assertEquals(causalHeaderLineCount, headerlines.size());

		List<Section<TableHeaderCell>> headercells = Sections.findSuccessorsOfType(innerTable, TableHeaderCell.class);
		assertEquals(causalHeaderCellCount, headercells.size());

		List<Section<TableLine>> lines = Sections.findSuccessorsOfType(innerTable, TableLine.class);
		assertEquals(causalLineCount, lines.size());

		List<Section<TableCell>> cells = Sections.findSuccessorsOfType(innerTable, TableCell.class);
		assertEquals(causalCellCount, cells.size());

		List<Section<TableCellFirstColumn>> firstColumncells = Sections.findSuccessorsOfType(innerTable, TableCellFirstColumn.class);
		assertEquals(causalCellFirstColumnCount, firstColumncells.size());
	}

	@Test
	public void testDecisionTableMarkup() {
		Section<DecisionTable> score =
				Sections.findSuccessor(decisionTable, DecisionTable.class);
		assertNotNull(score);

		Section<TableDescriptionType> solution = Sections.findSuccessor(score, TableDescriptionType.class);
		assertNotNull(solution);
		assertEquals("EntscheidungsTabelle {\r\n", solution.getText());

		Section<InnerTable> innerTable =
				Sections.findSuccessor(score, InnerTable.class);
		assertNotNull(innerTable);

		List<Section<TableHeaderLine>> headerlines = Sections.findSuccessorsOfType(innerTable, TableHeaderLine.class);
		assertEquals(decisionHeaderLineCount, headerlines.size());

		List<Section<TableHeaderCell>> headercells = Sections.findSuccessorsOfType(innerTable, TableHeaderCell.class);
		assertEquals(decisionHeaderCellCount, headercells.size());

		List<Section<TableLine>> lines = Sections.findSuccessorsOfType(innerTable, TableLine.class);
		assertEquals(decisionLineCount, lines.size());

		List<Section<TableCell>> cells = Sections.findSuccessorsOfType(innerTable, TableCell.class);
		assertEquals(decisionCellCount, cells.size());

		List<Section<TableCellFirstColumn>> firstColumncells = Sections.findSuccessorsOfType(innerTable, TableCellFirstColumn.class);
		assertEquals(decisionCellFirstColumnCount, firstColumncells.size());

		Section<TableNormalCell> actionsCell = Sections.findChildOfType(lines.get(2), TableNormalCell.class);
		assertEquals(actionsKeyword, actionsCell.getText());

		List<Section<SetQuestionValue>> questionValues = Sections.findSuccessorsOfType(innerTable, SetQuestionValue.class);
		assertEquals(setQuestionValueCount, questionValues.size());
	}

	@Test
	public void testHeuristicDiagnosisTableMarkup() {
		Section<HeuristicDiagnosisTable> score =
				Sections.findSuccessor(heuristicTable, HeuristicDiagnosisTable.class);
		assertNotNull(score);

		Section<TableSolutionType> solution = Sections.findSuccessor(score, TableSolutionType.class);
		assertNotNull(solution);
		assertEquals("HeuristischeDiagnoseTabelle {\r\n", solution.getText());

		Section<InnerTable> innerTable =
				Sections.findSuccessor(score, InnerTable.class);
		assertNotNull(innerTable);

		List<Section<TableHeaderLine>> headerlines = Sections.findSuccessorsOfType(innerTable, TableHeaderLine.class);
		assertEquals(heuristicHeaderLineCount, headerlines.size());

		List<Section<TableHeaderCell>> headercells = Sections.findSuccessorsOfType(innerTable, TableHeaderCell.class);
		assertEquals(heuristicHeaderCellCount, headercells.size());

		List<Section<TableLine>> lines = Sections.findSuccessorsOfType(innerTable, TableLine.class);
		assertEquals(heuristicLineCount, lines.size());

		List<Section<TableCell>> cells = Sections.findSuccessorsOfType(innerTable, TableCell.class);
		assertEquals(heuristicCellCount, cells.size());

		List<Section<TableCellFirstColumn>> firstColumncells = Sections.findSuccessorsOfType(innerTable, TableCellFirstColumn.class);
		assertEquals(heuristicCellFirstColumnCount, firstColumncells.size());
	}
}
