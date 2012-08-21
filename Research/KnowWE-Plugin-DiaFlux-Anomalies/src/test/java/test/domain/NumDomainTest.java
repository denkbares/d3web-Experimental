/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package test.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.we.diaflux.datamanagement.NumDomain;

/**
 * 
 * @author Reinhard Hatko
 * @created 21.06.2012
 */
public class NumDomainTest {

	QuestionNum q;
	KnowledgeBase kb;

	@Before
	public void setup() {
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		q = new QuestionNum(kb, "q");
	}

	@Test
	public void testConstructorValue() throws Exception {
		NumDomain domain = domain("1");

		Assert.assertEquals(Arrays.asList(parse("[1, 1]")), domain.getIntervals());
	}

	@Test
	public void testConstructorInterval() throws Exception {
		NumDomain domain = domain("[1, 2]");

		Assert.assertEquals(Arrays.asList(parse("[1, 2]")), domain.getIntervals());
	}

	@Test
	public void testAddValues() {
		assertThat(domain("5").add(domain("7")).getIntervals(), is(Arrays.asList(parse("[5, 5]"), parse("[7, 7]"))));
	}

	@Test
	public void testAddSameValue() {
		assertThat(domain("5").add(domain("5")).getIntervals(), is(Arrays.asList(parse("[5, 5]"))));
	}

	@Test
	public void testAddIntersectingIntervals() {
		NumDomain domain = domain("(3, 7]").add(domain("[5, 9]"));

		assertThat(domain.getIntervals(), is(Arrays.asList(parse("(3, 9]"))));
	}

	@Test
	public void testAddContainedValueToInterval() {
		NumDomain domain = domain("[3, 7]").add(domain("5"));

		assertThat(domain.getIntervals(), is(Arrays.asList(parse("[3, 7]"))));
	}

	@Test
	public void testAddNonIntersectingIntervals() {
		NumDomain domain = domain("[3, 7)").add(domain("[7, 9)"));
		assertThat(domain.getIntervals(), is(Arrays.asList(parse("[3, 7)"), parse("[7, 9)"))));
	}

	@Test
	public void testHashcode() {
		assertThat(domain("[3, 7]").hashCode(), is(domain("[3, 7]").hashCode()));
		assertThat(domain("[3, 7]").negate().hashCode(), is(domain("[3, 7]").negate().hashCode()));

	}

	@Test
	public void testEquals() {
		assertThat(domain("[3, 7]").equals(domain("[3, 7]")), is(true));
		assertThat(domain("[1, 2]", "[3, 7]").equals(domain("[3, 7]", "[1, 2]")), is(true));
		assertThat(domain("[3, 7]").negate().equals(domain("[3, 7]").negate()), is(true));

		assertThat(domain("[3, 7]").equals(domain("[3, 7)")), is(false));
		assertThat(domain("(3, 7)").negate().equals(domain("[3, 7]").negate()), is(false));

	}

	@Test
	public void testContains() {
		// Borders
		assertThat(domain("[3, 7]").contains(domain("[3, 7]")), is(true));
		assertThat(domain("[3, 7]").contains(domain("[3, 7)")), is(true));
		assertThat(domain("[3, 7]").contains(domain("(3, 7]")), is(true));
		assertThat(domain("[3, 7]").contains(domain("(3, 7)")), is(true));

		assertThat(domain("(3, 7]").contains(domain("[3, 7]")), is(false));
		assertThat(domain("[3, 7)").contains(domain("[3, 7]")), is(false));
		assertThat(domain("(3, 7)").contains(domain("[3, 7]")), is(false));

		// Not containing
		assertThat(domain("[3, 7]").contains(domain("[2, 5]")), is(false));
		assertThat(domain("[3, 7]").contains(domain("[7, 8]")), is(false));
		assertThat(domain("[3, 7]").contains(domain("[2, 8]")), is(false));
		assertThat(domain("[3, 7)", "(7,8]").contains(domain("[5, 8]")), is(false));
		assertThat(domain("[3, 7]").contains(domain("[2, 5)", "(5, 7]")), is(false));

		// Containing
		assertThat(domain("[3, 7]").contains(domain("[4, 5]")), is(true));
		assertThat(domain("[3, 7]").contains(domain("[3, 5)", "(5, 7]")), is(true));

	}

	@Test
	public void testIntersectValues() {
		assertThat(domain("3").intersect(domain("3")), is(domain("3")));

		assertThat(domain("3").intersect(domain("3")).isEmpty(), is(false));
		assertThat(domain("3").intersect(domain("4")).isEmpty(), is(true));
		
	}
	
