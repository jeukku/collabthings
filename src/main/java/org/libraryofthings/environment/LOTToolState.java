package org.libraryofthings.environment;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTToolException;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;
import org.libraryofthings.model.LOTValues;

public class LOTToolState implements LOTRuntimeObject {

	private RunEnvironment env;
	private LOTFactoryState factory;

	private LOTTool tool;
	private LVector location = new LVector();
	private LVector normal = new LVector(1, 0, 0);
	//
	private LLog log = LLog.getLogger(this);
	private boolean inuse;
	private String name;
	private LOTPool pool = new LOTPool();

	public LOTToolState(final String name, final RunEnvironment runenv,
			final LOTTool ntool, final LOTFactoryState factorystate) {
		log.info("LOTToolState with " + runenv + " tool:" + ntool.getName());

		this.name = name;
		this.factory = factorystate;
		this.env = runenv;
		this.tool = ntool;
		this.factory = factorystate;
	}

	public String getName() {
		return name;
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
	public String getParameter(String name) {
		return factory.getParameter(name);
	}

	@Override
	public void setParent(LOTRuntimeObject nparent) {
		//
	}

	public void call(final String scriptname, final LOTValues values)
			throws LOTToolException {
		LOTScript script = tool.getScript(scriptname);

		LOTValues callvalues = values != null ? values.copy() : new LOTValues();

		callvalues.put("tool", this);

		StringBuilder sb = new StringBuilder();
		sb.append("Calling " + scriptname + "(" + this + ")\n");
		sb.append("\tvalues:" + callvalues);
		sb.append("\tEnvironment:" + env.getInfo().replace(";", "\n\t"));
		log.info(sb.toString());

		if (script != null) {
			script.run(env, this, callvalues);
		} else {
			throw new LOTToolException("Script called '" + scriptname
					+ "' does not exist in " + this);
		}
		log.info("call " + scriptname + " done");
	}

	public void moveTo(LVector l) {
		moveTo(l, normal);
	}

	public LVector getAbsoluteLocation() {
		if (factory != null) {
			return factory.getLocation().copy().add(location);
		} else {
			return location;
		}
	}

	public void moveTo(LVector l, LVector n) {
		log.info("moveTo " + l + " " + n);
		this.factory.requestMove(this, l, n);
	}

	public LVector getLocation() {
		return location;
	}

	public void setLocation(LVector l, LVector n) {
		this.location.set(l);
		this.normal.set(n);
	}

	public void setAvailable() {
		this.inuse = false;
	}

	public void setInUse() {
		this.inuse = true;
	}

	public boolean isInUse() {
		return this.inuse;
	}

	public LOTPool getPool() {
		return pool;
	}

	@Override
	public String toString() {
		return "LOTToolState[" + this.tool + "][" + this.location + "]";
	}
}
