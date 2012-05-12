/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaflux.datamanagement;

import java.util.Stack;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class FrameStack {

	private final Stack<EvalResult> stack;

	/**
	 * Constructor
	 */
	public FrameStack() {
		stack = new Stack<EvalResult>();
	}

	/**
	 * Merges the EvalResult with the last on the stack and adds it to the top
	 * of the stack
	 * 
	 * @created 08.05.2012
	 * @param evalR
	 * @return
	 */
	public EvalResult push(EvalResult evalR) {
		EvalResult newER = evalR;
		if (!stack.isEmpty()) {

			EvalResult lastER = stack.peek();
			newER = lastER.restrictWith(evalR);

		}
		return stack.push(newER);
	}

	/**
	 * Removes the object at the top of the stack and returns it
	 * 
	 * @created 08.05.2012
	 * @return
	 */
	public EvalResult pop() {
		return stack.pop();
	}

	/**
	 * Tests if the Framestack has no EvalResults
	 * 
	 * @created 08.05.2012
	 * @return
	 */
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	/**
	 * Returns the EvalResult at the top of the stack without removing it
	 * 
	 * @created 08.05.2012
	 * @return
	 */
	public EvalResult peek() {
		return stack.peek();
	}
}
