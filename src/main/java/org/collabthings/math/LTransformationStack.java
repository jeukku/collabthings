package org.collabthings.math;

import java.util.ArrayDeque;
import java.util.Deque;

public class LTransformationStack {
	private Deque<LTransformation> stack = new ArrayDeque<>();

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
