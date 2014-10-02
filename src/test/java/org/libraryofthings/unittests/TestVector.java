package org.libraryofthings.unittests;

import org.libraryofthings.LOTTestCase;
import org.libraryofthings.math.LVector;

public final class TestVector extends LOTTestCase {

	private static final double LENGTH_1_1_1 = 1.7320508075688;

	public void testVectorLength() {
		LVector v = new LVector(1, 0, 0);
		assertReallyClose(v.length(), 1.0);
		v = new LVector(0, 1, 0);
		assertReallyClose(v.length(), 1.0);
		v = new LVector(0, 0, 1);
		assertReallyClose(v.length(), 1.0);
		v = new LVector(1, 1, 1);
		assertReallyClose(v.length(), LENGTH_1_1_1);
	}

	public void testMultiply() {
		LVector v = getV();
		v.scale(6);
		assertReallyClose(v.length(), LENGTH_1_1_1 * 6);
	}

	private LVector getV() {
		LVector v = new LVector(1, 1, 1);
		return v;
	}

	public void testSub() {
		LVector v = getV().getSub(new LVector(1, 1, 0));
		assertReallyClose(v.length(), 1.0);
	}

	public void testAdd() {
		LVector v = new LVector(1, 1, 0).getAdd(new LVector(0, 0, 1));
		assertReallyClose(v.length(), LENGTH_1_1_1);
	}

	public void testNormalize() {
		LVector l = new LVector(2, 2, 2);
		l.normalize();
		assertReallyClose(l.length(), 1.0);
	}

	public void testShortString() {
		assertEquals("[0.00. 0.00. 0.00]", new LVector().asShortString().replace(',', '.'));
	}
}
