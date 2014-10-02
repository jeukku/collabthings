package org.libraryofthings.environment.impl;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTToolException;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.math.LTransformation;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTTool;
import org.libraryofthings.model.LOTValues;

public class LOTToolState implements LOTRuntimeObject {

	private LOTRunEnvironment env;
	private LOTFactoryState factory;

	private LOTTool tool;
	private LVector location = new LVector();
	private LVector normal = new LVector(1, 0, 0);
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

		StringBuilder sb = new StringBuilder();
		sb.append("Calling " + scriptname + "(" + this + ")\n");
		sb.append("\tvalues:" + callvalues);
		sb.append("\tEnvironment:" + env.getInfo().replace(";", "\n\t"));
		log.info(sb.toString());

		LOTScriptRunnerImpl script = pool.getScript(tool.getScript(scriptname));
		if (script != null) {
			script.run(callvalues);
		} else {
			throw new LOTToolException("Script called '" + scriptname
					+ "' does not exist in " + this);
		}
		log.info("call " + scriptname + " done");
	}

	public void moveTo(LVector l) {
		moveTo(l, normal);
	}

	public void moveTo(LVector l, LVector n) {
		log.info("moveTo " + l + " " + n);
		this.factory.requestMove(this, l, n);
	}

	@Override
	public LTransformation getTransformation() {
		LTransformation t = new LTransformation();
		t.mult(LTransformation.getTranslate(location));
		return t;
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
