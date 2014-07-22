package org.libraryofthings.environment;

import org.libraryofthings.LLog;
import org.libraryofthings.model.LOTScript;

import waazdoh.util.ConditionWaiter;

public final class LOTTask {
	private LOTScript s;
	private Object[] params;
	private boolean isrun;

	public LOTTask(LOTScript s2, Object[] params2) {
		this.s = s2;
		this.params = params2;
		//
		LLog.getLogger(this).info("LOTTask " + s.isOK());
	}

	public boolean run(RunEnvironment runenv) {
		try {
			boolean ret = s.run(runenv, params);
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
		return "LOTTask[" + this.s + "][" + params + "]";
	}

	public boolean isRun() {
		return isrun;
	}
}
