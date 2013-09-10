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
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Value;
import de.d3web.testing.Message;

/**
 * 
 * @author Reinhard Hatko
 * @created 06.08.2012
 */
public class DeadPathTestYNTest extends TestBase {

	QuestionYN questionYN;
	Value yes;
	Value no;

	@Before
	public void setup() {
		questionYN = new QuestionYN(kb.getRootQASet(), "QuestionYN");
		yes = KnowledgeBaseUtils.findValue(questionYN, "Yes");
		no = KnowledgeBaseUtils.findValue(questionYN, "No");
	}


	@Test
	public void testFailure() throws IOException {

		createFlow(new CondEqual(questionYN, yes), new CondEqual(questionYN, no));

		runTest(Message.Type.FAILURE);
	}

	@Test
	public void testSuccess() throws IOException {

		createFlow(new CondEqual(questionYN, yes), new CondEqual(questionYN, yes));
		runTest(Message.Type.SUCCESS);

	}

}
