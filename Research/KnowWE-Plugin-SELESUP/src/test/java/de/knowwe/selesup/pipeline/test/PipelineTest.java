/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.selesup.pipeline.test;

import org.junit.Test;

import de.knowwe.selesup.pipeline.Pipeline;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Test for the {@link Pipeline} class, using the {@link TestHandler} implementation.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 12.11.14
 */
public class PipelineTest {



	@Test
	public void testHandlers() {
		String input1 = "1;2;3;4;5";
		int[] output1 = new int[] {1, 2, 3, 4, 5};
		assertArrayEquals(output1, TestHandler.STRING_TO_INTARRAY.process(input1));

		int[] input2 = output1;
		int output2 = 15;
		int actualOutput = TestHandler.INTARRAY_TO_SUM.process(input2);
		assertEquals(output2, actualOutput);
	}

	@Test
	public void testPipelineRun() {
		String input = "1;2;3;4;5";
		int output = 15;

		Pipeline<String, int[], Integer> pipeline = Pipeline.append(Pipeline.createHead(TestHandler.STRING_TO_INTARRAY), TestHandler.INTARRAY_TO_SUM);
		int actualOutput = pipeline.run(input);
		assertEquals(output, actualOutput);
	}



}
