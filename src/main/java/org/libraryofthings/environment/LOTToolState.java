package org.libraryofthings.environment;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTToolException;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTTool;

public class LOTToolState {

	private RunEnvironment env;
	private LOTTool tool;
	private LVector location = new LVector();
	private LVector normal = new LVector(1, 0, 0);
	//
	private LLog log = LLog.getLogger(this);

	public LOTToolState(final RunEnvironment runenv, final LOTTool ntool) {
		this.env = runenv;
		this.tool = ntool;
	}

	public void call(final String nname, final Object... params) throws LOTToolException {

		List<Object> l = new LinkedList<Object>();
		l.add(this);
		for (Object o : params) {
			l.add(o);
		}

		tool.call(env, nname, l.toArray());
	}

	public void moveTo(LVector l) {
		moveTo(location, normal);
	}

	public void moveTo(LVector l, LVector n) {
		log.info("moving to l:" + l + " n:" + n);
		this.env.requestMove(this, l, n);
	}

	public LVector getLocation() {
		return location;
	}

	public void setLocation(LVector l, LVector n) {
		this.location.set(l);
		this.normal.set(n);
	}
}
