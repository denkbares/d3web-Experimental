/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.test.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.test.D3webTestUtils;
import de.d3web.testing.Utils;


/**
 * 
 * @author Reinhard Hatko
 * @created 25.03.2013
 */
public class UnusedObjectTestTest {

	/**
	 * Test method for {@link de.d3web.test.D3webTestUtils#isIgnoredInHierarchy(de.d3web.core.knowledge.TerminologyObject, java.util.Collection)}.
	 */
	@Test
	public void testIsIgnored() {
		KnowledgeBase kb = new KnowledgeBase();
		QContainer root = new QContainer(kb, "q000");
		kb.setRootQASet(root);

		QContainer in = new QContainer(root, "Input");
		QContainer mess = new QContainer(in, "Mess");
		QContainer user = new QContainer(mess, "User");
		new QContainer(mess, "Dev");
		Question q = new QuestionNum(user, "age");

		assertThat(D3webTestUtils.isIgnoredInHierarchy(q, getIgnores("age")), is(true));
		assertThat(D3webTestUtils.isIgnoredInHierarchy(q, getIgnores("AGE")), is(true));

		assertThat(D3webTestUtils.isIgnoredInHierarchy(q, getIgnores("INPUT")), is(true));
		assertThat(D3webTestUtils.isIgnoredInHierarchy(q, getIgnores("input")), is(true));

		assertThat(D3webTestUtils.isIgnoredInHierarchy(q, getIgnores("Mess")), is(true));
		assertThat(D3webTestUtils.isIgnoredInHierarchy(q, getIgnores("User")), is(true));

		assertThat(D3webTestUtils.isIgnoredInHierarchy(q, getIgnores()), is(false));
		assertThat(D3webTestUtils.isIgnoredInHierarchy(q, getIgnores("")), is(false));
		assertThat(D3webTestUtils.isIgnoredInHierarchy(q, getIgnores("Dev")), is(false));
		assertThat(D3webTestUtils.isIgnoredInHierarchy(q, getIgnores("agent")), is(false));
	}

	private static Collection<Pattern> getIgnores(String... ignores) {
		Collection<Pattern> result = new LinkedList<Pattern>();
		for (String ignore: ignores) {
			result.addAll(Utils.compileIgnores(new String[] {ignore}));
		}
		
		return result;
	}

}
