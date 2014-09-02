package org.libraryofthings.environment;

import org.libraryofthings.LLog;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTValues;

import waazdoh.util.ConditionWaiter;

public final class LOTTask {
	private LOTScript s;
	private LOTValues values;
	private boolean isrun;
	private LOTRuntimeObject runtimeobject;

	public LOTTask(LOTScript s2, LOTRuntimeObject o, LOTValues values) {
		this.s = s2;
		this.values = values;
		this.runtimeobject = o;
		//
		LLog.getLogger(this).info("LOTTask " + s.isOK());
	}

	public boolean run(RunEnvironment runenv) {
		try {
			boolean ret = s.run(runenv, runtimeobject, values);
			isrun = true;
			return ret;
		} catch (Exception e) {
			LLog.getLogger(this).error(this, "run", e);
			isrun = true;
			return false;
		}
	}

	public void waitUntilFinished() {
		new ConditionWaiter(() -> this.isrun, 0);
	}

	public String toString() {
		return "LOTTask[" + this.s + "][" + values + "]";
	}

	public boolean isRun() {
		return isrun;
	}
}
