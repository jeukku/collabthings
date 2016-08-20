package org.collabthings.math;

import java.util.ArrayDeque;
import java.util.Deque;

import com.jme3.math.Matrix4f;
import com.jme3.math.Transform;

public class TransformStack {
	private Deque<Matrix4f> stack = new ArrayDeque<>();

	public TransformStack() {
		Transform t = new Transform();
		stack.push(t.toTransformMatrix());
	}

	public void push(Transform transformation) {
		Matrix4f current = stack.peek();
		Matrix4f n = current.clone();
		n = n.mult(transformation.toTransformMatrix());
		stack.push(n);
	}

	public void pop() {
		stack.pop();
	}

	public Matrix4f current() {
		return stack.peek();
	}
}
