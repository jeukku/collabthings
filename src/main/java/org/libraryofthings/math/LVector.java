package org.libraryofthings.math;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import waazdoh.util.xml.JBean;

public final class LVector {
	private static final int _X = 0;
	private static final int _Y = 1;
	private static final int _Z = 2;
	private static final int VALUE_COUNT = 3;
	//
	private RealVector v = new ArrayRealVector(new double[VALUE_COUNT]);

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public LVector(double x, double y, double z) {
		v.setEntry(_X, x);
		v.setEntry(_Y, y);
		v.setEntry(_Z, z);
	}

	/**
	 * Zero vector
	 */
	public LVector() {
		// zeros
	}

	public LVector(JBean b) {
		set(b);
	}

	@Override
	public String toString() {
		return "V[" + getX() + ", " + getY() + ", " + getZ() + "]";
	}

	public void set(LVector location) {
		v.setEntry(_X, location.getX());
		v.setEntry(_Y, location.getY());
		v.setEntry(_Z, location.getZ());
	}

	public double getY() {
		return v.getEntry(_Y);
	}

	public double getX() {
		return v.getEntry(_X);
	}

	public double getZ() {
		return v.getEntry(_Z);
	}

	public LVector copy() {
		return new LVector(getX(), getY(), getZ());
	}

	public LVector add(LVector addv) {
		v.add(addv.v);
		return this;
	}

	public LVector getSub(LVector b) {
		LVector ret = copy();
		ret.sub(b);
		return ret;
	}

	private void sub(LVector b) {
		v.subtract(b.v);
	}

	public double length() {
		return v.getL1Norm();
	}

	public void mult(double d) {
		v.mapMultiply(d);
	}

	public JBean getBean() {
		String name = "vector";
		return getBean(name);
	}

	public JBean getBean(String name) {
		JBean b = new JBean(name);
		b.addValue("x", getX());
		b.addValue("y", getY());
		b.addValue("z", getZ());
		return b;
	}

	public void set(JBean b) {
		v.setEntry(_X, b.getDoubleValue("x"));
		v.setEntry(_Y, b.getDoubleValue("y"));
		v.setEntry(_Z, b.getDoubleValue("z"));
	}

}
