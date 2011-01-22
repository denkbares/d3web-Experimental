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

package tests;

import java.util.List;

import junit.framework.TestCase;
import de.d3web.we.kdom.basic.LineBreak;
import de.d3web.we.kdom.sectionFinder.AllTextSectionFinder;
import de.d3web.we.kdom.sectionFinder.ISectionFinder;
import de.d3web.we.kdom.sectionFinder.LineSectionFinder;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.kdom.sectionFinder.SentenceSectionFinder;
import de.d3web.we.kdom.sectionFinder.StringSectionFinder;
import de.d3web.we.kdom.semanticAnnotation.SemanticAnnotationProperty;
import de.d3web.we.kdom.table.TableCell;
import de.d3web.we.kdom.table.TableCellStart;
import de.d3web.we.kdom.table.TableHeadStart;
import de.d3web.we.kdom.table.TableLine;

/**
 * Tests all KnowWESectionFinders
 * 
 * @author Johannes Dienst
 * 
 */
public class KnowWESectionFinderTest extends TestCase {

	private final static String WRONG_FIRST_START = "Wrong start at first finding";
	private final static String WRONG_FIRST_END = "Wrong end at first finding";
	private final static String WRONG_SECOND_START = "Wrong start at second finding";
	private final static String WRONG_SECOND_END = "Wrong end at second finding";
	private final static String WRONG_THIRD_START = "Wrong start at third finding";
	private final static String WRONG_THIRD_END = "Wrong end at third finding";
	private final static String WRONG_FOURTH_START = "Wrong start at fourth finding";
	private final static String WRONG_FOURTH_END = "Wrong end at fourth finding";

	public void testAllTextFinder() {
		String test = "asoflkoasklfolakso";
		List<SectionFinderResult> results = new AllTextSectionFinder().lookForSections(test, null,
				null);

		assertEquals(WRONG_FIRST_START, 0, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, test.length(), results.get(0).getEnd());
	}

	public void testAnnotationKnowledgeSliceObjectAnswerSectionFinder() {

	}

	public void testAnnotationKnowledgeSliceObjectComparatorSectionFinder() {

	}

	public void testAnnotationKnowledgeSliceObjectQuestiongetSectionFinder() {

	}

	public void testAnnotationKnowledgeSliceSubjectSectionFinder() {

	}

	public void testAnnotationPropertyFinder() {
		String test = "blablub {{the currently measured mileage"
					+ " <=> swrc:asks:: Real mileage  /100km}}bla blub";
		ISectionFinder f =
					new SemanticAnnotationProperty().getSectioner();
		List<SectionFinderResult> results = f.lookForSections(test, null, null);

		assertEquals(WRONG_FIRST_START, 45, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 56, results.get(0).getEnd());
	}

	public void testAnnotationSubjectSectionFinder() {

	}

	// public void testFindingFinder() {
	//
	// }

	// public void testFindingsFinder() {
	//
	// }

	/**
	 * Single \r or \n are LineBreaks too for this finder
	 */
	public void testLineBreakFinder() {
		String text = " There goes the cow\r\n"
					+ " and there it goes not"
					+ "perhaps it dont want to go \r\n";
		ISectionFinder f = new LineBreak().getSectioner();
		List<SectionFinderResult> results = f.lookForSections(text, null, null);

		assertEquals(WRONG_FIRST_START, 19, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 21, results.get(0).getEnd());

		assertEquals(WRONG_SECOND_START, 69, results.get(1).getStart());
		assertEquals(WRONG_SECOND_END, 72, results.get(1).getEnd());
	}

	public void testLineSectionFinder() {
		String text = " There goes the cow\r\n"
				+ " and there it goes not"
				+ "perhaps it dont want to go \r\n";
		ISectionFinder f = LineSectionFinder.getInstance();
		List<SectionFinderResult> results = f.lookForSections(text, null, null);

		assertEquals(WRONG_FIRST_START, 0, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 21, results.get(0).getEnd());

		assertEquals(WRONG_SECOND_START, 21, results.get(1).getStart());
		assertEquals(WRONG_SECOND_END, 72, results.get(1).getEnd());
	}

	// public void testQuestionFinder() {
	//
	// }

	// public void testRatedSolutionFinder() {
	//
	// }

