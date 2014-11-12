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
package de.knowwe.selesup;

import java.util.Collection;

import de.knowwe.selesup.pipeline.Handler;
import de.knowwe.selesup.pipeline.Pipeline;
import de.knowwe.selesup.sofa.Sofa;
import de.knowwe.selesup.sofa.SofaFilter;

/**
 * The DataProvider interface is responsible for providing data for further processing.
 * Therefore a couple of methods are available for configuration:
 *
 * <ul>
 *     <li>{@link #getSofaFilter()}: Allows to specify a {@link de.knowwe.selesup.sofa.SofaFilter} that can be used to
 *     limit the resources that shall be processed using this DataProvider</li>
 *     <li>{@link #getAvailableHandlers()}: Returns a list of {@link de.knowwe.selesup.pipeline.Handler}s, that are supported
 *     by this DataProvider and can be combined to configure a pipeline</li>
 *     <li>{@link #setPipeline(de.knowwe.selesup.pipeline.Pipeline)}: Sets the {@link de.knowwe.selesup.pipeline.Pipeline}, consisting of multiple {@link de.knowwe.selesup.pipeline.Handler}
 *     objects, that shall be used to generate result candidates.</li>
 * </ul>
 *
 * After the DataProvider has been configured, a call of the {@link #getCandidates(java.util.Collection, Class)}
 * method starts the computation of result candidates using the configured pipeline.
 *
 * @param <T> type of the final result objects.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 29.08.14
 */
public interface DataProvider<T> {

	/**
	 * Returns result candidates, e.g. new instances for the ontology.
	 * The candidates are computed using the specified {@link de.knowwe.selesup.sofa.Sofa} objects, which represent
	 * documents (e.g. wiki articles) or other data (e.g. Excel files provided as wiki attachment).
	 * <p>
	 * This is a generic method, the class of the result objects must be provided using the second
	 * argument.
	 * <p>
	 * Please note that this method throws an {@link java.lang.IllegalStateException} if the method
	 * {@link #setPipeline(de.knowwe.selesup.pipeline.Pipeline)} has not been called before.
	 * <p>
	 *
	 * @param resources the resources that shall be analyzed
	 * @param c         the class of the result objects, used to determine the type
	 * @return a collection of result candidates
	 */
	Collection<T> getCandidates(Collection<Sofa> resources, Class<T> c);


	/**
	 * Sets the pipeline used for the computation of candidates
	 * ({@link #getCandidates(java.util.Collection, Class)}.
	 *
	 * Please note that if you want to return more than one result candidate, the usage of
	 * arrays is highly recommended, e.g. if you want to return a couple of triples, set the generic
	 * parameter &lt;T&gt; to Statement[].
	 *
	 * @param pipeline the pipeline that shall be used for the further processing
	 */
	void setPipeline(Pipeline<Sofa, ?, T> pipeline);

	/**
	 * Returns a collection of {@link de.knowwe.selesup.pipeline.Handler}s that are supported by this {@link DataProvider}
	 * implementation. The returned collection should be used to configure a pipeline, which
	 * afterwards can be set using the {@link #setPipeline(Pipeline)} method.
	 *
	 * @return
	 */
	Collection<Handler<?, ?>> getAvailableHandlers();

	/**
	 * Returns a {@link de.knowwe.selesup.sofa.SofaFilter} object that is used to filter the {@link Sofa} instances
	 * globally available to the needs of this {@link DataProvider} implementation, e.g. if the
	 * implementation can only handle Excel files, the returned filter should only accept
	 * sofas encapsulating Excel data.
	 *
	 * @return a sofa filter object.
	 */
	SofaFilter getSofaFilter();

}
