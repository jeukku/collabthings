package org.libraryofthings.math;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;

public class LOrientation {
	private Matrix4d m = new Matrix4d();

	public static LOrientation getRotateY(double d) {
		LOrientation t = new LOrientation();
		t.rotateY(d);
		return t;
	}

	private void rotateY(double d) {
		AxisAngle4d a = new AxisAngle4d(new LVector(0, 1, 0), d);
		m.set(a);
	}

	public LVector transform(LVector v) {
		m.transform(v);
		return v;
	}
}
