package org.collabthings.model;

import com.jme3.math.Vector3f;

public final class CTTriangle {
	private final int a, b, c;
	private final Vector3f n;

	public CTTriangle(int a, int b, int c, Vector3f nn) {
		this.a = a;
		this.b = b;
		this.c = c;
		if (nn != null) {
			this.n = new Vector3f(nn);
		} else {
			n = null;
		}
	}

	public Vector3f getN() {
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
