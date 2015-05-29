package org.collabthings.model;

import org.collabthings.math.LVector;

public class LOTTriangle {
	public int a, b, c;
	public LVector n;

	public LOTTriangle(int a, int b, int c, LVector n) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.n = new LVector(n);
	}
}
