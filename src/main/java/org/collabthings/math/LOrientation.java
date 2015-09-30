package org.collabthings.math;

import waazdoh.common.WObject;

public class LOrientation {
	private static final String VALUENAME_ORIENTATION_LOCATION = "location";
	private static final String VALUENAME_ORIENTATION_NORMAL = "normal";
	private static final String VALUENAME_ORIENTATION_ANGLE = "angle";

	private final LVector location = new LVector();
	private final LVector normal = new LVector(0, 1, 0);
	private double angle = 0.0;

	public LOrientation(WObject o) {
		getLocation().set(new LVector(o.get(VALUENAME_ORIENTATION_LOCATION)));
		getNormal().set(new LVector(o.get(VALUENAME_ORIENTATION_NORMAL)));
		setAngle(o.getDoubleValue(VALUENAME_ORIENTATION_ANGLE));
	}

	public LOrientation() {
		//
	}

	@Override
	public String toString() {
		return "[O:(" + getLocation() + ")(" + getNormal() + ")(" + getAngle()
				+ ")]";
	}

	public WObject getBean() {
		WObject ob = new WObject();
		ob.add(VALUENAME_ORIENTATION_LOCATION, getLocation().getBean());
		ob.add(VALUENAME_ORIENTATION_NORMAL, getNormal().getBean());
		ob.addValue(VALUENAME_ORIENTATION_ANGLE, getAngle());
		return ob;
	}

	public void set(LVector n, double d) {
		getNormal().set(n);
		setAngle(d);
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public LVector getLocation() {
		return location;
	}

	public LVector getNormal() {
		return normal;
	}
}
