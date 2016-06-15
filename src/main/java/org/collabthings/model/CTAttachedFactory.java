package org.collabthings.model;

import org.collabthings.math.LOrientation;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;

import waazdoh.common.WObject;

public class CTAttachedFactory {
	private CTFactory factory;

	private LOrientation orientation = new LOrientation();

	private String bookmark;

	public CTAttachedFactory(CTFactory f) {
		this.factory = f;
	}

	public CTAttachedFactory(String nbookmark, CTFactory f) {
		this.factory = f;
		this.bookmark = nbookmark;
	}

	@Override
	public String toString() {
		return "Child factory " + factory + " at " + orientation;
	}

	public CTFactory getFactory() {
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
