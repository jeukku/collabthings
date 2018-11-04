/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

package org.collabthings.simulation;

import java.util.ArrayList;
import java.util.List;

import org.collabthings.environment.CTEnvironmentTask;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.CTRuntimeEvent;
import org.collabthings.environment.RunEnvironmentListener;
import org.collabthings.util.LLog;

import collabthings.core.utils.ConditionWaiter;

public class CTSimpleSimulation implements CTSimulation, RunEnvironmentListener {
	private static final double MAX_STEP = 0.002;
	private static final double MIN_STEP = 0.001;
	//

	private CTRunEnvironment env;
	private LLog log = LLog.getLogger(this);
	private boolean allsuccess = true;
	private CTStepRunner runner;
	//
	private List<CTRuntimeEvent> events = new ArrayList<CTRuntimeEvent>();
	private boolean done;

	public CTSimpleSimulation(CTRunEnvironment runenv) {
		this.env = runenv;
		env.addListener(this);
	}

	@Override
	public void event(CTRuntimeEvent e) {
		events.add(e);
	}

	@Override
	public boolean run(int maxruntime) {
		log.info("Starting simulation maxruntime:" + maxruntime);
		start();

		ConditionWaiter.wait(() -> check(), maxruntime);
		log.info("Simulation stopping. Every things ok:" + allsuccess);
		stop();

		done = true;

		log.info("Simulation stopped. Every things ok:" + allsuccess);

		return allsuccess;
	}

	@Override
	public synchronized void taskFailed(CTRunEnvironment runenv, CTEnvironmentTask task) {
		log.info("task " + task + " failed in " + runenv);
		allsuccess = false;
	}

	private void stop() {
		log.info("Stopping");
		env.stop();
		runner.stop();
	}

	private void start() {
		runner = new CTStepRunner(MAX_STEP, MIN_STEP, dtime -> step(dtime));
		log.info("started " + runner);
	}

	private boolean step(double dtime) {
		env.step(dtime);
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
