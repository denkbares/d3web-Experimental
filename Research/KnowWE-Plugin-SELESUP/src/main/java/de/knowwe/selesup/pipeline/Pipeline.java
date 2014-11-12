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
 * A generic pipeline that encapsulates a couple of consecutive {@link Handler} objects.
 * The encapsulated handlers are executed sequentially, producing the final result.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 01.09.14
 */
public class Pipeline<I, T, O> {

	private final Pipeline<I, ?, T> head;
	private final Handler<T, O> tail;

	public static <I, T, O> Pipeline<I, ?, O> createPipeline(Handler<I, ?> head, HandlerContainer handlers, Handler<T, O> tail) {
		Pipeline<I, ?, ?> temp = createHead(head);
		for (Handler handler : handlers) {
			temp = append(temp, handler);
		}
		Pipeline<I, ?, T> result = (Pipeline<I, ?, T>) temp;
		return append(result, tail);
	}

	/**
	 * Util method to create the head of pipeline. The specified {@link Handler} will be defined
	 * as head of the pipeline.
	 *
	 * @param head the handler used as head in the pipeline
	 * @param <I> the type of the handlers input
	 * @param <O> the type of the handlers output
	 * @return A pipeline object with the specified handler as head
	 */
	public static <I, O> Pipeline<I, I, O> createHead(Handler<I, O> head) {
		return new Pipeline<>(null, head);
	}

	/**
	 * Util method to append a {@link Handler} to an existing {@link Pipeline}.
	 *
	 * @param head the existing pipeline
	 * @param tail the handler that shall be appended to the pipeline
	 * @param <I> The input type of the existing pipeline
	 * @param <T> the output type of the existing pipeline, must be equal to the input type of the new handler
	 * @param <O> the output type of the new handler
	 * @return a new pipeline object consisting of the old pipeline and the handler
	 */
	public static <I, T, O> Pipeline<I, T, O> append(Pipeline<I, ?, T> head, Handler<T, O> tail) {
		return new Pipeline<>(head, tail);
	}

	private Pipeline(Pipeline<I, ?, T> head, Handler<T, O> tail) {
		this.head = head;
		this.tail = tail;
	}

	/**
	 * Runs the pipeline with the input of generic type &lt;I&gt; producing objects of the generic
	 * type &lt;O&gt;
	 *
	 * @param input the input to be processed by this pipeline
	 * @return the result of this pipeline
	 */
	public O run(I input) {
		T headResult;
		if (head == null) {
			headResult = (T) input;
		} else {
			headResult = head.run(input);
		}
		return tail.process(headResult);
	}

}
