package org.libraryofthings.simulation;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LLog;
import org.libraryofthings.environment.LOTTask;
import org.libraryofthings.environment.RunEnvironment;

import waazdoh.util.ConditionWaiter;

public class LOTSimpleSimulation implements LOTSimulation {
	private static final double MAX_STEP = 0.01;
	//

	private RunEnvironment env;
	private List<LOTTask> runningtasks = new LinkedList<LOTTask>();
	private LLog log = LLog.getLogger(this);
	private boolean allsuccess = true;
	private LOTStepRunner runner;

	public LOTSimpleSimulation(RunEnvironment runenv) {
		this.env = runenv;
	}

	@Override
	public boolean run(int maxruntime) {
		start();

		//
		/*
		 * List<LOTTask> tasks = env.getTasks(); for (LOTTask task : tasks) { if
		 * (!task.run(env)) { return false; } }
		 */

		new ConditionWaiter(() -> check(), maxruntime);
		stop();
		//
		return allsuccess;
	}

	private void stop() {
		env.stop();
		runner.stop();
	}

	private void start() {
		runner = new LOTStepRunner(MAX_STEP, dtime -> step(dtime));
	}

	private boolean step(double dtime) {
		env.step(dtime);
		return !isReady();
	}

	private boolean check() {
		if (!env.getTasks().isEmpty()) {
			final LOTTask task = env.getTasks().get(0);
			env.removeTask(task);
			runningtasks.add(task);
			//
			new Thread(() -> runTask(task)).start();
		}
		//
		return isReady();
	}

	private boolean isReady() {
		return env.getTasks().isEmpty() && runningtasks.isEmpty();
	}

	private void runTask(LOTTask task) {
		if (!task.run(env)) {
			this.allsuccess = false;
		}
		runningtasks.remove(task);
	}
}
