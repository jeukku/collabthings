package org.libraryofthings;

import javax.script.ScriptException;

import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTTool;

public class LOTToolState {

	private LOTEnvironment env;
	private LOTTool tool;
	private LVector location = new LVector();
	//
	private LLog log = LLog.getLogger(this);

	public LOTToolState(final LOTEnvironment nenv, final LOTTool ntool) {
		this.env = nenv;
		this.tool = ntool;
	}

	public void call(final RunEnvironment nenv, final String nname,
			final Object... params) throws NoSuchMethodException,
			ScriptException {
		tool.call(nenv, nname, params);
	}

	public void moveTo(LVector v) {
		log.info("moving to " + v);
	}

	public LVector getLocation() {
		return location;
	}
}
