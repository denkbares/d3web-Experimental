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

import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumGreaterEqual;
import de.d3web.core.inference.condition.CondNumLessEqual;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.evaluators.OrEvaluator;


/**
 * 
 * @author Reinhard Hatko
 * @created 06.08.2012
 */
public class OrNumEvaluatorTest extends NumEvaluatorsTestBase {

	@Before
	public void setup() {
		eval = new OrEvaluator();
	}

	@Test
	public void testcanEvaluate() throws Exception {
		assertThat(eval.canEvaluate(new CondOr(new LinkedList<Condition>())), is(true));
		assertThat(eval.canEvaluate(new CondNumGreaterEqual(q, 5.)), is(false));

	}


	@Test
	public void testNum() {
		CondOr or;
		{
			Condition c1 = new CondNumGreaterEqual(q, 5.);
			Condition c2 = new CondNumGreater(q, 7.);
			or = new CondOr(Arrays.asList(c1, c2));

			assertThat(eval(or), is(domain("[5, Infinity]")));
		}

		{
			Condition c1 = new CondNumLessEqual(q, 5.);
			Condition c2 = new CondNumGreater(q, 7.);
			or = new CondOr(Arrays.asList(c1, c2));

			assertThat(eval(or), is(domain("[-Infinity, 5]", "(7, Infinity]")));
		}

	}



}
