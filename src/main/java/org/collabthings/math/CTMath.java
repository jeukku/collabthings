package org.collabthings.math;

public class CTMath {

	public static double radToDegrees(double angle) {
		angle = limitAngle(angle);

		return 360.0 * angle / (2 * Math.PI);
	}

	public static double limitAngle(double angle) {
		while (angle < -2 * Math.PI)
			angle += 2 * Math.PI;
		while (angle > 2 * Math.PI)
			angle -= 2 * Math.PI;
		return angle;
	}

	public static double degreesToRad(double na) {
		double r = 2*Math.PI * na / 360.0;
		return limitAngle(r);
	}

}
