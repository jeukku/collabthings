package org.libraryofthings.environment.impl;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTToolException;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.math.LOrientation;
import org.libraryofthings.math.LTransformation;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTTool;
import org.libraryofthings.model.LOTValues;

public class LOTToolState implements LOTRuntimeObject {

	private LOTRunEnvironment env;
	private LOTFactoryState factory;

	private final LOrientation o = new LOrientation();
	private LOTTool tool;
	//
	private LLog log = LLog.getLogger(this);
	private boolean inuse;
	private String name;
	final private LOTPool pool;

	public LOTToolState(final String name, final LOTRunEnvironment runenv,
			final LOTTool ntool, final LOTFactoryState factorystate) {
		log.info("LOTToolState with " + runenv + " tool:" + ntool.getName());

		this.name = name;
		this.factory = factorystate;
		this.env = runenv;
		this.tool = ntool;
		this.factory = factorystate;
		pool = new LOTPool(runenv, this);
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

	public void call(final String scriptname, final LOTValues values)
			throws LOTToolException {

		LOTValues callvalues = values != null ? values.copy() : new LOTValues();

		callvalues.put("tool", this);

		LOTScriptRunnerImpl script = pool.getScript(tool.getScript(scriptname));
		this.env.recordEvent(this, "calling " + scriptname + " " + script, callvalues);
		if (script != null) {
			script.run(callvalues);
		} else {
			throw new LOTToolException("Script called '" + scriptname
					+ "' does not exist in " + this);
		}
	}

	public void moveTo(LVector l) {
		moveTo(l, o.getNormal(), o.getAngle());
	}

	public void moveTo(LVector l, LVector n, double angle) {
		log.info("moveTo " + l + " " + n);
		this.factory.requestMove(this, l, n, angle);
	}

	@Override
	public LTransformation getTransformation() {
		return new LTransformation(o);
	}

	public LVector getLocation() {
		return o.getLocation();
	}

	public void setOrientation(LVector l, LVector n, double angle) {
		o.getLocation().set(l);
		o.getNormal().set(n);
		o.setAngle(angle);
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
		return "LOTToolState[" + this.tool + "][" + this.o + "]";
	}
}
