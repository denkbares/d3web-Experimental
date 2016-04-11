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
package test.evaluators;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondKnown;
import de.d3web.core.inference.condition.ConditionTrue;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.evaluators.TrueEvaluator;


/**
 * 
 * @author Reinhard Hatko
 * @created 28.06.2012
 */
public class TrueEvalTest {


	private KnowledgeBase kb;

	@Before
	public void setup() {
		kb = new KnowledgeBase();
	}
	@Test
	public void testCanEvaluate() {
		assertThat(new TrueEvaluator().canEvaluate(ConditionTrue.INSTANCE), is(true));
		assertThat(
				new TrueEvaluator().canEvaluate(new CondKnown(new QuestionOC(kb, "q"))),
				is(false));

	}

	@Test
	public void testEvaluate() throws Exception {
		assertThat(new TrueEvaluator().evaluate(ConditionTrue.INSTANCE, kb), is(new EvalResult()));

	}

}
