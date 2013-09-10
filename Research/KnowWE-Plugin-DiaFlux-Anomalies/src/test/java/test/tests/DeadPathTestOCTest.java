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
package test.tests;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Value;
import de.d3web.testing.Message;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 12.11.2012
 */
public class DeadPathTestOCTest extends TestBase {

	QuestionOC questionOC;
	Value val1;
	Value val2;

	@Before
	public void setup() {
		questionOC = new QuestionOC(kb.getRootQASet(), "QuestionOC", "val1", "val2");

		val1 = KnowledgeBaseUtils.findValue(questionOC, "val1");
		val2 = KnowledgeBaseUtils.findValue(questionOC, "val2");
	}


	@Test
	public void testFailureEqual() throws IOException {

		createFlow(new CondEqual(questionOC, val1), new CondEqual(questionOC, val2));

		runTest(Message.Type.FAILURE);
	}


	@Test
	public void testFailureNotEqual() throws IOException {

		createFlow(new CondNot(new CondEqual(questionOC, val1)), new CondEqual(questionOC, val1));

		runTest(Message.Type.FAILURE);
	}

	@Test
	public void testSuccessEqual() throws IOException {

		createFlow(new CondEqual(questionOC, val1), new CondEqual(questionOC, val1));

		runTest(Message.Type.SUCCESS);
	}

}
