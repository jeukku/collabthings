package org.libraryofthings.environment;

import org.libraryofthings.LLog;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTSubPart;

public class LOTPartState implements LOTRuntimeObject {
	private LOTPart part;
	private LVector location = new LVector();
	//
	private LLog log = LLog.getLogger(this);
	private RunEnvironment runenv;
	//
	private LOTRuntimeObject parent;

	public LOTPartState(final RunEnvironment runenv, final LOTPart part) {
		this.part = part;
		this.runenv = runenv;
	}

	public LVector getLocation() {
		return location;
	}

	@Override
	public void step(double dtime) {
		// nothing to do
	}

	@Override
	public void stop() {
		// nothing to do
	}

	@Override
	public void setParent(final LOTRuntimeObject nparent) {
		this.parent = nparent;
	}

	public LVector getAbsoluteLocation() {
		if (parent != null) {
			return parent.getLocation().copy().add(getLocation());
		} else {
			return getLocation();
		}
	}

	public LOTPart getPart() {
		return part;
	}

	public void addPart(LOTSubPart np) {
		LOTSubPart nsp = part.newSubPart();
		nsp.setPart(np.getPart());
		nsp.setOrientation(np.getLocation(), np.getNormal());
	}

	@Override
	public String getParameter(String name) {
		if (parent != null) {
			return parent.getParameter(name);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return "PartState[" + part + "][" + location + "]";
	}
}
