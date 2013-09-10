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
import de.knowwe.core.kdom.basicType.LineBreak;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.sectionFinder.LineSectionFinder;
import de.knowwe.kdom.sectionFinder.SentenceSectionFinder;
import de.knowwe.kdom.sectionFinder.StringSectionFinder;
import de.knowwe.kdom.table.TableCell;
import de.knowwe.kdom.table.TableCellStart;
import de.knowwe.kdom.table.TableHeadStart;
import de.knowwe.kdom.table.TableLine;

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

	
	/**
	 * Single \r or \n are LineBreaks too for this finder
	 */
	public void testLineBreakFinder() {
		String text = " There goes the cow\r\n"
					+ " and there it goes not"
					+ "perhaps it dont want to go \r\n";
		SectionFinder f = new LineBreak().getSectionFinder();
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
		SectionFinder f = LineSectionFinder.getInstance();
		List<SectionFinderResult> results = f.lookForSections(text, null, null);

		assertEquals(WRONG_FIRST_START, 0, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 21, results.get(0).getEnd());

		assertEquals(WRONG_SECOND_START, 21, results.get(1).getStart());
		assertEquals(WRONG_SECOND_END, 72, results.get(1).getEnd());
	}

	
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

		SectionFinder f = new TableCell().getSectionFinder();
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
		SectionFinder f = new TableCellStart().getSectionFinder();

		List<SectionFinderResult> results = f.lookForSections(text, null,
				new AnonymousType("dummy"));
		assertEquals(WRONG_FIRST_START, 0, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 1, results.get(0).getEnd());
	}

	public void testTableHeadStartFinder() {
		String text = "|| Apple ";
		SectionFinder f = new TableHeadStart().getSectionFinder();
		List<SectionFinderResult> results = f.lookForSections(text, null, null);
		assertEquals(WRONG_FIRST_START, 0, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 2, results.get(0).getEnd());
	}

	public void testTableLineSectionFinder() {
		String text = "|                        | Apple \r \n"
					+ "| sweetness              |   +   ";
		SectionFinder f = new TableLine().getSectionFinder();

		List<SectionFinderResult> results = f.lookForSections(text, null, null);

		assertEquals(WRONG_FIRST_START, 0, results.get(0).getStart());
		assertEquals(WRONG_FIRST_END, 34, results.get(0).getEnd());
		// System.out.println(text.substring(results.get(1).getStart(),
		// results.get(1).getEnd()));
		// System.out.println(text.substring(34, results.get(1).getEnd()));
		assertEquals(WRONG_FIRST_START, 36, results.get(1).getStart());
		assertEquals(WRONG_FIRST_END, 69, results.get(1).getEnd());
	}

}
