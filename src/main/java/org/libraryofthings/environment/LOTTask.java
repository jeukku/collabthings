package org.libraryofthings.environment;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.model.LOTScript;

import waazdoh.util.ConditionWaiter;

public final class LOTTask {
	private List<LOTTask> subtasks = new LinkedList<LOTTask>();
	//
	private LOTEnvironment env;
	private LOTScript s;
	private Object[] params;
	private boolean isrun;

	public LOTTask(final LOTEnvironment nenv, LOTScript s2, Object[] params2) {
		this.env = nenv;
		this.s = s2;
		this.params = params2;
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
		boolean ret = s.run(runenv, params);
		isrun = true;
		return ret;
	}

	public void waitUntilFinished() {
		new ConditionWaiter(() -> this.isrun, 0);
	}
}
