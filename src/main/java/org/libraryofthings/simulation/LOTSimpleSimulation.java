package org.libraryofthings.simulation;

import org.libraryofthings.LLog;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.environment.LOTTask;
import org.libraryofthings.environment.RunEnvironmentListener;
import org.libraryofthings.view.SwingSimulationFrame;

import waazdoh.util.ConditionWaiter;

public class LOTSimpleSimulation implements LOTSimulation,
		RunEnvironmentListener {
	private static final double MAX_STEP = 0.01;
	//

	private LOTRunEnvironment env;
	private LLog log = LLog.getLogger(this);
	private boolean allsuccess = true;
	private LOTStepRunner runner;
	//
	private SwingSimulationFrame view;

	public LOTSimpleSimulation(LOTRunEnvironment runenv) {
		this.env = runenv;
		env.addListener(this);
	}

	public LOTSimpleSimulation(LOTRunEnvironment runenv, boolean b) {
		this(runenv);
		if (b) {
			view = new SwingSimulationFrame(runenv);
		}
	}

	@Override
	public boolean run(int maxruntime) {
		start();

		new ConditionWaiter(() -> check(), maxruntime);
		stop();

		return allsuccess;
	}

	@Override
	public synchronized void taskFailed(LOTRunEnvironment runenv, LOTTask task) {
		log.info("task " + task + " failed in " + runenv);
		allsuccess = false;
	}

	private void stop() {
		log.info("Stopping");
		env.stop();
		runner.stop();
		if (view != null) {
			view.close();
		}
	}

	private void start() {
		runner = new LOTStepRunner(MAX_STEP, dtime -> step(dtime));
		log.info("started " + runner);
	}

	private boolean step(double dtime) {
		env.step(dtime);
		if (view != null) {
			view.step(dtime);
		}
		return !isReady();
	}

	private synchronized boolean check() {
		return isReady() || !this.allsuccess;
	}

	private synchronized boolean isReady() {
		return env.isReady();
	}
}