	@Test
	public void testIntersectSameIntervals() {
		assertThat(domain("[3, 7]").intersect(domain("[3, 7]")), is(domain("[3, 7]")));
		assertThat(domain("[3, 7]").intersect(domain("(3, 7]")), is(domain("(3, 7]")));
		assertThat(domain("[3, 7]").intersect(domain("[3, 7)")), is(domain("[3, 7)")));
		assertThat(domain("[3, 7]").intersect(domain("(3, 7)")), is(domain("(3, 7)")));

		assertThat(domain("(3, 7]").intersect(domain("[3, 7]")), is(domain("(3, 7]")));
		assertThat(domain("[3, 7)").intersect(domain("[3, 7]")), is(domain("[3, 7)")));
		assertThat(domain("(3, 7)").intersect(domain("[3, 7]")), is(domain("(3, 7)")));
		assertThat(domain("(3, 7)").intersect(domain("(3, 7)")), is(domain("(3, 7)")));
		
	}

	@Test
	public void testIntersectIntersectingIntervals() {
		assertThat(domain("[3, 7]").intersect(domain("[2, 5]")), is(domain("[3, 5]")));
		assertThat(domain("[3, 7]").intersect(domain("(2, 5]")), is(domain("[3, 5]")));
		assertThat(domain("[3, 7]").intersect(domain("[2, 5)")), is(domain("[3, 5)")));
		assertThat(domain("[3, 7]").intersect(domain("(2, 5)")), is(domain("[3, 5)")));

		assertThat(domain("(3, 7]").intersect(domain("[6, 8]")), is(domain("[6, 7]")));
		assertThat(domain("[3, 7)").intersect(domain("[6, 8]")), is(domain("[6, 7)")));
		assertThat(domain("(3, 7)").intersect(domain("[6, 8]")), is(domain("[6, 7)")));
		assertThat(domain("(3, 7)").intersect(domain("(6, 8)")), is(domain("(6, 7)")));

	}
	
	@Test
	public void testIntersectValueAndInterval() {
		assertThat(domain("3").intersect(domain("[3, 7]")), is(domain("3")));
		assertThat(domain("7").intersect(domain("[3, 7]")), is(domain("7")));

		assertThat(domain("2").intersect(domain("[3, 7]")).isEmpty(), is(true));
		assertThat(domain("8").intersect(domain("[3, 7]")).isEmpty(), is(true));
	}
	
	@Test
	public void testIsEmpty() {
		assertThat(domain("8").isEmpty(), is(false));
		assertThat(domain("[8, 9]").isEmpty(), is(false));
		assertThat(domain("[-8, 9]").isEmpty(), is(false));
		assertThat(domain("[-Infinity, Infinity]").isEmpty(), is(false));
		assertThat(domain("8").add(domain("9")).isEmpty(), is(false));

		assertThat(domain("(Infinity, Infinity]").isEmpty(), is(true));
		assertThat(domain("8").intersect(domain("9")).isEmpty(), is(true));

	}

	@Test
	public void testNegateOneValue() {
		testNegation(domain("5"));
	}

	@Test
	public void testNegateMultipleValues() {
		NumDomain domain = domain("5").add(domain("7")).add(domain("9"));
		testNegation(domain);
	}

	@Test
	public void testNegateOneInterval() {
		testNegation(domain("[1, 2]"));
		testNegation(domain("[-1, 2]"));
		testNegation(domain("[-Infinity, 2]"));
		testNegation(domain("[-Infinity, 2)"));
		testNegation(domain("[1, Infinity]"));
		testNegation(domain("(1, Infinity]"));
		testNegation(domain("(-Infinity, Infinity]"));
	}

	private static void testNegation(NumDomain domain) {
		// System.out.println(domain);
		// System.out.println(domain.negate());
		// System.out.println(domain.negate().negate());
		assertThat(domain.negate().negate(), is(domain));
	}

	private NumDomain domain(String... strings) {
		return domain(q, strings);
	}

	public static NumDomain domain(QuestionNum q, String... strings) {
		List<NumericalInterval> intervals = new LinkedList<NumericalInterval>();
		for (String string : strings) {
			intervals.add(parse(string));
		}

		return new NumDomain(q, intervals);

	}

	public static NumericalInterval parse(String s) {
		double left;
		double right;
		boolean leftOpen;
		boolean rightOpen;
		if (s.contains(",")) {
			String[] split = s.split(",");
			String leftSide = split[0].trim();
			String rightSide = split[1].trim();
			left = Double.parseDouble(leftSide.substring(1));
			right = Double.parseDouble(rightSide.substring(0, rightSide.length() - 1));

			if (leftSide.startsWith("(")) leftOpen = true;
			else if (leftSide.startsWith("[")) leftOpen = false;
			else throw new IllegalArgumentException(s);
	
			if (rightSide.endsWith(")")) rightOpen = true;
			else if (rightSide.endsWith("]")) rightOpen = false;
			else throw new IllegalArgumentException(s);
		} else {
			double value = Double.parseDouble(s);
			left = value;
			right = value;
			leftOpen = false;
			rightOpen = false;
		}

		return new NumericalInterval(left, right, leftOpen, rightOpen);

	}

}
