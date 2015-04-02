package org.libraryofthings.math;

import waazdoh.common.WData;



public class LOrientation {
	private static final String VALUENAME_ORIENTATION_LOCATION = "location";
	private static final String VALUENAME_ORIENTATION_NORMAL = "normal";
	private static final String VALUENAME_ORIENTATION_ANGLE = "angle";

	private final LVector location = new LVector();
	private final LVector normal = new LVector(0, 1, 0);
	private double angle = 0.0;

	public LOrientation(WData ob) {
		getLocation().set(new LVector(ob.get(VALUENAME_ORIENTATION_LOCATION)));
		getNormal().set(new LVector(ob.get(VALUENAME_ORIENTATION_NORMAL)));
		setAngle(ob.getDoubleValue(VALUENAME_ORIENTATION_ANGLE));
	}

	public LOrientation() {
		//
	}

	@Override
	public String toString() {
		return "[O:(" + getLocation() + ")(" + getNormal() + ")(" + getAngle()
				+ ")]";
	}

	public WData getBean(String beannameOrientation) {
		WData ob = new WData(beannameOrientation);
		ob.add(getLocation().getBean(VALUENAME_ORIENTATION_LOCATION));
		ob.add(getNormal().getBean(VALUENAME_ORIENTATION_NORMAL));
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
