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
	private LOTFactoryState factory;

	public LOTPartState(final RunEnvironment runenv,
			final LOTFactoryState factory, final LOTPart part) {
		this.part = part;
		this.runenv = runenv;
		this.factory = factory;
	}

	public void destroy() {
		factory.remove(this);
		part = null;
		location = null;
		factory = null;
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

	public LVector getAbsoluteLocation() {
		if (factory != null) {
			return factory.getLocation().copy().add(getLocation());
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
		if (factory != null) {
			return factory.getParameter(name);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return "PartState[" + part + "][" + location + "]";
	}
}
