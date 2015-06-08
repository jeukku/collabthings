package org.collabthings.model;

import org.collabthings.math.LOrientation;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;

import waazdoh.common.WData;

public class LOTAttachedFactory {
	private LOTFactory factory;

	private LOrientation orientation = new LOrientation();

	public LOTAttachedFactory(LOTFactory f) {
		this.factory = f;
	}

	@Override
	public String toString() {
		return "Factory " + factory + " at " + orientation;
	}

	public LOTFactory getFactory() {
		return factory;
	}

	public void setLocation(LVector l) {
		orientation.getLocation().set(l);
	}

	public LOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(LVector n, double d) {
		orientation.set(n, d);

	}

	public LTransformation getTransformation() {
		return new LTransformation(orientation);
	}

	public void set(WData wData) {
		orientation = new LOrientation(wData);
	}
}
