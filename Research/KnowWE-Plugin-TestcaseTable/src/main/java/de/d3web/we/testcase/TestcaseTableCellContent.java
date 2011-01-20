/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.SectionFinderConstraint;
import de.d3web.we.kdom.sectionFinder.ISectionFinder;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.kdom.table.Table;
import de.d3web.we.kdom.table.TableCellContent;
import de.d3web.we.kdom.table.TableLine;
import de.d3web.we.object.AnswerReference;
import de.d3web.we.object.QuestionReference;

/**
 * @author Florian Ziegler
 * @created 10.08.2010
 */
public class TestcaseTableCellContent extends TableCellContent {

	private final class UnchangedType extends DefaultAbstractKnowWEObjectType {

		private final String regex = "\\s*" + GetNewQuickEditAnswersAction.UNCHANGED_VALUE_STRING
				+ "\\s*";

		@Override
		protected void init() {
			setSectionFinder(new RegexSectionFinder(Pattern.compile(regex)));
		}

	}

	/**
	 * 
	 * @author Reinhard Hatko
	 * @created 18.01.2011
	 */
	private final class CellAnswerRef extends AnswerReference {

		@Override
		public Section<QuestionReference> getQuestionSection(Section<? extends AnswerReference> s) {

			Section<TableLine> line = s.findAncestorOfType(TableLine.class);
			boolean found = false;
			int i = 0;
			for (Section<?> section : line.getChildren()) {
				
				if (s.equalsOrIsSuccessorOf(section)) {
					found = true;
					break;
				}
				
				i++;
			}

			if (!found) {
				System.out.println("no Q found for: " + s);
				return null;
			}

			Section<Table> table = line.findAncestorOfType(Table.class);
			Section<TableLine> headerline = table.findSuccessor(TableLine.class);
			Section<? extends KnowWEObjectType> headerCell = headerline.getChildren().get(i);
			Section<QuestionReference> questionRef = headerCell.findSuccessor(QuestionReference.class);

			return questionRef;
		}
	}

	@Override
	protected void init() {
		setCustomRenderer(new TestcaseTableCellContentRenderer());
		childrenTypes.add(new UnchangedType());

		TimeStampType timeStampType = new TimeStampType();
		timeStampType.setSectionFinder(new ConstraintSectionFinder(timeStampType.getSectioner(),
				new SectionFinderConstraint() {

					@Override
					public boolean satisfiesConstraint(List<SectionFinderResult> found, Section father, KnowWEObjectType type) {
						Section line = father.findAncestorOfExactType(TestcaseTableLine.class);
						return line.getChildren().size() == 1;
					}

					@Override
					public void filterCorrectResults(List<SectionFinderResult> found, Section father, KnowWEObjectType type) {
						if (found == null || found.size() == 0) return;
						found.clear();

					}
				}));

		childrenTypes.add(timeStampType);
		QuestionReference qref = new QuestionReference();
		qref.setSectionFinder(new ISectionFinder() {

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, KnowWEObjectType type) {
				Section<TestcaseTable> table = father.findAncestorOfExactType(TestcaseTable.class);
				List<Section<TableLine>> lines = new LinkedList<Section<TableLine>>();
				table.findSuccessorsOfType(TableLine.class, lines);

				// header line
				if (lines.size() == 1) {
					// empty first cell (contained in a TableCell section)
					if (lines.get(0).getChildren().size() == 1) {
						return null;
					}

					if (text.length() > 0) {
						return SectionFinderResult.createSingleItemList(new SectionFinderResult(0,
								text.length()));
					}
					else {
						return null;
					}
				}
				else return null;

			}
		});

		childrenTypes.add(qref);
		CellAnswerRef aRef = new CellAnswerRef();
		childrenTypes.add(aRef);

		aRef.setSectionFinder(new ISectionFinder() {

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, KnowWEObjectType type) {
				Section<TestcaseTable> table = father.findAncestorOfExactType(TestcaseTable.class);
				List<Section<TableLine>> lines = new LinkedList<Section<TableLine>>();
				table.findSuccessorsOfType(TableLine.class, lines);

				if (lines.size() > 1) {
					if (text.length() > 0) {
						return SectionFinderResult.createSingleItemList(new SectionFinderResult(0,
								text.length()));
					}
					else return null;
				}
				else return null;

			}
		});

	}


}
