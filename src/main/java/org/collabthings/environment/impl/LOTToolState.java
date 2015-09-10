package org.collabthings.environment.impl;

import org.collabthings.LOTToolException;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTRuntimeEvent;
import org.collabthings.math.LOrientation;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTRuntimeObject;
import org.collabthings.model.LOTTool;
import org.collabthings.model.LOTValues;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

public class LOTToolState implements LOTRuntimeObject {

	private LOTRunEnvironment env;
	private LOTFactoryState factory;

	private final LOrientation o = new LOrientation();
	private LOTTool tool;
	//
	private LLog log = LLog.getLogger(this);
	private boolean inuse;
	private String name;
	private final LOTPool pool;
	private LOTEvents events = new LOTEvents();

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

	@Override
	public PrintOut printOut() {
		PrintOut out = new PrintOut();

		out.append("toolstate");
		out.append(1, "" + out);
		return out;
	}

	@Override
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
		// TODO shouldn't be hard coded like this
		if (!"draw".equals(scriptname)) {
			this.env.recordEvent(this, "calling " + scriptname + " " + script,
					callvalues);
			events.add(new LOTRuntimeEvent(this, "" + scriptname, callvalues));
		}

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
	public LOrientation getOrientation() {
		return o;
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

	public LOTEvents getEvents() {
		return events;
	}
}
