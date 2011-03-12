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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Value;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.kdom.InvalidKDOMSchemaModificationOperation;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.SyntaxError;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;
import de.d3web.we.kdom.table.TableCell;
import de.d3web.we.kdom.table.TableLine;
import de.d3web.we.object.QuestionReference;
import de.d3web.we.utils.KnowWEUtils;

/**
 * 
 * @author Sebastian Furth
 * @created 20/10/2010
 */
public class TestcaseTableLine extends TableLine {

	public static final String TESTCASE_KEY = "TESTCASE";

	public TestcaseTableLine() {
		setCustomRenderer(new TestcaseTableLineRenderer());
		try {
			replaceChildType(new Cell(), TableCell.class);
		}
		catch (InvalidKDOMSchemaModificationOperation e) {
			e.printStackTrace();
		}

		addSubtreeHandler(new SubtreeHandler<TestcaseTableLine>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<TestcaseTableLine> s) {

				KnowledgeBase kb = findKB(s, article);

				Section<TimeStampType> timeStamp = Sections.findSuccessor(s, TimeStampType.class);
				if (timeStamp == null) {
					int lineNumber = Sections.findAncestorOfType(s, TestcaseTable.class).getChildren().indexOf(
							s);
					Section<CellContent> cell = Sections.findSuccessor(s, CellContent.class);

					LinkedList<KDOMReportMessage> list = new LinkedList<KDOMReportMessage>();
					list.add(new SyntaxError("Invalid timestamp '" + cell.getOriginalText()
							+ "' in line: " + lineNumber));
					return list;
				}
				long time = TimeStampType.getTimeInMillis(timeStamp);
				RatedTestCase testCase = new RatedTestCase();
				testCase.setTimeStamp(new Date(time));

				List<Section<ValueType>> values = new LinkedList<Section<ValueType>>();
				Sections.findSuccessorsOfType(s, ValueType.class, values);

				for (Section<ValueType> valueSec : values) {

					Section<? extends HeaderCell> headerCell = TestcaseTable.findHeaderCell(valueSec);

					Section<QuestionReference> qRef = Sections.findSuccessor(headerCell,
							QuestionReference.class);
					String qName = qRef.getOriginalText();
					// TODO unchanged value, unknown value
					Question question = kb.getManager().searchQuestion(qName);
					Value value = KnowledgeBaseUtils.findValue(question, valueSec.getOriginalText());
					Finding finding = new Finding(question, value);
					testCase.add(finding);
				}
				KnowWEUtils.storeObject(article, s, TESTCASE_KEY, testCase);

				return null;
			}

			private KnowledgeBase findKB(Section<TestcaseTableLine> s, KnowWEArticle article) {

				String master = TestcaseTableType.getMaster(
						Sections.findAncestorOfExactType(s, TestcaseTableType.class),
						article.getTitle());

				return D3webModule.getKnowledgeRepresentationHandler(article.getWeb()).getKB(
						master);

			}
		});
	}
}
