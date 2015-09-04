package org.collabthings.environment.impl;

import org.collabthings.environment.LOTScriptRunner;
import org.collabthings.environment.LOTTask;
import org.collabthings.model.LOTValues;
import org.collabthings.util.LLog;

import waazdoh.client.utils.ConditionWaiter;

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

	@Override
	public boolean run() {
		boolean ret = s.run(values);
		isrun = true;
		return ret;
	}

	@Override
	public String getError() {
		return s.getError();
	}

	public void waitUntilFinished() {
		ConditionWaiter.wait(() -> this.isrun, 0);
	}

	@Override
	public String toString() {
		return "LOTTask[" + this.s + "][" + values + "]";
	}

	public boolean isRun() {
		return isrun;
	}
}
