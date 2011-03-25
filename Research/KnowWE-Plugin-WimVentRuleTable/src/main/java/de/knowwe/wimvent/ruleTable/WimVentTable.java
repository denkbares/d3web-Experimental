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
package de.knowwe.wimvent.ruleTable;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.ExactlyOneFindingConstraint;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.AllTextSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.subtreeHandler.IncrementalConstraint;
import de.d3web.we.kdom.table.TableCellContent;
import de.d3web.we.kdom.table.TableLine;
import de.d3web.we.kdom.table.TableRenderer;
import de.d3web.we.kdom.table.TableUtils;
import de.d3web.we.object.AnswerReference;
import de.d3web.we.object.QuestionReference;

public class WimVentTable extends AbstractType implements IncrementalConstraint<WimVentTable> {

	public static final StyleRenderer NUMBER_RENDERER = new StyleRenderer(
			"font-weight:bold");

	public WimVentTable() {
		this.addChildType(new WimVentRuleTableHeaderLine());
		this.addChildType(new WimVentRuleTableRuleLine());
		this.sectionFinder = new AllTextSectionFinder();
		this.setCustomRenderer(new TableRenderer());
	}

	public static Section<QuestionReference> findQRecInColumn(Section<? extends Type> s, int column) {
		Section<TableLine> tableLine = Sections.findAncestorOfType(s, TableLine.class);
		Section<?> tableSection = tableLine.getFather();
		Section<TableLine> headerLine = Sections.findSuccessor(tableSection,
				TableLine.class);

		List<Section<TableCellContent>> cells = new ArrayList<Section<TableCellContent>>();
		Sections.findSuccessorsOfType(headerLine, TableCellContent.class, cells);
		Section<TableCellContent> questionCell = cells.get(column);
		Section<QuestionReference> qRef = Sections.findSuccessor(questionCell,
				QuestionReference.class);
		return qRef;
	}

	class WimVentRuleTableHeaderLine extends TableLine {

		public WimVentRuleTableHeaderLine() {
			// there has to be exactly one
			SectionFinder s = this.sectionFinder;
			ConstraintSectionFinder csf = new ConstraintSectionFinder(s);
			csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
			this.sectionFinder = csf;
			
			QuestionReference questionReference = new QuestionReference();
			questionReference.setSectionFinder(new AllTextFinderTrimmed());
			this.injectTableCellContentChildtype(questionReference);
		}


	}

	class WimVentRuleTableRuleLine extends TableLine {
		public WimVentRuleTableRuleLine() {
			this.injectTableCellContentChildtype(new GrEqTableCondNum());
			this.injectTableCellContentChildtype(new LeEqTableCondNum());
			this.injectTableCellContentChildtype(new GrTableCondNum());
			this.injectTableCellContentChildtype(new LeTableCondNum());
			this.injectTableCellContentChildtype(new EqTableCondNum());
			// this.injectTableCellContentChildtype(new
			// IntervallTableCondNum());
			de.d3web.we.kdom.basic.Number number = new de.d3web.we.kdom.basic.Number();
			number.setCustomRenderer(NUMBER_RENDERER);
			this.injectTableCellContentChildtype(number);
			this.injectTableCellContentChildtype(new TableAnswerRef());

			this.addSubtreeHandler(new TableLineRuleCompiler());
		}
	}

	static class TableAnswerRef extends AnswerReference implements IncrementalConstraint<TableAnswerRef> {

		public TableAnswerRef() {
			this.setSectionFinder(new AllTextFinderTrimmed());
		}

		@Override
		public Section<QuestionReference> getQuestionSection(Section<? extends AnswerReference> s) {
			// find column number of current cell
			Section<TableCellContent> cell = Sections.findAncestorOfType(s,
					TableCellContent.class);
			int column = TableUtils.getColumn(cell);

			Section<QuestionReference> qRef = findQRecInColumn(s, column);
			return qRef;
		}

		@Override
		public boolean violatedConstraints(KnowWEArticle article, Section<TableAnswerRef> s) {
			Section<DefaultMarkupType> type = Sections.findAncestorOfType(s,
					DefaultMarkupType.class);
			return type.isOrHasSuccessorNotReusedBy(article.getTitle());
		}

	}

	@Override
	public boolean violatedConstraints(KnowWEArticle article, Section<WimVentTable> s) {
		return s.isOrHasSuccessorNotReusedBy(article.getTitle());
	}

}
