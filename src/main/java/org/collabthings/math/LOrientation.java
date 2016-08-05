package org.collabthings.math;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

import waazdoh.common.WObject;

public class LOrientation {
	private static final String VALUENAME_ORIENTATION_LOCATION = "location";
	private static final String VALUENAME_ORIENTATION_NORMAL = "normal";
	private static final String VALUENAME_ORIENTATION_ANGLE = "angle";

	private final Vector3f location = new Vector3f();
	private final Vector3f normal = new Vector3f(0, 1, 0);
	private double angle = 0.0;

	public LOrientation(WObject o) {
		getLocation().set(CTMath.parseVector(o.get(VALUENAME_ORIENTATION_LOCATION)));
		getNormal().set(CTMath.parseVector(o.get(VALUENAME_ORIENTATION_NORMAL)));
		setAngle(o.getDoubleValue(VALUENAME_ORIENTATION_ANGLE));
	}

	public LOrientation() {
		//
	}

	public LOrientation(Vector3f n, float f) {
		normal.set(n);
		angle = f;
	}

	public LOrientation(Vector3f vector3f) {
		location.set(vector3f);
	}

	public LOrientation(Vector3f nlocation, Vector3f nnormal, float nangle) {
		location.set(nlocation);
		normal.set(nnormal);
		angle = nangle;
	}

	@Override
	public String toString() {
		return "[O:(" + getLocation() + ")(" + getNormal() + ")(" + getAngle() + ")]";
	}

	public WObject getBean() {
		WObject ob = new WObject();
		ob.add(VALUENAME_ORIENTATION_LOCATION, CTMath.getBean(getLocation()));
		ob.add(VALUENAME_ORIENTATION_NORMAL, CTMath.getBean(getNormal()));
		ob.addValue(VALUENAME_ORIENTATION_ANGLE, getAngle());
		return ob;
	}

	public void set(Vector3f n, double d) {
		getNormal().set(n);
		setAngle(d);
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = CTMath.limitAngle(angle);
	}

	public Vector3f getLocation() {
		return location;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public Transform getTransformation() {
		Quaternion quaternion = new Quaternion((float) (normal.x * Math.sin(angle / 2)), (float) (normal.y * Math.sin(angle / 2)),
				(float) (normal.z * Math.sin(angle / 2)), (float) (Math.cos(CTMath.limitAngle(angle / 2))));
		return new Transform(this.location,
				quaternion);
	}
}
