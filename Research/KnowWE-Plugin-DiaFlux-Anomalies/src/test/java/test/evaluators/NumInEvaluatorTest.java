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

import de.d3web.core.inference.condition.CondNumGreaterEqual;
import de.d3web.core.inference.condition.CondNumIn;
import de.d3web.we.diaflux.evaluators.NumInEval;

/**
 * 
 * @author Reinhard Hatko
 * @created 28.06.2012
 */
public class NumInEvaluatorTest extends NumEvaluatorsTestBase {

	@Before
	public void setup() {
		eval = new NumInEval();
	}

	@Test
	public void testcanEvaluate() throws Exception {
		assertThat(eval.canEvaluate(new CondNumIn(q, 5., 6.)), is(true));
		assertThat(eval.canEvaluate(new CondNumGreaterEqual(q, 5.)), is(false));

	}

	@Test
	public void testevaluate() throws Exception {

		assertThat(eval(new CondNumIn(q, 5., 7.5)), is(domain("[5, 7.5]")));
		assertThat(eval(new CondNumIn(q, 0., .6)), is(domain("[0, .6]")));
		assertThat(eval(new CondNumIn(q, -5., -1.)), is(domain("[-5, -1]")));

	}


}
