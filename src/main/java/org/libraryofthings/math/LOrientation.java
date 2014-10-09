package org.libraryofthings.math;

public class LOrientation {
	public final LVector location = new LVector();
	public final LVector normal = new LVector(0, 1, 0);
	public double angle = 0.0;

	@Override
	public String toString() {
		return "[O:(" + location + ")(" + normal + ")(" + angle + ")]";
	}
}
