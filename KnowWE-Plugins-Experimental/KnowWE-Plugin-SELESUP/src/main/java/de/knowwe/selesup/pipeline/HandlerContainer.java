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

import java.util.Arrays;
import java.util.Iterator;

/**
 * Encapsulates multiple {@link Handler} instances for easier pipeline instantiation.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 12.11.14
 */
public class HandlerContainer implements Iterable<Handler<?, ?>> {

	private final Handler<?, ?>[] handlers;

	/**
	 * Creates a new HandlerContainer that encapsulates the specified {@link Handler} instances
	 * in the specified order.
	 * @param handlers the handlers that shall be encapsulated
	 */
	public HandlerContainer(Handler<?, ?>... handlers) {
		this.handlers = handlers;
	}

	/**
	 * Returns the encapsulated {@link Handler} instances.
	 * @return the encapsulated handlers
	 */
	public Handler<?, ?>[] getHandlers() {
		return handlers;
	}

	@Override
	public Iterator<Handler<?, ?>> iterator() {
		return Arrays.asList(handlers).iterator();
	}
}
