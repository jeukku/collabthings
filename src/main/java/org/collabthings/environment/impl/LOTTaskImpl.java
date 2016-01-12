package org.collabthings.environment.impl;

import org.collabthings.environment.LOTScriptRunner;
import org.collabthings.environment.LOTEnvironmentTask;
import org.collabthings.model.LOTValues;
import org.collabthings.util.LLog;

import waazdoh.client.utils.ConditionWaiter;

public final class LOTTaskImpl implements LOTEnvironmentTask {
	private LOTScriptRunner s;
	private LOTValues values;
	private boolean isrun;

	public LOTTaskImpl(LOTScriptRunner s2, LOTValues values2) {
		this.s = s2;
		this.values = values2;
		//
		LLog.getLogger(this).info("LOTTask " + s.toString());
	}

	@Override
	public boolean run() {
		boolean ret = s.run(values);
		isrun = true;
		LLog.getLogger(this).info("Task done " + ret);
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
