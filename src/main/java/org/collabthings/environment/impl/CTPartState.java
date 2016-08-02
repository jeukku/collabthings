package org.collabthings.environment.impl;

import java.util.ArrayList;
import java.util.List;

import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.math.LOrientation;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTRuntimeObject;
import org.collabthings.model.CTSubPart;
import org.collabthings.util.PrintOut;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

public class CTPartState implements CTRuntimeObject {
	private CTPart part;

	private CTFactoryState factory;
	private LOrientation orientation = new LOrientation();
	private List<CTPartStateListener> listeners;

	public CTPartState(final CTRunEnvironment runenv, final CTFactoryState factory, final CTPart part) {
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
			for (CTPartStateListener l : listeners) {
				l.destroyed();
			}
		}
	}

	public void addListener(CTPartStateListener l) {
		if (listeners == null) {
			listeners = new ArrayList<CTPartState.CTPartStateListener>();
		}
		listeners.add(l);
	}

	public Vector3f getLocation() {
		return orientation.getLocation().clone();
	}

	public void setLocation(Vector3f v) {
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

	public CTPart getPart() {
		return part;
	}

	public void addPart(CTSubPart np) {
		CTSubPart nsp = part.newSubPart();
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

	public Transform getTransformation() {
		return orientation.getTransformation();
	}

	public static interface CTPartStateListener {

		void destroyed();

	}
}
