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
 * Interface for a handler. A handler is a representation of a pipeline element, processing
 * the input (of type defined by the generic parameter &lt;I&gt;) in order to produce an output
 * (of the type defined by the generic parameter &lt;O&gt;).
 *
 * Please note that the usage of arrays instead of collections is recommended because of the generic
 * parameters.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 01.09.14
 */
public interface Handler<I, O> {

	/**
	 * Processes the input of type &lt;I&gt; to produce an output of type &lt;O&gt;
	 * @param input the data that shall be processed
	 * @return the output data
	 */
	O process(I input);

	/**
	 * Returns the {@link Class} of the input data.
	 * @return {@link Class} of the input
	 */
	Class<I> getInputClass();

	/**
	 * Returns the {@link Class} of the output data.
	 * @return {@link Class} of the output
	 */
	Class<O> getOutputClass();
}
