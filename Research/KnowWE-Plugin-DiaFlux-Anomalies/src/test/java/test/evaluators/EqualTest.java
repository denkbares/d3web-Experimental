/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package test.evaluators;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import test.domain.OCDomainTest;
import de.d3web.core.inference.condition.CondAnswered;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.we.diaflux.datamanagement.OCDomain;
import de.d3web.we.diaflux.evaluators.ChoiceEqualEval;
import de.d3web.we.diaflux.evaluators.Evaluator;

/**
 * 
 * @author Reinhard Hatko
 * @created 28.06.2012
 */
public class EqualTest {

	KnowledgeBase kb;
	QuestionOC oc;
	QuestionMC mc;
	Evaluator eval;

	@Before
	public void setup() {
		kb = KnowledgeBaseUtils.createKnowledgeBase();
		oc = new QuestionOC(kb, "OC");
		mc = new QuestionMC(kb, "MC");

		for (int i = 0; i < 5; i++) {
			oc.addAlternative(new Choice(String.valueOf((char) ('a' + i))));
			mc.addAlternative(new Choice(String.valueOf((char) ('a' + i))));
		}
		eval = new ChoiceEqualEval();
	}

	@Test
	public void testCanEvaluate() throws Exception {
		assertThat(eval.canEvaluate(new CondEqual(oc, new ChoiceValue("a"))), is(true));
		assertThat(eval.canEvaluate(new CondEqual(mc, new ChoiceValue("a"))), is(true));
		assertThat(eval.canEvaluate(new CondAnswered(mc)), is(false));
	}

	@Test
	public void testEvaluateOC() throws Exception {
		assertThat(evalOC(new CondEqual(oc, new ChoiceValue("a"))), is(domainOC("a")));
		assertThat(evalOC(new CondEqual(oc, new ChoiceValue("b"))), is(domainOC("b")));
	}

	private OCDomain evalOC(Condition cond) {
		return (OCDomain) eval.evaluate(cond, kb).getDomain(oc);
	}

	private OCDomain domainOC(String s) {
		return new OCDomain(oc, OCDomainTest.parse(oc, s));
	}

}
