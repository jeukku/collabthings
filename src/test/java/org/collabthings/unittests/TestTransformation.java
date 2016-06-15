package org.collabthings.unittests;

import org.collabthings.CTTestCase;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;

public final class TestTransformation extends CTTestCase {

	public void testNormalRotationAndTranslation() {
		LVector v = new LVector(1, 0, 0);
		LTransformation t = new LTransformation(new LVector(2, 0, 0), new LVector(0, 1, 0), Math.PI / 2);
		t.transform(v);
		assertReallyClose(new LVector(2, 0, -1), v);
	}

	public void testRotate() {
		LVector v = new LVector(1, 0, 0);
		LTransformation t = LTransformation.getRotateY(-Math.PI / 2); // 90

		v = t.transform(v);
		assertClose(0.0, v.x);
		assertClose(0.0, v.y);
		assertClose(1.0, v.z);
	}

	public void testTranslation() {
		LVector v = new LVector(1, 0, 0);
		LTransformation o = LTransformation.getTranslate(0, 2, 0);
		v = o.transform(v);
		assertClose(1.0, v.x);
		assertClose(2.0, v.y);
		assertClose(0.0, v.z);
	}

	public void testRotateTreeBack() {
		LVector v = new LVector(1, 0, 0);
		LTransformation t1 = LTransformation.getRotateY(-Math.PI / 2); // 90
		LTransformation t2 = LTransformation.getRotateY(Math.PI / 2); // 90

		t2.mult(t1);
		//
		v = t2.transform(v);
		assertClose(1.0, v.x);
		assertClose(0.0, v.y);
		assertClose(0.0, v.z);
	}

	public void testRotateTranslateRotate() {
		LVector v = new LVector(1, 0, 0);
		LTransformation t1 = LTransformation.getRotateY(-Math.PI / 2); // 90
		LTransformation t2 = LTransformation.getTranslate(2, 0, 0); // 90
		LTransformation t3 = LTransformation.getRotateY(-Math.PI / 2); // 90

		t1.mult(t2);
		t1.mult(t3);

		//
		v = t1.transform(v);

		assertClose(-1.0, v.x);
		assertClose(0.0, v.y);
		assertClose(2.0, v.z);
	}

	private void assertClose(double z, double d) {
		assertTrue("" + z + " was " + d, Math.abs(z - d) < 0.000000001);
	}

}
