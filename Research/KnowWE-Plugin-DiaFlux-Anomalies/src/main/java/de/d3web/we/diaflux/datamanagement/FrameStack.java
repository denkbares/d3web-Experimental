package de.d3web.we.diaflux.datamanagement;

import java.util.Stack;

public class FrameStack {

	private Stack<EvalResult> stack;

	public FrameStack() {
		stack = new Stack<EvalResult>();
	}

	/**
	 * Merges the EvalResult with the last on the stack
	 * and adds it to the top of the stack
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
	 * Removes the object at the top of the stack
	 * and returns it
	 */
	public EvalResult pop() {
		return stack.pop();
	}

	/**
	 * Tests if the Framestack has no EvalResults
	 */
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	/**
	 * Returns the EvalResult at the top of the stack
	 * without removing it
	 */
	public EvalResult peek() {
		return stack.peek();
	}
}
