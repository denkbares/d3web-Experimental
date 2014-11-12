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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import de.knowwe.selesup.pipeline.AbstractHandler;
import de.knowwe.selesup.sofa.Sofa;

/**
 * @author Sebastian Furth (denkbares GmbH)
 * @created 12.11.14
 */
public class TestHandler<I, O> extends AbstractHandler<I, O> {

	static final TestHandler<Sofa, String> SOFA_TO_STRING = new TestHandler<>(Sofa.class, String.class, (Sofa sofa) -> {
		try {
			BufferedInputStream bis = new BufferedInputStream(sofa.getInputStream());
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int result = bis.read();
			while(result != -1) {
				byte b = (byte)result;
				buf.write(b);
				result = bis.read();
			}
			return buf.toString();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	});

	static final TestHandler<String, int[]> STRING_TO_INTARRAY =
			new TestHandler<>(String.class, int[].class, (String string) -> {
				String[] elements = string.split(";");
				int[] result = new int[elements.length];
				for (int i = 0; i < elements.length; i++) {
					result[i] = Integer.parseInt(elements[i]);
				}
				return result;
			});

	static final TestHandler<int[], Integer> INTARRAY_TO_SUM =
			new TestHandler<>(int[].class, Integer.class, (int[] ints) -> Arrays.stream(ints).sum());


	private final Function<I, O> mapper;

	protected TestHandler(Class<I> inputClass, Class<O> outputClass, Function<I, O> mapper) {
		super(inputClass, outputClass);
		this.mapper = mapper;
	}

	@Override
	public O process(I input) {
		return mapper.apply(input);
	}
}
