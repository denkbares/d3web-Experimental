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

import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.testing.Message;

/**
 * 
 * 
 * @author Reinhard Hatko
 * @created 12.11.2012
 */
public class DeadPathTestNumTest extends TestBase {

	QuestionNum question;

	@Before
	public void setup() {
		question = new QuestionNum(kb.getRootQASet(), "QuestionYN");
	}


	@Test
	public void testFailureEqual() throws IOException {

		createFlow(new CondNumEqual(question, 5.), new CondNumEqual(question, 6.));

		runTest(Message.Type.FAILURE);
	}

	@Test
	public void testSuccessEqual() throws IOException {

		createFlow(new CondNumEqual(question, 1.), new CondNumEqual(question, 1.));
		runTest(Message.Type.SUCCESS);

	}

	@Test
	public void testFailureGreaterLess() throws IOException {

		createFlow(new CondNumGreater(question, 6.), new CondNumLess(question, 5.));

		runTest(Message.Type.FAILURE);
	}

	@Test
	public void testSuccessGreaterLess() throws IOException {

		createFlow(new CondNumGreater(question, 6.), new CondNumLess(question, 7.));
		runTest(Message.Type.SUCCESS);

	}

}
