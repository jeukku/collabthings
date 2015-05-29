package org.collabthings.math;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class LTransformation {
	private Matrix4d m = new Matrix4d();
	public static final Vector3d UP = new Vector3d(0, 1, 0);

	final private AxisAngle4d a = new AxisAngle4d();

	public LTransformation() {
		m.setIdentity();
	}

	public LTransformation(LOrientation o) {
		this(o.getLocation(), o.getNormal(), o.getAngle());
	}

	public LTransformation(LVector location, LVector orientationnormal,
			double orientationangle) {
		m.setIdentity();

		// RotationAxis = cross(N, N')
		// RotationAngle = arccos(dot(N, N') / (|N| * |N'|))

		mult(LTransformation.getTranslate(location.x, location.y, location.z));

		Vector3d cross = new Vector3d();
		cross.cross(UP, orientationnormal);
		if (cross.length() > 0) {
			cross.normalize();
			Vector3d dot = new Vector3d(UP);
			double angle = Math.acos(dot.dot(orientationnormal));
			mult(LTransformation.getRotate(orientationnormal, orientationangle));
			mult(LTransformation.getRotate(cross, angle));
		} else {
			mult(LTransformation.getRotate(UP, orientationangle));
		}

	}

	@Override
	public String toString() {
		return "LT:" + m;
	}

	public static LTransformation getRotate(Vector3d v, double d) {
		LTransformation t = new LTransformation();
		v.normalize();
		t.rotate(v, d);
		return t;
	}

	public static LTransformation getRotateY(double d) {
		LTransformation t = new LTransformation();
		t.rotateY(d);
		return t;
	}

	public static LTransformation getTranslate(double x, double y, double z) {
		LTransformation o = new LTransformation();
		o.translate(x, y, z);
		return o;
	}

	public static LTransformation getTranslate(LVector v) {
		return LTransformation.getTranslate(v.x, v.y, v.z);
	}

	public void translate(double x, double y, double z) {
		m.setTranslation(new Vector3d(x, y, z));
	}

	public void rotateY(double d) {
		AxisAngle4d a = new AxisAngle4d(new Vector3d(0, 1, 0), d);
		m.set(a);
	}

	public void rotate(Vector3d v, double d) {
		a.set(v, d);
		m.set(a);
	}

	public LVector transform(LVector v) {
		double nx = v.x * m.m00 + v.y * m.m01 + v.z * m.m02 + m.m03;
		double ny = v.x * m.m10 + v.y * m.m11 + v.z * m.m12 + m.m13;
		double nz = v.x * m.m20 + v.y * m.m21 + v.z * m.m22 + m.m23;

		v.x = nx;
		v.y = ny;
		v.z = nz;

		return v;
	}

	public LVector transformw0(LVector v) {
		double nx = v.x * m.m00 + v.y * m.m01 + v.z * m.m02;
		double ny = v.x * m.m10 + v.y * m.m11 + v.z * m.m12;
		double nz = v.x * m.m20 + v.y * m.m21 + v.z * m.m22;

		v.x = nx;
		v.y = ny;
		v.z = nz;

		return v;
	}

	public void mult(LTransformation t1) {
		m.mul(t1.m);
	}

	public LTransformation copy() {
		LTransformation n = new LTransformation();
		n.m.set(m);
		return n;
	}

}
