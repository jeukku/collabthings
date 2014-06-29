package org.libraryofthings.unittests;

import org.libraryofthings.LOTTestCase;
import org.libraryofthings.math.LVector;

public final class TestMath extends LOTTestCase {

	private static final double LENGTH_1_1_1 = 1.7320508075688;
	private static final double ACCEPTED_DIFFERENCE = 0.000000000001;

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
		v.mult(6);
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
		LVector v = new LVector(1, 1, 0).add(new LVector(0, 0, 1));
		assertReallyClose(v.length(), LENGTH_1_1_1);
	}

	private void assertReallyClose(double valuea, double valueb) {
		assertTrue("expecting " + valueb + ",but is " + valuea,
				Math.abs(valuea - valueb) < ACCEPTED_DIFFERENCE);
	}
}
