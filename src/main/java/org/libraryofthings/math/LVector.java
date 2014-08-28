package org.libraryofthings.math;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import waazdoh.client.model.JBean;

public final class LVector {
	private static final int X = 0;
	private static final int Y = 1;
	private static final int Z = 2;
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
		v.setEntry(X, x);
		v.setEntry(Y, y);
		v.setEntry(Z, z);
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

	private void set(RealVector y) {
		v.setEntry(X, y.getEntry(X));
		v.setEntry(Y, y.getEntry(Y));
		v.setEntry(Z, y.getEntry(Z));
	}

	@Override
	public String toString() {
		return "V[" + getX() + ", " + getY() + ", " + getZ() + "]";
	}

	public void set(LVector location) {
		v.setEntry(X, location.getX());
		v.setEntry(Y, location.getY());
		v.setEntry(Z, location.getZ());
	}

	public double getY() {
		return v.getEntry(Y);
	}

	public double getX() {
		return v.getEntry(X);
	}

	public double getZ() {
		return v.getEntry(Z);
	}

	public LVector copy() {
		return new LVector(getX(), getY(), getZ());
	}

	public LVector add(LVector addv) {
		set(v.add(addv.v));
		return this;
	}

	public LVector getSub(LVector b) {
		LVector ret = copy();
		ret.sub(b);
		return ret;
	}

	public void sub(LVector b) {
		set(v.subtract(b.v));
	}

	public double length() {
		return v.getNorm();
	}

	public LVector mult(double d) {
		set(v.mapMultiply(d));
		return this;
	}

	public LVector getMult(double d) {
		LVector ret = copy();
		ret.mult(d);
		return ret;
	}

	public void normalize() {
		set(v.mapDivide(length()));
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
		v.setEntry(X, b.getDoubleValue("x"));
		v.setEntry(Y, b.getDoubleValue("y"));
		v.setEntry(Z, b.getDoubleValue("z"));
	}

	public LVector getNormalized() {
		LVector c = copy();
		c.normalize();
		return c;
	}

}
