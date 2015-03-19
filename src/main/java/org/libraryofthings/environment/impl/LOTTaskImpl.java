package org.libraryofthings.environment.impl;

import org.libraryofthings.LLog;
import org.libraryofthings.environment.LOTScriptRunner;
import org.libraryofthings.environment.LOTTask;
import org.libraryofthings.model.LOTValues;

import waazdoh.common.ConditionWaiter;

public final class LOTTaskImpl implements LOTTask {
	private LOTScriptRunner s;
	private LOTValues values;
	private boolean isrun;

	public LOTTaskImpl(LOTScriptRunner s2, LOTValues values2) {
		this.s = s2;
		this.values = values2;
		//
		LLog.getLogger(this).info("LOTTask " + s);
	}

	public boolean run() {
		try {
			boolean ret = s.run(values);
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
