/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.we.testcase;

import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.table.Table;
import de.d3web.we.kdom.table.TableAttributesProvider;
import de.d3web.we.kdom.table.TableCell;
import de.d3web.we.kdom.table.TableCellContent;
import de.d3web.we.kdom.table.TableLine;
import de.d3web.we.kdom.table.TableUtils;
import de.d3web.we.testcase.kdom.TimeStampType;

/**
 * @author Florian Ziegler
 */
public class TestcaseTableAttributesProvider implements TableAttributesProvider {

	@Override
	public String[] getAttributeValues(Section<? extends TableCellContent> s) {

		if (s != null) {
			return TestcaseUtils.getKnowledge(s);
		}
		return null;
	}

	@Override
	public String getNoEditColumnAttribute(Section<Table> s) {
		return "-1";
	}

	@Override
	public String getNoEditRowAttribute(Section<Table> s) {
		return "-1";
	}

	@Override
	public String getWidthAttribute(Section<Table> s) {
		return null;
	}

	@Override
	public String getCellForAppendRowQuickEdit(Section<TableCell> cell) {
		Section<TableCellContent> content = Sections.findChildOfType(cell, TableCellContent.class);
		int col = TableUtils.getColumn(content);
		int row = TableUtils.getRow(content);

		KnowledgeBase knowledgeService = D3webModule.getAD3webKnowledgeServiceInTopic(
				cell.getWeb(), cell.getTitle());

		// if autocompile is off
		if (knowledgeService == null) {
			return null;
		}
		List<Question> questions = knowledgeService.getManager().getQuestions();
		String header = TableUtils.getColumnHeadingForCellContent(content).trim();

		if (col == 0) {
			return " ";
		}
		else if (col == 1) {
			if (row > 1) {
				Section<Table> table = Sections.findAncestorOfType(cell, Table.class);
				List<Section<TableLine>> lines = Sections.findChildrenOfType(table, TableLine.class);
				Section<TableLine> line = lines.get(row);
				Section<? extends Type> cells = line.getChildren().get(col);
				Section<TimeStampType> asdf = Sections.findSuccessor(cells,
						TimeStampType.class);
				return TimeStampType.createTimeAsTimeStamp(TimeStampType.getTimeInMillis(asdf) + 1);

			}
			return "1s";
		}
		else if (row == 0) {
			return questions.get(0).getName();
		}
		else {
			for (Question q : questions) {
				if (q.getName().equals(header)) {
					if (q instanceof QuestionYN) {
						return "Yes";
					}
					else if (q instanceof QuestionChoice) {
						return ((QuestionChoice) q).getAllAlternatives().get(0).getName();

					}
					else if (q instanceof QuestionNum) {
						return "0";
					}
				}
			}


		}
		return "established";
	}


	@Override
	public String getCellForAppendColQuickEdit(Section<TableLine> line) {
		Section<? extends Type> lastChild =
				line.getChildren().get(line.getChildren().size() - 1);
		Section<TableCellContent> content = Sections.findSuccessor(lastChild,
				TableCellContent.class);
		return content.getText();
	}

}
