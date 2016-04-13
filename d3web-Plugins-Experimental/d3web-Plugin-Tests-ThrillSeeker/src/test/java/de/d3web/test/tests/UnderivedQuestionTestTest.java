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

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.values.NumValue;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.test.UnderivedQuestionTest;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;


/**
 * 
 * @author Reinhard Hatko
 * @created 25.03.2013
 */
public class UnderivedQuestionTestTest {

	private KnowledgeBase kb;

	@Before
	public void init() throws IOException {
		InitPluginManager.init();
		kb = new KnowledgeBase();
	}

	@Test
	public void testSuccessSetToConstantInRule() throws InterruptedException {

		Question derived = new QuestionNum(kb, "derived");
		RuleFactory.createSetValueRule(derived, new NumValue(42),
				ConditionTrue.INSTANCE);

		doTest(Message.Type.SUCCESS);
	}


	@Test
	public void testfail() throws InterruptedException {

		new QuestionNum(kb, "underived");
		
		doTest(Message.Type.FAILURE);
	}

	private void doTest(Type result) throws InterruptedException {
		UnderivedQuestionTest test = new UnderivedQuestionTest();
		Message message = test.execute(kb, new String[0]);
		assertThat(message.getType(), is(result));
	}

}
