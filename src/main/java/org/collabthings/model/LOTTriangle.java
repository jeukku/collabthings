package org.collabthings.model;

import org.collabthings.math.LVector;

public final class LOTTriangle {
	private final int a, b, c;
	private final LVector n;

	public LOTTriangle(int a, int b, int c, LVector n) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.n = new LVector(n);
	}

	public LVector getN() {
		return n;
	}

	public int getA() {
		return a;
	}

	public int getB() {
		return b;
	}

	public int getC() {
		return c;
	}
}
