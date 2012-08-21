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

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.we.diaflux.datamanagement.OCDomain;

/**
 * 
 * @author Reinhard Hatko
 * @created 28.06.2012
 */
public class OCDomainTest {

	KnowledgeBase kb;
	QuestionOC q;

	@Before
	public void setUp() throws Exception {
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		q = new QuestionOC(kb, "QOC");

		// Answers a-e
		for (int i = 0; i < 5; i++) {
			q.addAlternative(new Choice(String.valueOf((char) ('a' + i))));

		}
	}

	@Test
	public void testConstructor1() throws Exception {
		assertThat(domain("a,b,c").getChoices(), is(parse("a,b,c")));
	}

	@Test
	public void testConstructor2() throws Exception {
		assertThat(new OCDomain(q).getChoices(), is(parse("a,b,c,d,e")));
	}

	/**
	 * Test method for {@link de.d3web.we.diaflux.datamanagement.OCDomain#add(de.d3web.we.diaflux.datamanagement.OCDomain)}.
	 */
	@Test
	public void testAdd() {

		assertThat(domain("a").add(domain("e")).getChoices(), is(parse("a,e")));
		assertThat(domain("a,b,c").add(domain("d")).getChoices(), is(parse("a,b,c,d")));

		assertThat(domain("a,b,c").add(domain("d")).getChoices(), is(parse("a,b,c,d")));

		assertThat(domain("a,b,c").add(domain("a")).getChoices(), is(parse("a,b,c")));
		assertThat(domain("a,b,c,d,e").add(domain("d")).getChoices(), is(parse("a,b,c,d,e")));

	}

	/**
	 * Test method for {@link de.d3web.we.diaflux.datamanagement.OCDomain#negate()}.
	 */
	@Test
	public void testNegate() {
		assertThat(domain("a").negate().getChoices(), is(parse("b,c,d,e")));
		assertThat(domain("a,e").negate().getChoices(), is(parse("b,c,d")));

		assertThat(domain("a,b,c,d,e").negate().getChoices(), is(parse("")));
		assertThat(domain("a,b,c,d,e").negate().negate(), is(domain("a,b,c,d,e")));
		assertThat(domain("a").negate().negate(), is(domain("a")));
	}

	/**
	 * Test method for {@link de.d3web.we.diaflux.datamanagement.OCDomain#contains(de.d3web.we.diaflux.datamanagement.OCDomain)}.
	 */
	@Test
	public void testContains() {
		assertThat(domain("a").contains(domain("a")), is(true));
		assertThat(domain("a,b").contains(domain("a")), is(true));
		assertThat(domain("b,a").contains(domain("a,b")), is(true));

		assertThat(domain("a").contains(domain("b")), is(false));
		assertThat(domain("a,b").contains(domain("b,c")), is(false));

		assertThat(domain("a,b,c,d,e").negate().contains(domain("a")), is(false));

	}

	/**
	 * Test method for {@link de.d3web.we.diaflux.datamanagement.OCDomain#intersects(de.d3web.we.diaflux.datamanagement.OCDomain)}.
	 */
	@Test
	public void testIntersects() {
		assertThat(domain("a").intersects(domain("a")), is(true));
		assertThat(domain("a,b").intersects(domain("a")), is(true));
		assertThat(domain("b,a").intersects(domain("a,b")), is(true));
		assertThat(domain("a,b").intersects(domain("b,c")), is(true));

		assertThat(domain("a").intersects(domain("b")), is(false));
		assertThat(domain("").intersects(domain("a")), is(false));

	}

	/**
	 * Test method for {@link de.d3web.we.diaflux.datamanagement.OCDomain#isEmpty()}.
	 */
	@Test
	public void testIsEmpty() {
		assertThat(domain("a").isEmpty(), is(false));
		assertThat(domain("a,b,c,d,e").isEmpty(), is(false));

		assertThat(domain("").isEmpty(), is(true));

	}

	/**
	 * Test method for {@link de.d3web.we.diaflux.datamanagement.OCDomain#intersect(de.d3web.we.diaflux.datamanagement.OCDomain)}.
	 */
	@Test
	public void testIntersect() {
		assertThat(domain("a").intersect(domain("a")), is(domain("a")));
		assertThat(domain("a,b").intersect(domain("a")), is(domain("a")));
		assertThat(domain("b,a").intersect(domain("a,b")), is(domain("a,b")));
		assertThat(domain("a,b").intersect(domain("b,c")), is(domain("b")));

		assertThat(domain("a").intersect(domain("b")), is(domain("")));
		assertThat(domain("").intersect(domain("a")), is(domain("")));
	}

	/**
	 * Test method for {@link de.d3web.we.diaflux.datamanagement.OCDomain#getChoices()}.
	 */
	@Test
	public void testGetChoices() {
		assertThat(domain("").getChoices(), is(parse("")));
		assertThat(domain("a").getChoices(), is(parse("a")));
		assertThat(domain("a,b,c,d,e").getChoices(), is(parse("a,b,c,d,e")));

	}

	// Tests for internal helper methods
	@Test
	public void testParse() throws Exception {
		assertThat(parse("a").size(), is(1));
		assertThat(parse("a,a").size(), is(1));
		assertThat(parse("a,b,c,d,e").size(), is(5));
	}

	@Test
	public void testDomain() throws Exception {
		assertThat(domain("a").getChoices(), is(parse("a")));
		assertThat(domain("a,a").getChoices(), is(parse("a")));
		assertThat(domain("").getChoices(), is(parse("")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseIllegal() throws Exception {
		parse("f");

	}

	Collection<Choice> parse(String s) {
		return parse(q, s);
	}

	public static Collection<Choice> parse(QuestionChoice q, String s) {
		Collection<Choice> result = new HashSet<Choice>();

		if (s.isEmpty()) return result;
		else {

			for (String answer : s.split(",")) {
				Choice choice = KnowledgeBaseUtils.findChoice(q, answer.trim());
				if (choice == null) throw new IllegalArgumentException();
				result.add(choice);
			}
			return result;
		}

	}

	OCDomain domain(String s) {
		return new OCDomain(q, parse(s));
	}

}
