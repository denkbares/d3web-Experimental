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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import de.knowwe.selesup.DataProvider;
import de.knowwe.selesup.pipeline.HandlerContainer;
import de.knowwe.selesup.pipeline.Pipeline;
import de.knowwe.selesup.sofa.Sofa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test/Reference implementation for the {@link DataProvider} interface, using the
 * {@link TestDataProvider} implementation.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 12.11.14
 */
public class DataProviderTest {

	@Test
	public void testGetCandidates() {

		Pipeline<Sofa, ?, Integer> pipeline = Pipeline.createPipeline(TestHandler.SOFA_TO_STRING,
				new HandlerContainer(TestHandler.STRING_TO_INTARRAY), TestHandler.INTARRAY_TO_SUM);
		DataProvider<Integer> provider = new TestDataProvider();
		provider.setPipeline(pipeline);

		Collection<Sofa> sofas = Arrays.asList(new TestSofa("1;2;3"), new TestSofa("2;3;5"));
		Collection<Integer> candidates = provider.getCandidates(sofas, Integer.class);

		int expected1 = 6;
		int expected2 = 10;
		assertEquals(2, candidates.size());
		assertTrue(candidates.contains(expected1) && candidates.contains(expected2));
	}


}
