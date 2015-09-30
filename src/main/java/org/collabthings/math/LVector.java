package org.collabthings.math;

import javax.vecmath.Vector3d;

import waazdoh.common.WObject;

public class LVector extends Vector3d {
	private static final long serialVersionUID = 1L;

	public LVector(LVector v) {
		super(v);
	}

	public LVector(double i, double j, double k) {
		super(i, j, k);
	}

	public LVector() {
		super(0, 0, 0);
	}

	public LVector(WObject b) {
		set(b);
	}

	public LVector copy() {
		return new LVector(this);
	}

	public LVector getSub(LVector s) {
		LVector n = new LVector(this);
		n.sub(s);
		return n;
	}

	public LVector getNormalized() {
		LVector n = new LVector(this);
		n.normalize();
		return n;
	}

	public LVector getAdd(LVector l) {
		LVector n = copy();
		n.add(l);
		return n;
	}

	public WObject getBean() {
		WObject b = new WObject();
		b.addValue("x", x);
		b.addValue("y", y);
		b.addValue("z", z);
		return b;
	}

	public void set(WObject b) {
		x = b.getDoubleValue("x");
		y = b.getDoubleValue("y");
		z = b.getDoubleValue("z");
	}

	public String asShortString() {
		return String.format("[%.2f, %.2f, %.2f]", x, y, z);
	}

	public LVector getScaled(double d) {
		LVector l = copy();
		l.scale(d);
		return l;
	}

}
