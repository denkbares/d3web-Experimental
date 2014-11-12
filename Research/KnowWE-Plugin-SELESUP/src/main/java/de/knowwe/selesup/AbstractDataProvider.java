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
import java.util.LinkedList;

import de.knowwe.selesup.pipeline.Pipeline;
import de.knowwe.selesup.sofa.Sofa;

/**
 * Abstract implementation of the {@link DataProvider} interface.
 * Providing standard handling for the methods {@link #getCandidates(java.util.Collection, Class)}
 * and {@link DataProvider#setPipeline(de.knowwe.selesup.pipeline.Pipeline)}.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 12.11.14
 */
public abstract class AbstractDataProvider<T> implements DataProvider<T> {

	protected Pipeline<Sofa, ?, T> pipeline;

	@Override
	public Collection<T> getCandidates(Collection<Sofa> resources, Class<T> c) {
		if (pipeline == null) {
			throw new IllegalStateException("The pipeline has not been set!");
		}
		Collection<T> result = new LinkedList<>();
		resources.forEach(resource -> { result.add(pipeline.run(resource)); } );
		return result;
	}

	@Override
	public void setPipeline(Pipeline<Sofa, ?, T> pipeline) {
		this.pipeline = pipeline;
	}

}
