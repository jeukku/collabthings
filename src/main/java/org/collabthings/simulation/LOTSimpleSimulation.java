package org.collabthings.simulation;

import java.util.LinkedList;
import java.util.List;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTRuntimeEvent;
import org.collabthings.environment.LOTTask;
import org.collabthings.environment.RunEnvironmentListener;
import org.collabthings.util.LLog;
import org.collabthings.view.JFXRunEnvironmentFrame;

import waazdoh.client.utils.ConditionWaiter;

public class LOTSimpleSimulation implements LOTSimulation,
		RunEnvironmentListener {
	private static final double MAX_STEP = 0.002;
	private static final double MIN_STEP = 0.001;
	//

	private LOTRunEnvironment env;
	private LLog log = LLog.getLogger(this);
	private boolean allsuccess = true;
	private LOTStepRunner runner;
	//
	private JFXRunEnvironmentFrame view;
	private List<LOTRuntimeEvent> events = new LinkedList<LOTRuntimeEvent>();
	private boolean done;

	public LOTSimpleSimulation(LOTRunEnvironment runenv) {
		this.env = runenv;
		env.addListener(this);
	}

	public LOTSimpleSimulation(LOTRunEnvironment runenv, boolean b) {
		this(runenv);
		if (b) {
			view = new JFXRunEnvironmentFrame(runenv);
		}
	}

	@Override
	public void event(LOTRuntimeEvent e) {
		events.add(e);
	}

	@Override
	public boolean run(int maxruntime) {
		start();

		new ConditionWaiter(() -> check(), maxruntime);
		stop();

		done = true;
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
		runner = new LOTStepRunner(MAX_STEP, MIN_STEP, dtime -> step(dtime));
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

	public boolean isDone() {
		return done;
	}
}
