package org.libraryofthings.environment;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.model.LOTScript;

import waazdoh.util.ConditionWaiter;

public final class LOTTask {
	private List<LOTTask> subtasks = new LinkedList<LOTTask>();
	//
	private LOTClient client;
	private LOTScript s;
	private Object[] params;
	private boolean isrun;

	public LOTTask(final LOTClient nenv, LOTScript s2, Object[] params2) {
		this.client = nenv;
		this.s = s2;
		this.params = params2;
		//
		LLog.getLogger(this).info("LOTTask " + s.isOK());
	}

	public List<LOTTask> getSubTasks() {
		List<LOTTask> list = new LinkedList<LOTTask>();
		list.addAll(subtasks);
		return list;
	}

	public void addSubTask(LOTTask lotTask) {
		this.subtasks.add(lotTask);
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
}
