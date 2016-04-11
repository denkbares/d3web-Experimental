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

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumGreaterEqual;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.inference.condition.CondNumLessEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.we.diaflux.evaluators.AndEvaluator;


/**
 * 
 * @author Reinhard Hatko
 * @created 06.08.2012
 */
public class AndNumEvaluatorTest extends NumEvaluatorsTestBase {

	@Before
	public void setup() {
		eval = new AndEvaluator();
	}

	@Test
	public void testcanEvaluate() throws Exception {
		assertThat(eval.canEvaluate(new CondAnd(new LinkedList<Condition>())), is(true));
		assertThat(eval.canEvaluate(new CondNumGreaterEqual(q, 5.)), is(false));

	}


	@Test
	public void testNum() {
		assertThat(eval(parseCondition(q, "[5, 7]")), is(domain("[5,7]")));
		assertThat(eval(parseCondition(q, "(5, 7)")), is(domain("(5,7)")));
		assertThat(eval(parseCondition(q, "(-5, 7)")), is(domain("(-5,7)")));
	}

	private static Condition parseCondition(QuestionNum q, String s) {
		double left;
		double right;
		Condition c1;
		Condition c2;
		if (s.contains(",")) {
			String[] split = s.split(",");
			String leftSide = split[0].trim();
			String rightSide = split[1].trim();
			left = Double.parseDouble(leftSide.substring(1));
			right = Double.parseDouble(rightSide.substring(0, rightSide.length() - 1));

			if (leftSide.startsWith("(")) c1 = new CondNumGreater(q, left);
			else if (leftSide.startsWith("[")) c1 = new CondNumGreaterEqual(q, left);
			else throw new IllegalArgumentException(s);

			if (rightSide.endsWith(")")) c2 = new CondNumLess(q, right);
			else if (rightSide.endsWith("]")) c2 = new CondNumLessEqual(q, right);
			else throw new IllegalArgumentException(s);
		}
		else {
			throw new IllegalArgumentException();
		}

		return new CondAnd(Arrays.asList(c1, c2));

	}


}
