package org.collabthings.environment.impl;

import org.collabthings.environment.CTEnvironmentTask;
import org.collabthings.environment.CTScriptRunner;
import org.collabthings.model.CTValues;
import org.collabthings.util.LLog;

import waazdoh.client.utils.ConditionWaiter;

public final class CTTaskImpl implements CTEnvironmentTask {
	private CTScriptRunner s;
	private CTValues values;
	private boolean isrun;

	public CTTaskImpl(CTScriptRunner s2, CTValues values2) {
		this.s = s2;
		this.values = values2;
		//
		LLog.getLogger(this).info("CTTask " + s.toString());
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
		return "CTTask[" + this.s + "][" + values + "]";
	}

	public boolean isRun() {
		return isrun;
	}
}
