package org.collabthings.unittests;

import org.collabthings.CTTestCase;
import org.collabthings.math.CTMath;

import com.jme3.math.Vector3f;

import waazdoh.datamodel.WObject;

public final class TestVector extends CTTestCase {

	private static final double LENGTH_1_1_1 = 1.7320507764816284;

	public void testVectorLength() {
		Vector3f v = new Vector3f(1, 0, 0);
		assertReallyClose(v.length(), 1.0);
		v = new Vector3f(0, 1, 0);
		assertReallyClose(v.length(), 1.0);
		v = new Vector3f(0, 0, 1);
		assertReallyClose(v.length(), 1.0);
		v = new Vector3f(1, 1, 1);
		assertReallyClose(v.length(), LENGTH_1_1_1);
	}

	public void testMultiply() {
		Vector3f v = getV();
		v.multLocal(6);
		assertReallyClose(v.length(), LENGTH_1_1_1 * 6);
	}

	private Vector3f getV() {
		Vector3f v = new Vector3f(1, 1, 1);
		return v;
	}

	public void testWObject() {
		Vector3f v = new Vector3f(1, 2, 3);
		WObject w = CTMath.getBean(v);
		String t = w.toYaml();
		int length = t.split("\n").length;
		assertTrue("lines in " + t + " is " + length, length == 3);
	}

	public void testSub() {
		Vector3f v = getV().subtract(new Vector3f(1, 1, 0));
		assertReallyClose(v.length(), 1.0);
	}

	public void testAdd() {
		Vector3f v = new Vector3f(1, 1, 0).addLocal(new Vector3f(0, 0, 1));
		assertReallyClose(v.length(), LENGTH_1_1_1);
	}

	public void testNormalize() {
		Vector3f l = new Vector3f(2, 2, 2);
		l.normalizeLocal();
		assertReallyClose(1.0, l.length());
	}

}
