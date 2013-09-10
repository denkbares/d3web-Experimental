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

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.we.object.AnswerReference;
import de.d3web.we.object.QuestionReference;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.ExactlyOneFindingConstraint;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.table.TableCellContent;
import de.knowwe.kdom.table.TableLine;
import de.knowwe.kdom.table.TableRenderer;
import de.knowwe.kdom.table.TableUtils;

public class WimVentTable extends AbstractType {

	public static final StyleRenderer NUMBER_RENDERER = new StyleRenderer(
			"font-weight:bold");

	public WimVentTable() {
		this.addChildType(new WimVentRuleTableHeaderLine());
		this.addChildType(new WimVentRuleTableRuleLine());
		this.setSectionFinder(new AllTextSectionFinder());
		this.setRenderer(new TableRenderer());
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
			SectionFinder s = this.getSectionFinder();
			ConstraintSectionFinder csf = new ConstraintSectionFinder(s);
			csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
			this.setSectionFinder(csf);

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
			de.knowwe.core.kdom.basicType.Number number = new de.knowwe.core.kdom.basicType.Number();
			number.setRenderer(NUMBER_RENDERER);
			this.injectTableCellContentChildtype(number);
			this.injectTableCellContentChildtype(new TableAnswerRef());

			this.addSubtreeHandler(new TableLineRuleCompiler());
		}
	}

	static class TableAnswerRef extends AnswerReference {

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
		public Class<?> getTermObjectClass(Section<? extends Term> section) {
			return Choice.class;
		}

	}

}
