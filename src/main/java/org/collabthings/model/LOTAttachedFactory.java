package org.collabthings.model;

import org.collabthings.math.LOrientation;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;

import waazdoh.common.WObject;

public class LOTAttachedFactory {
	private LOTFactory factory;

	private LOrientation orientation = new LOrientation();

	private String bookmark;

	public LOTAttachedFactory(LOTFactory f) {
		this.factory = f;
	}

	public LOTAttachedFactory(String nbookmark, LOTFactory f) {
		this.factory = f;
		this.bookmark = nbookmark;
	}

	@Override
	public String toString() {
		return "Child factory " + factory + " at " + orientation;
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

	public void set(WObject wObject) {
		orientation = new LOrientation(wObject);
	}

	public String getBookmark() {
		return bookmark;
	}

	public void setBookmark(String nbookmark) {
		this.bookmark = nbookmark;
	}
}
