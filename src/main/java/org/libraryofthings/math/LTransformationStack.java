package org.libraryofthings.math;

import java.util.Stack;

public class LTransformationStack {
	private Stack<LTransformation> stack = new Stack<>();

	public LTransformationStack() {
		LTransformation t = new LTransformation();
		stack.push(t);
	}

	public void push(LTransformation transformation) {
		LTransformation current = stack.peek();
		LTransformation n = current.copy();
		n.mult(transformation);
		stack.push(n);
	}

	public void pull() {
		stack.pop();
	}

	public LTransformation current() {
		return stack.peek();
	}
}
