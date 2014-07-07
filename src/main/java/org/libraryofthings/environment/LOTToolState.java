package org.libraryofthings.environment;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTToolException;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTScript;
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

	public void call(final String scriptname, final Object... params)
			throws LOTToolException {
		log.info("call " + scriptname + " params:" + params);
		
		List<Object> l = new LinkedList<Object>();
		l.add(this);
		for (Object o : params) {
			l.add(o);
		}

		LOTScript script = tool.getScript(scriptname);

		if (script != null) {
			log.info("calling " + scriptname + " with " + l);
			script.run(env, l);
		} else {
			throw new LOTToolException("Script called '" + scriptname
					+ "' does not exist in " + this);
		}
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

	@Override
	public String toString() {
		return "LOTToolState " + this.tool;
	}
}