	public void testRegexSectionFinder() {
		String text = "<ME> is a ,bad, cruel bastard !! that !! </ME>";
		RegexSectionFinder f = new RegexSectionFinder("[!!]+[ \\w ]+[!!]+", 0);
		List<SectionFinderResult> results = f.lookForSections(text, null, null);

		assertEquals(WRONG_FIRST_START, 30, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 40, results.get(0).getEnd());

		f = new RegexSectionFinder("[<ME>][\\w\\W]*[</ME>]", 0);
		results = f.lookForSections(text, null, null);

		assertEquals(WRONG_FIRST_START, 0, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 46, results.get(0).getEnd());
	}

	/**
	 * Only recognizes ! and ? as sentencedelimiter. No .!!!
	 */
	public void testSentenceSectionFinder() {
		String text = "Whoray its summer \r\n"
					+ "its summer in the air."
					+ "so lets smoke! a cigarette. or two\n\r"
					+ "and sing a mascarade.";
		SentenceSectionFinder f = new SentenceSectionFinder();
		List<SectionFinderResult> results = f.lookForSections(text, null, null);

		assertEquals(WRONG_FIRST_START, 0, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 57, results.get(0).getEnd());

		// assertEquals(WRONG_SECOND_START, 57, results.get(1).getStart());
		// assertEquals(WRONG_SECOND_END, 70, results.get(1).getEnd());
		//
		// assertEquals(WRONG_THIRD_START, 70, results.get(2).getStart());
		// assertEquals(WRONG_THIRD_END, 99, results.get(2).getEnd());
	}

	// public void testStateRatingFinder() {
	//
	// }

	// TODO: Should this only find the first occurrence
	public void testStringSectionFinder() {
		String text = " bla blublbu bla jetzt nicht bla";
		StringSectionFinder f = new StringSectionFinder("bla");
		List<SectionFinderResult> results = f.lookForSections(text, null, null);

		assertEquals(WRONG_FIRST_START, 1, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 4, results.get(0).getEnd());

		// assertEquals(WRONG_SECOND_START, 14, results.get(1).getStart());
		// assertEquals(WRONG_SECOND_END, 17, results.get(1).getEnd());
		//
		// assertEquals(WRONG_THIRD_START, 31, results.get(2).getStart());
		// assertEquals(WRONG_THIRD_END, 34, results.get(2).getEnd());
	}

	/**
	 * Last finding sould be 71/72 not 71/71
	 */
	public void testTableCellSectionFinder() {
		String text = "|                        | Apple "
					+ "| sweetness              |   +   ";

		ISectionFinder f = new TableCell().getSectioner();
		List<SectionFinderResult> results = f.lookForSections(text, null, null);

		assertEquals(WRONG_FIRST_START, 0, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 25, results.get(0).getEnd());

		assertEquals(WRONG_SECOND_START, 25, results.get(1).getStart());
		assertEquals(WRONG_SECOND_END, 33, results.get(1).getEnd());

		assertEquals(WRONG_THIRD_START, 33, results.get(2).getStart());
		assertEquals(WRONG_THIRD_END, 58, results.get(2).getEnd());

		assertEquals(WRONG_FOURTH_START, 58, results.get(3).getStart());
		assertEquals(WRONG_FOURTH_END, 66, results.get(3).getEnd());
	}

	public void testTableCellStartFinder() {
		String text = "| Apple ";
		ISectionFinder f = new TableCellStart().getSectioner();

		List<SectionFinderResult> results = f.lookForSections(text, null, null);
		assertEquals(WRONG_FIRST_START, 0, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 1, results.get(0).getEnd());
	}

	public void testTableHeadStartFinder() {
		String text = "|| Apple ";
		ISectionFinder f = new TableHeadStart().getSectioner();
		List<SectionFinderResult> results = f.lookForSections(text, null, null);
		assertEquals(WRONG_FIRST_START, 0, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 2, results.get(0).getEnd());
	}

	public void testTableLineSectionFinder() {
		String text = "|                        | Apple \r \n"
					+ "| sweetness              |   +   ";
		ISectionFinder f = new TableLine().getSectioner();

		List<SectionFinderResult> results = f.lookForSections(text, null, null);

		assertEquals(WRONG_FIRST_START, 0, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 34, results.get(0).getEnd());

		assertEquals(WRONG_FIRST_START, 34, results.get(1).getStart());
		assertEquals(WRONG_FIRST_END, 69, results.get(1).getEnd());
	}

	// TODO What does that
	public void testTagHandlerAttributeFinder() {

	}
}
