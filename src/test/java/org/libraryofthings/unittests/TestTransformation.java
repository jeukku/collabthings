package org.libraryofthings.unittests;

import org.libraryofthings.LOTTestCase;
import org.libraryofthings.math.LOrientation;
import org.libraryofthings.math.LVector;

public final class TestTransformation extends LOTTestCase {

	public void testRotate() {
		LVector v = new LVector(1, 0, 0);
		LOrientation t = LOrientation.getRotateY(-Math.PI / 2); // 90

		v = t.transform(v);
		assertClose(0.0, v.x);
		assertClose(0.0, v.y);
		assertClose(1.0, v.z);
	}

	private void assertClose(double z, double d) {
		assertTrue("" + z + " was " + d, Math.abs(z - d) < 0.00000000000001);
	}

}
