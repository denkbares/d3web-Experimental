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
package de.knowwe.selesup.pipeline;

/**
 * Abstract implementation of the {@link Handler} interface, providing access to encapsulated
 * input and output classes, that specify the generic arguments.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 01.09.14
 */
public abstract class AbstractHandler<I, O> implements Handler<I, O> {

	private final Class<I> inputClass;
	private final Class<O> outputClass;

	protected AbstractHandler(Class<I> inputClass, Class<O> outputClass) {
		this.inputClass = inputClass;
		this.outputClass = outputClass;
	}

	@Override
	public Class<I> getInputClass() {
		return inputClass;
	}

	@Override
	public Class<O> getOutputClass() {
		return outputClass;
	}
}
