package org.collabthings.unittests;

import org.collabthings.CTTestCase;
import org.collabthings.math.LOrientation;

import com.jme3.math.Matrix4f;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

public final class TestTransformation extends CTTestCase {

	public void testNormalRotationAndTranslation() {
		Vector3f v = new Vector3f();
		LOrientation o = new LOrientation(new Vector3f(2, 0, 0), new Vector3f(0, 1, 0), (float) Math.PI / 2);
		Transform t = o.getTransformation();
		t.transformVector(new Vector3f(1, 0, 0), v);
		assertReallyClose(new Vector3f(2, 0, -1), v);
	}

	public void testRotate() {
		Vector3f v = new Vector3f();

		Transform t = new LOrientation(Vector3f.UNIT_Y, (float) -Math.PI / 2).getTransformation(); // 90

		t.transformVector(new Vector3f(1, 0, 0), v);
		assertReallyClose(1, v.length());
		assertClose(0.0, v.x);
		assertClose(0.0, v.y);
		assertClose(1.0, v.z);
	}

	public void testTranslation() {
		Vector3f v = new Vector3f();
		Transform o = new LOrientation(new Vector3f(0, 2, 0)).getTransformation();

		v = o.transformVector(new Vector3f(1, 0, 0), v);
		assertClose(1.0, v.x);
		assertClose(2.0, v.y);
		assertClose(0.0, v.z);
	}

	public void testRotateTreeBack() {
		Vector3f v = new Vector3f();
		Transform t1 = new LOrientation(Vector3f.UNIT_Y, (float) -Math.PI / 2).getTransformation(); // 90
		Transform t2 = new LOrientation(Vector3f.UNIT_Y, (float) Math.PI / 2).getTransformation(); // 90

		t2.combineWithParent(t1);
		//
		v = t2.transformVector(new Vector3f(1, 0, 0), v);
		assertClose(1.0, v.x);
		assertClose(0.0, v.y);
		assertClose(0.0, v.z);
	}

	public void testRotateTranslateRotate() {
		Vector3f v = new Vector3f();
		Transform t1 = new LOrientation(Vector3f.UNIT_Y, (float) -Math.PI / 2).getTransformation(); // 90
		Transform t2 = new LOrientation(new Vector3f(2, 0, 0)).getTransformation(); // 90
		Transform t3 = new LOrientation(Vector3f.UNIT_Y, (float) Math.PI / 2).getTransformation(); // 90

		Matrix4f m1 = t3.toTransformMatrix();
		m1.multLocal(t2.toTransformMatrix());
		m1.multLocal(t1.toTransformMatrix());

		//
		m1.mult(new Vector3f(1, 0, 0), v);

		assertClose(1.0, v.x);
		assertClose(0.0, v.y);
		assertClose(-2.0, v.z);
	}

	private void assertClose(double z, double d) {
		assertTrue("" + z + " was " + d, Math.abs(z - d) < ACCEPTED_DIFFERENCE);
	}

}
