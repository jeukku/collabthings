package org.collabthings.model;

import org.collabthings.math.LOrientation;
import com.jme3.math.Vector3f;
import com.jme3.math.Transform;

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

	public void setLocation(Vector3f l) {
		orientation.getLocation().set(l);
	}

	public LOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Vector3f n, double d) {
		orientation.set(n, d);

	}

	public Transform getTransformation() {
		return orientation.getTransformation();
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
