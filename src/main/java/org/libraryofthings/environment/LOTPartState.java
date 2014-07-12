package org.libraryofthings.environment;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTSubPart;

public class LOTPartState {

	private LOTSubPart part;
	private LOTClient env;
	private LVector location = new LVector();
	//
	private LLog log = LLog.getLogger(this);

	public LOTPartState(final LOTClient env, final LOTSubPart part) {
		this.env = env;
		this.part = part;
	}

	public LVector getLocation() {
		return location;
	}

	public LVector getAbsoluteLocation() {
		// TODO
		return location;
	}

	public LOTSubPart getPart() {
		return part;
	}

	public void addPart(LOTSubPart np) {
		LOTPart p = part.getPart();
		LOTSubPart nsp = new LOTSubPart(p, env);
		nsp.setOrientation(np.getLocation(), np.getNormal());
		p.addSubPart(nsp);
	}

	@Override
	public String toString() {
		return "PartState[" + part + "][" + location + "]";
	}
}
