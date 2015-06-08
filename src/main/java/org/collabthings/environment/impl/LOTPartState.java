package org.collabthings.environment.impl;

import org.collabthings.LLog;
import org.collabthings.PrintOut;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.math.LOrientation;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTRuntimeObject;
import org.collabthings.model.LOTSubPart;

public class LOTPartState implements LOTRuntimeObject {
	private LOTPart part;
	//
	private LLog log = LLog.getLogger(this);
	private LOTRunEnvironment runenv;
	//
	private LOTFactoryState factory;
	private LOrientation orientation = new LOrientation();

	public LOTPartState(final LOTRunEnvironment runenv,
			final LOTFactoryState factory, final LOTPart part) {
		this.part = part;
		this.runenv = runenv;
		this.factory = factory;
	}

	@Override
	public LOrientation getOrientation() {
		return orientation;
	}

	@Override
	public PrintOut printOut() {
		PrintOut o = new PrintOut();
		o.append("partstate");
		o.append(1, "" + part);
		return o;
	}

	@Override
	public String getName() {
		return getParameter("name");
	}

	public void destroy() {
		factory.remove(this);
		part = null;
		factory = null;
	}

	public LVector getLocation() {
		return orientation.getLocation().copy();
	}

	public void setLocation(LVector v) {
		orientation.getLocation().set(v);
	}

	@Override
	public void step(double dtime) {
		// nothing to do
	}

	@Override
	public void stop() {
		// nothing to do
	}

	public LOTPart getPart() {
		return part;
	}

	public void addPart(LOTSubPart np) {
		LOTSubPart nsp = part.newSubPart();
		nsp.setPart(np.getPart());
		nsp.setOrientation(np.getLocation(), np.getNormal(), np.getAngle());
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
		return "PartState[" + part + "][" + orientation + "]";
	}

	public LTransformation getTransformation() {
		return new LTransformation(orientation);
	}
}
