package org.collabthings.environment.impl;

import java.util.LinkedList;
import java.util.List;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.math.LOrientation;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTRuntimeObject;
import org.collabthings.model.LOTSubPart;
import org.collabthings.util.PrintOut;

public class LOTPartState implements LOTRuntimeObject {
	private LOTPart part;

	private LOTFactoryState factory;
	private LOrientation orientation = new LOrientation();
	private List<LOTPartStateListener> listeners;

	public LOTPartState(final LOTRunEnvironment runenv,
			final LOTFactoryState factory, final LOTPart part) {
		this.part = part;
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
		if (factory != null) {
			factory.remove(this);
		}

		part = null;
		factory = null;

		if (listeners != null) {
			for (LOTPartStateListener l : listeners) {
				l.destroyed();
			}
		}
	}

	public void addListener(LOTPartStateListener l) {
		if (listeners == null) {
			listeners = new LinkedList<LOTPartState.LOTPartStateListener>();
		}
		listeners.add(l);
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

	public static interface LOTPartStateListener {

		void destroyed();

	}
}
