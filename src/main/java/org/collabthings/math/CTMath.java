package org.collabthings.math;

import com.jme3.math.Vector3f;

import waazdoh.common.WObject;

public class CTMath {

	public static double radToDegrees(double angle) {
		angle = limitAngle((float) angle);

		return 360.0 * angle / (2 * Math.PI);
	}

	public static float limitAngle(double angle) {
		while (angle < -2 * Math.PI)
			angle += 2 * Math.PI;
		while (angle > 2 * Math.PI)
			angle -= 2 * Math.PI;
		return (float) angle;
	}

	public static double degreesToRad(double na) {
		double r = 2 * Math.PI * na / 360.0;
		return limitAngle((float) r);
	}

	public static Vector3f parseVector(WObject o) {
		return new Vector3f((float) o.getDoubleValue("x"), (float) o.getDoubleValue("y"),
				(float) o.getDoubleValue("z"));
	}

	public static WObject getBean(Vector3f v) {
		WObject b = new WObject();
		b.addValue("x", v.x);
		b.addValue("y", v.y);
		b.addValue("z", v.z);
		return b;
	}

}
